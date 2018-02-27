package com.deplink.homegenius.Protocol.packet.ellisdk;

import android.content.Context;
import android.util.Log;

import com.deplink.homegenius.util.PublicMethod;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;



public class UdpPacket implements OnRecvListener {

    public static final String TAG = "UdpPacket";
    UdpComm netUdp;
    private OnRecvListener listener;
    //发送网络包队列
    public static ArrayList<BasicPacket> sendNetPakcetList;

    UdpPacketThread udpPacketThread;
    private Context mContext;

    public UdpPacket(Context context) {
        this.mContext = context;
        if (sendNetPakcetList == null) {
            sendNetPakcetList = new ArrayList<>();
        }
    }


    /**
     * 发送网络数据包
     *
     * @param packet
     * @return
     */
    public synchronized int writeNet(BasicPacket packet) {
        sendNetPakcetList.add(packet);
        return 0;
    }


    /**
     * 删除一个发送包
     *
     * @param list
     * @param packet
     * @return
     */
    private synchronized boolean delOneSendPacket(ArrayList<BasicPacket> list, BasicPacket packet) {
        list.remove(packet);
        return true;
    }


    public void start() {
        stop();
        netUdp = new UdpComm(mContext,  EllESDK_DEF.LocalConPort, this);
        netUdp.startServer();
        udpPacketThread = new UdpPacketThread();
        udpPacketThread.start();
    }


    public void stop() {
        if (udpPacketThread != null) {
            udpPacketThread.stopThis();
            udpPacketThread = null;
        }
        if (listener != null)
            listener = null;
        sendNetPakcetList.clear();
        if(netUdp!=null){
            netUdp.stopServer();
        }
    }

    public void restart() {
        start();
    }


    public void dealListener(BasicPacket packet) {
        int size = sendNetPakcetList.size();
        for (int i = 0; i < size; i++) {
            BasicPacket tmp = sendNetPakcetList.get(i);
            if (tmp.seq == packet.seq && tmp.mac == packet.mac && tmp.listener != null) {
                tmp.listener.OnRecvData(packet);
                tmp.isFinish = true;
                return;
            }
            size = sendNetPakcetList.size();
        }
    }

    @Override
    public void OnRecvData(BasicPacket packet) {
        DevStatus.statusDealPacket(packet);
        dealListener(packet);
        EllESDK.getNewPacket(packet);
    }

    private class UdpPacketThread extends Thread {

        boolean isRun;

        public void stopThis() {
            isRun = false;
        }

        @Override
        public void run() {
            super.run();
            isRun = true;
            while (isRun) {
                for (int i = 0; i < sendNetPakcetList.size(); i++) {
                    BasicPacket tmp = sendNetPakcetList.get(i);
                    if (tmp.isFinish) {
                        delOneSendPacket(sendNetPakcetList, tmp);
                        continue;
                    }
                    if (tmp != null) {
                        if (tmp.ip != null && tmp.isSetIp) {
                            Log.i(TAG,"udppacket 141 tmp data="+tmp.toString());
                            netUdp.sendData(tmp.getUdpData());
                            delOneSendPacket(sendNetPakcetList, tmp);
                        } else {
                            int result=tmp.isPacketCouldSend(PublicMethod.getTimeMs());
                            if (result==BasicPacket.PacketWait){
                                continue;
                            }
                            if (result==BasicPacket.PacketTimeout) {
                                if (tmp.listener != null) {
                                    delOneSendPacket(sendNetPakcetList, tmp);
                                }
                                i--;
                            }
                            OneDev dev = DevStatus.getOneDev(tmp.mac);
                            if (dev != null) {
                                switch (dev.getDevStatus(PublicMethod.getTimeMs())) {
                                    case OneDev.ConnTypeNULL:
                                        Log.d("TAG", "设备离线，回掉失败");
                                        if (tmp.listener != null)
                                            tmp.listener.OnRecvData(tmp);
                                        delOneSendPacket(sendNetPakcetList, tmp);
                                        break;
                                    case OneDev.ConnTypeLocal:
                                        try {
                                            tmp.ip = InetAddress.getByName("255.255.255.255");
                                            tmp.port =  EllESDK_DEF.LocalConPort;
                                            netUdp.sendData(tmp.getUdpData(tmp.ip, tmp.port));
                                        } catch (UnknownHostException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                }
                            } else {
                                if (tmp.isLocal) {
                                    try {
                                        tmp.ip = InetAddress.getByName("255.255.255.255");
                                        tmp.port = EllESDK_DEF.LocalConPort;
                                        Log.i(TAG,"udppacket 187");
                                        netUdp.sendData(tmp.getUdpData(tmp.ip, tmp.port));
                                    } catch (UnknownHostException e) {
                                        e.printStackTrace();
                                    }
                                }


                            }
                        }

                    }
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}

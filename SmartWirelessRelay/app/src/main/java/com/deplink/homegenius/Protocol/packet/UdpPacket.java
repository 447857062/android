package com.deplink.homegenius.Protocol.packet;

import android.content.Context;

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

    public static final int LocalConPort = 5880;
    public void start() {
        stop();
        netUdp = new UdpComm(mContext, LocalConPort, this);
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
    }

    public void restart() {
        start();
    }


    public void dealListener(BasicPacket packet) {
        int size = sendNetPakcetList.size();
        if (packet.xdata != null && packet.xdata.length > 0 && packet.xdata[0] == 0x04) {
        }
        for (int i = 0; i < size; i++) {
            BasicPacket tmp = sendNetPakcetList.get(i);
            /*if (tmp.seq == packet.seq && tmp.mac == packet.mac && tmp.listener != null) {
                tmp.listener.OnRecvData(packet);
                tmp.isFinish = true;
                return;
            }*/
            size = sendNetPakcetList.size();
        }
    }

    @Override
    public void OnRecvData(BasicPacket packet) {
      //  DevStatus.statusDealPacket(packet);
        dealListener(packet);
    //    EllESDK.getNewPacket(packet);
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
                 /*   if (tmp != null) {
                        if (tmp.ip != null && tmp.isSetIp) {
                            Log.i(TAG,"udppacket 141 tmp data="+tmp.toString());
                            netUdp.sendData(tmp.getUdpData());
                            delOneSendPacket(sendNetPakcetList, tmp);
                        } else {
                            int result=tmp.isPacketCouldSend(PublicMethod.getTimeMs());
                            if (result==PacketWait){
                                continue;
                            }
                            if (result==PacketTimeout) {
                                if (tmp.listener != null) {
                                    delOneSendPacket(sendNetPakcetList, tmp);
                                }
                                i--;
                            }
                            OneDev dev = DevStatus.getOneDev(tmp.mac);
                            if (dev != null) {
                                switch (dev.getDevStatus(PublicMethod.getTimeMs())) {
                                    case ConnTypeNULL:
                                        Log.d("TAG", "设备离线，回掉失败");
                                        if (tmp.listener != null)
                                            tmp.listener.OnRecvData(tmp);
                                        delOneSendPacket(sendNetPakcetList, tmp);
                                        break;
                                    case ConnTypeLocal:

                                        try {
                                            tmp.ip = InetAddress.getByName("255.255.255.255");
                                            tmp.port = LocalConPort;
                                            Log.i(TAG,"udppacket 170");
                                            netUdp.sendData(tmp.getUdpData(tmp.ip, tmp.port));
                                        } catch (UnknownHostException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case ConnTypeRemote:
                                        tmp.ip = dev.remoteIP;
                                        tmp.port = RemoteConPort;
                                        Log.i(TAG,"udppacket 179");
                                        netUdp.sendData(tmp.getUdpData(tmp.ip, tmp.port));
                                        break;
                                }
                            } else {
                                if (tmp.isLocal) {
                                    try {
                                        tmp.ip = InetAddress.getByName("255.255.255.255");
                                        tmp.port = LocalConPort;
                                        Log.i(TAG,"udppacket 187");
                                        netUdp.sendData(tmp.getUdpData(tmp.ip, tmp.port));
                                    } catch (UnknownHostException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    if (tmp.listener != null)
                                        tmp.listener.OnRecvData(tmp);
                                    delOneSendPacket(sendNetPakcetList, tmp);
                                }


                            }
                        }

                    }*/
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

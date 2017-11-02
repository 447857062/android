package deplink.com.smartwirelessrelay.homegenius.Protocol.UdpNet;

import android.content.Context;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import deplink.com.smartwirelessrelay.homegenius.Devices.EllESDK;
import deplink.com.smartwirelessrelay.homegenius.Protocol.BasicPacket;
import deplink.com.smartwirelessrelay.homegenius.Protocol.OnRecvListener;
import deplink.com.smartwirelessrelay.homegenius.util.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.util.PublicMethod;

import static deplink.com.smartwirelessrelay.homegenius.Protocol.BasicPacket.PacketTimeout;
import static deplink.com.smartwirelessrelay.homegenius.Protocol.BasicPacket.PacketWait;


public class UdpPacket implements OnRecvListener {

    public static final String TAG = "UdpPacket";
    private UdpComm netUdp;
    private OnRecvListener listener;
    //发送网络包队列
    public static ArrayList<BasicPacket> sendNetPakcetList;
    private UdpPacketThread udpPacketThread;
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
        netUdp = new UdpComm(mContext, AppConstant.UDP_CONNECT_PORT, this);
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
        //接收线程的数据回调
        dealListener(packet);
    }

    @Override
    public void OnRecvIp(byte[] ip) {
        EllESDK.getIp(ip);
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
                    if (tmp.ip != null && tmp.isSetIp) {
                        Log.i(TAG, "udppacket 141 tmp data=" + tmp.toString());
                        netUdp.sendData(tmp.getUdpData());
                        delOneSendPacket(sendNetPakcetList, tmp);
                    } else {
                        int result = tmp.isPacketCouldSend(PublicMethod.getTimeMs());
                        if (result == PacketWait) {
                            continue;
                        }
                        if (result == PacketTimeout) {
                            if (tmp.listener != null) {
                                delOneSendPacket(sendNetPakcetList, tmp);
                            }
                            i--;
                        }

                        if (tmp.isLocal) {
                            try {
                                tmp.ip = InetAddress.getByName("255.255.255.255");
                                tmp.port = AppConstant.UDP_CONNECT_PORT;
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
            }
        }
    }
}

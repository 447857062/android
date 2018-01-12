package com.deplink.homegenius.manager.connect.local.udp.packet;

import android.content.Context;
import android.util.Log;

import com.deplink.homegenius.Protocol.packet.BasicPacket;
import com.deplink.homegenius.manager.connect.local.udp.interfaces.OnGetIpListener;
import com.deplink.homegenius.manager.connect.local.udp.interfaces.OnRecvLocalConnectIpListener;

import java.util.ArrayList;

/**
 * udp发送广播包，探测本地连接的IP地址
 * 接收获取到的IP地址
 * 负责发送udp广播
 */
public class UdpPacket implements OnRecvLocalConnectIpListener {
    public static final String TAG = "UdpPacket";
    private UdpComm netUdp;
    //发送网络包队列
    public ArrayList<BasicPacket> sendNetPakcetList;
    private UdpPacketThread udpPacketThread;
    private OnGetIpListener mOnGetIpListener;

    public UdpPacket(Context context, OnGetIpListener listener) {
        this.mOnGetIpListener = listener;
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
     * 从udp发送数据队列里面删除一条数据
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
        netUdp = new UdpComm(this);
        netUdp.startServer();
        udpPacketThread = new UdpPacketThread();
        try {
            udpPacketThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (udpPacketThread != null) {
            udpPacketThread.stopThis();
            udpPacketThread = null;
        }
        sendNetPakcetList.clear();
    }
    @Override
    public void OnRecvIp(byte[] ip, String uid) {
        Log.i(TAG, "获取到连接的IP地址");
        mOnGetIpListener.onRecvLocalConnectIp(ip, uid);
    }

    private class UdpPacketThread extends Thread {
        boolean isRun;

        public void stopThis() {
            isRun = false;
        }

        @Override
        public void run() {
            super.run();
            Log.i(TAG, "UdpPacketThread is Run");
            isRun = true;
            while (isRun) {
                try {
                    for (int i = 0; i < sendNetPakcetList.size(); i++) {
                        BasicPacket tmp = sendNetPakcetList.get(i);
                        if (tmp.ip != null) {
                            netUdp.sendData(tmp.getUdpData());
                            delOneSendPacket(sendNetPakcetList, tmp);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

package com.deplink.homegenius.manager.connect.local.udp.packet;

import android.util.Log;

import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.manager.connect.local.udp.interfaces.OnRecvLocalConnectIpListener;
import com.deplink.homegenius.util.DataExchange;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;


/**
 * 通用UDP线程
 */
public class UdpComm {
    public static final String TAG = "UdpComm";
    private DatagramSocket udp = null;
    private OnRecvLocalConnectIpListener listener = null;
    private RecvThread recvThread = null;
    private boolean isRun = false;

    public boolean isRun() {
        return isRun;
    }

    public UdpComm(OnRecvLocalConnectIpListener listener) {
        this.listener = listener;
    }

    /**
     * 发送数据
     *
     * @param packet
     * @return
     */
    public boolean sendData(DatagramPacket packet) {
        if (udp == null)
            return false;
        byte[] temp = packet.getData();
        try {
            Log.i(TAG, "udp sendData success port" + ":" + packet.getPort());
            udp.send(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 启动服务
     */
    public int startServer() {
        if (isRun)
            return 0;
        if (udp == null) {
            try {
                udp = new DatagramSocket();
                recvThread = new RecvThread();
                recvThread.setPriority(Thread.NORM_PRIORITY);
                recvThread.start();
                isRun = true;
                return 1;
            } catch (SocketException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return 0;
    }

    /**
     * 停止服务
     */

    public int stopServer() {
        Log.i(TAG, "停止udp探测包 isRun=" + isRun);
        if (isRun) {
            isRun = false;
            if (recvThread != null) {
                recvThread.stopThis();
                recvThread = null;
            }
            if (udp != null) {
                udp.close();
                udp = null;
            }
            return 1;
        }
        return 0;
    }

    public class RecvThread extends Thread {
        boolean isRun = false;

        public void stopThis() {
            isRun = false;
        }

        /**
         * 接收线程
         */

        @Override
        public void run() {
            super.run();
            byte[] data = new byte[10240];
            isRun = true;
            DatagramPacket packet = new DatagramPacket(data, 0, data.length);
            packet.setPort(AppConstant.UDP_CONNECT_PORT);
            Log.i(TAG, "udp RecvThread 接收数据 run");
            while (isRun) {
                try {
                    udp.receive(packet);
                    int len = packet.getLength();
                    if (len > 0) {
                        byte[] result = new byte[len];
                        System.arraycopy(data, 0, result, 0, len);
                        Log.i(TAG, "udp RecvThread 接收数据 ip=" + Arrays.toString(packet.getAddress().getAddress()) + ":" + packet.getPort());
                        //获取设备的通讯IP地址，这个不能根据上面的packet.getAddress()获取的IP地址来
                        Log.i(TAG, "" + DataExchange.byteArrayToHexString(packet.getAddress().getAddress()));
                        byte[] uidResult = new byte[33];
                        System.arraycopy(result, 6, uidResult, 0, 33);
                        String uid=new String(uidResult);
                        Log.i(TAG, "网关SN:" +uid);
                        listener.OnRecvIp(packet.getAddress().getAddress(), uid);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            udp = null;
        }
    }
}

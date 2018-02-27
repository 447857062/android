package com.deplink.homegenius.Protocol.packet.ellisdk;

import android.content.Context;
import android.util.Log;

import com.deplink.homegenius.util.DataExchange;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


/**
 * 通用UDP线程
 */
public class UdpComm {

    public static final String TAG = "UdpComm";

    private int port = 5880;
    private DatagramSocket udp = null;
    private OnRecvListener listener = null;
    private RecvThread recvThread = null;
    private Context mContext;

    private boolean isRun = false;

    public UdpComm(Context context, int port, OnRecvListener listener) {
        this.mContext = context;
        this.port = port;
        this.listener = listener;
    }

    public UdpComm(OnRecvListener listener) {
        this.listener = listener;
    }

    public DatagramSocket getUdp() {
        return udp;
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
        try {
            Log.e(TAG, "sendData:" + packet.getAddress().getHostAddress() + ":" + packet.getPort());
          byte[]temp= packet.getData();

            Log.e(TAG, "sendData:" + DataExchange.byteArrayToHexString(temp));
            udp.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 返回端口
     *
     * @return
     */
    public int returnPort() {
        return port;
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
                port = udp.getLocalPort();
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
        if (isRun) {
            if (recvThread != null)
                recvThread.stopThis();
            recvThread = null;
            isRun = false;

            if (udp != null) {
                Log.d(TAG, "set udp null");
                udp.close();
                udp = null;
            }
            return 1;
        }
        return 0;
    }

    /**
     * 接收线程
     */
    public class RecvThread extends Thread {

        boolean isRun = false;

        public void stopThis() {
            isRun = false;
        }

        @Override
        public void run() {
            super.run();
            byte[] data = new byte[10240];
            isRun = true;
            DatagramPacket packet = new DatagramPacket(data, 0, data.length);
            packet.setPort(5880);
            while (isRun) {
                try {
                    udp.receive(packet);
                    int len = packet.getLength();
                    if (len > 0) {
                        byte[] result = new byte[len];

                        System.arraycopy(data, 0, result, 0, len);
                        BasicPacket basicPacket = new BasicPacket(mContext, packet.getAddress(), packet.getPort());
                        Log.i(TAG,"basicPacket.tostring="+basicPacket.toString());
                        basicPacket.unpackPacketWithData(result, result.length);
                        Log.d("RecvThread", DataExchange.byteArrayToHexString(result));
                        listener.OnRecvData(basicPacket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            udp = null;
        }
    }
}

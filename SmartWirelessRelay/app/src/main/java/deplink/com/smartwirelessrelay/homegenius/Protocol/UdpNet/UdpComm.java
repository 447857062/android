package deplink.com.smartwirelessrelay.homegenius.Protocol.UdpNet;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.BasicPacket;
import deplink.com.smartwirelessrelay.homegenius.Protocol.interfaces.OnRecvListener;
import deplink.com.smartwirelessrelay.homegenius.util.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.util.DataExchange;


/**
 * 通用UDP线程
 */
public class UdpComm {

    public static final String TAG = "UdpComm";
    private int port = AppConstant.UDP_CONNECT_PORT;
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
            Log.e(TAG, "udp sendData:" + packet.getAddress().getHostAddress() + ":" + packet.getPort());
            byte[] temp = packet.getData();
            Log.e(TAG, "udp sendData success:" + DataExchange.byteArrayToHexString(temp));
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
                port = udp.getLocalPort();
                recvThread = new RecvThread();
                recvThread.setPriority(Thread.NORM_PRIORITY);
                recvThread.start();
                isRun = true;
                return 1;
            } catch (SocketException e) {
                // TODO Auto-generated catch block
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
            if (recvThread != null){
                recvThread.stopThis();
                recvThread = null;
            }
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
            packet.setPort(AppConstant.UDP_CONNECT_PORT);
            while (isRun) {
                try {
                    udp.receive(packet);
                    int len = packet.getLength();
                    if (len > 0) {
                        byte[] result = new byte[len];
                        System.arraycopy(data, 0, result, 0, len);
                        BasicPacket basicPacket = new BasicPacket(mContext, packet.getAddress(), packet.getPort());
                        Log.i(TAG, "udp 接收数据 ip=" + packet.getAddress().toString() + ":" + packet.getPort());
                        //获取设备的通讯IP地址，这个不能根据上面的packet.getAddress()获取的IP地址来
                        //basicPacket.unpackPacketWithWirelessData(result);
                        listener.OnRecvIp(basicPacket.unpackPacketWithWirelessData(result));
                        stopServer();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            udp = null;
        }
    }
}

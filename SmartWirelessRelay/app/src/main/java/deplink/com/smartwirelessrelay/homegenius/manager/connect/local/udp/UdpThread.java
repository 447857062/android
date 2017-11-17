package deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp;

import android.content.Context;
import android.util.Log;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.GeneralPacket;
import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.udp.UdpPacket;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.util.NetStatusUtil;
import deplink.com.smartwirelessrelay.homegenius.util.PublicMethod;


/**
 * Created by benond on 2017/2/6.
 */

public class UdpThread {

    private static final String TAG = "UdpThread";
    Timer timer;
    public DatagramSocket dataSocket;
    public Context mContext;
    private UdpPacket udp;


    //TODO UID要不要传
    public UdpThread(Context context, UdpPacket udpPacket) {
        mContext = context;
        udp = udpPacket;
        try {
            dataSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    class timerTimeoutTask extends TimerTask {
        @Override
        public void run() {
            timerTimeout();
        }
    }

    public void timerTimeout() {
        //"设备状体定时器运行正常";
        //先判断当前的网络状态，没有网络则不执行设备的检查
        //当前如果是WiFi的话，则优先执行本地查找，确定本地没有设备后再执行远程查找
        boolean net = NetStatusUtil.isWiFiActive(mContext);
        //更新下本地的网络状态和IP地址
        PublicMethod.getLocalIP(mContext);
        if(net){
            wifiCheckHandler();
        }
    }

    public void wifiCheckHandler() {
        GeneralPacket packet;
        try {
            //发送一个局域网查询包
            packet = new GeneralPacket(InetAddress.getByName("255.255.255.255"), AppConstant.UDP_CONNECT_PORT, mContext);
            //查询设备，探测设备区别(这里探测设备就不需要发送查询的gson数据了)
            packet.packCheckPacketWithUID();
            Log.i(TAG, "wifiCheckHandler send udp packet");
            udp.writeNet(packet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


    }

    public void open() {
        timer = new Timer();
        timer.schedule(new timerTimeoutTask(), 1000, 5000);
    }
    public void cancel() {
        timer.cancel();
    }
}

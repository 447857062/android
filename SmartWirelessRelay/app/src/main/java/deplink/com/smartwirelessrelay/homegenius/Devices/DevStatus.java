package deplink.com.smartwirelessrelay.homegenius.Devices;

import android.content.Context;
import android.util.Log;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.udp.UdpPacket;
import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.GeneralPacket;
import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.manager.netStatus.NetStatuChangeReceiver;
import deplink.com.smartwirelessrelay.homegenius.util.PublicMethod;
import deplink.com.smartwirelessrelay.homegenius.util.SharedPreference;


/**
 * Created by benond on 2017/2/6.
 */

public class DevStatus implements NetStatuChangeReceiver.onNetStatuschangeListener{

    private static final String TAG = "DevStatus";
     Timer timer;
    public DatagramSocket dataSocket;
    public Context mContext;
    private UdpPacket udp;
    private int currentNetStatu;
    private NetStatuChangeReceiver mNetStatuChangeReceiver;
    public DevStatus(Context context, UdpPacket udpPacket) {
        mContext = context;
        udp = udpPacket;
        mNetStatuChangeReceiver=new NetStatuChangeReceiver();
        mNetStatuChangeReceiver.setmOnNetStatuschangeListener(this);
        try {
            dataSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNetStatuChange(int netStatu) {
        currentNetStatu=netStatu;
    }

    class timerTimeoutTask extends TimerTask {

        @Override
        public void run() {
            timerTimeout();
        }
    }

    public void timerTimeout() {
        //NSLog(@"设备状体定时器运行正常");
        //先判断当前的网络状态，没有网络则不执行设备的检查
        //当前如果是WiFi的话，则优先执行本地查找，确定本地没有设备后再执行远程查找
        int net = PublicMethod.checkConnectionState(mContext);
        //更新下本地的网络状态和IP地址
        PublicMethod.getLocalIP(mContext);
        switch (currentNetStatu) {
            case NetStatuChangeReceiver.NET_TYPE_WIFI:
                //WiFi模式
                //调试,不打开探测
                wifiCheckHandler();
                break;
            default:s
                break;
        }
    }
    public void wifiCheckHandler() {
        GeneralPacket packet = null;
        try {
            //发送一个局域网查询包
            packet = new GeneralPacket(InetAddress.getByName("255.255.255.255"), AppConstant.UDP_CONNECT_PORT, mContext);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //查询设备，探测设备区别(这里探测设备就不需要发送查询的gson数据了)
        packet.packCheckPacketWithUID( null,null);
        //uid
        SharedPreference sharedPreference = new SharedPreference(mContext, "uid");
        String uid = sharedPreference.getString("uid");
        //已收到uid
        Log.i(TAG, "wifiCheckHandler send udp packet");
        if(uid!=null && !uid.equals("")){
            byte[]temp=uid.getBytes();
            Log.i(TAG,"uid="+uid+"长度="+temp.length);
            udp.writeNet(packet);
        }else{
            udp.writeNet(packet);
        }
    }
    public void open() {
        timer = new Timer();
        timer.schedule(new timerTimeoutTask(),1000, 5000);
    }
}

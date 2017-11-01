package deplink.com.smartwirelessrelay.homegenius.Devices;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import deplink.com.smartwirelessrelay.homegenius.Protocol.GeneralPacket;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.QueryOptions;
import deplink.com.smartwirelessrelay.homegenius.util.PublicMethod;
import deplink.com.smartwirelessrelay.homegenius.util.SharedPreference;
import deplink.com.smartwirelessrelay.homegenius.Protocol.UdpNet.UdpPacket;


/**
 * Created by benond on 2017/2/6.
 */

public class DevStatus {

    private static final String TAG = "DevStatus";
    static Timer timer;
    static InetAddress regServerIP;
    static boolean couldSearch;

    public static List<String> ssids;
    int curNetWork;
    public DatagramSocket dataSocket;
    public String RegServer = "devreg1.elletechnology.com";
    public Context mContext;
    private UdpPacket udp;

    public DevStatus(Context context, UdpPacket udpPacket) {
        mContext = context;
        udp = udpPacket;
        try {
            dataSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void getRegServerIP() {
        if (regServerIP == null) {
            try {
                regServerIP = InetAddress.getByName(RegServer);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            if (regServerIP != null) {
                Log.d(TAG, "得到注册服务器IP:%@" + regServerIP);
            }
        }
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
        //当前如果是流量模式的话，则只执行远程查询
        int net = PublicMethod.checkConnectionState(mContext);
        if (net != -1 && PublicMethod.isNetworkOnline() && net != 3) {
            getRegServerIP();

//            if (curNetWork != net) {
//                curNetWork = net;
//                udp.restart();
//            }
        }

        //更新下本地的网络状态和IP地址
        PublicMethod.getLocalIP(mContext);
        switch (net) {
            case -1:
                return;
            case 2:

                break;
            case 1:         //WiFi模式
            case 3:         //WiFi模式
                //调试,不打开探测
                //wifiCheckHandler();
                break;
            default:
                break;
        }
    }
    public void wifiCheckHandler() {

        Log.i(TAG, "wifiCheckHandler couldSearch=" + couldSearch);

        GeneralPacket packet = null;
        try {
            //发送一个局域网查询包
            packet = new GeneralPacket(InetAddress.getByName("255.255.255.255"),8999, mContext);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //探测设备
        QueryOptions queryCmd=new QueryOptions();
        queryCmd.setOP("QUERY");
        queryCmd.setMethod("DevList");
        Gson gson=new Gson();
        String text = gson.toJson(queryCmd);
        //查询设备，探测设备区别(这里探测设备就不需要发送查询的gson数据了)
        Log.i(TAG,"  packet.packCheckPacketWithUID( null,/*text.getBytes()*/null)");
        packet.packCheckPacketWithUID( null,/*text.getBytes()*/null);
        //uid
        SharedPreference sharedPreference = new SharedPreference(mContext, "uid");
        String uid = sharedPreference.getString("uid");
        //已收到uid
        if(uid!=null && !uid.equals("")){
            byte[]temp=uid.getBytes();
            Log.i(TAG,"uid="+uid+"长度="+temp.length);
            udp.writeNet(packet);
        }else{
            udp.writeNet(packet);
        }
    }



    public void open() {
        curNetWork = PublicMethod.checkConnectionState(mContext);
        couldSearch = false;
        ssids = new ArrayList<>();
        regServerIP = null;
        timer = new Timer();
        timer.schedule(new timerTimeoutTask(),1000, 5000);

    }
}

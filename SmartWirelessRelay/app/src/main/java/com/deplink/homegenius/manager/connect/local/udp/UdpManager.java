package com.deplink.homegenius.manager.connect.local.udp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.deplink.homegenius.manager.connect.local.udp.interfaces.OnGetIpListener;
import com.deplink.homegenius.manager.connect.local.udp.interfaces.UdpManagerGetIPLintener;
import com.deplink.homegenius.manager.connect.local.udp.packet.UdpPacket;
import com.deplink.homegenius.util.IPV4Util;
import com.deplink.homegenius.util.NetUtil;
import com.deplink.homegenius.util.WeakRefHandler;

/**
 * Created by Administrator on 2017/11/7.
 * 发送udp探测包
 * 获取本地连接的ip地址
 * 使用：
 * UdpManager manager=UdpManager.getInstance();
 * manager.InitUdpConnect(this);
 */
public class UdpManager implements OnGetIpListener {
    private static final String TAG = "UdpManager";
    /**
     * 这个类设计成单例
     */
    private static UdpManager instance;
    private UdpPacket udpPacket;
    private UdpThread udpThread;
    private UdpManagerGetIPLintener mUdpManagerGetIPLintener;
    private UdpManager() {
    }

    public static synchronized UdpManager getInstance() {
        if (instance == null) {
            instance = new UdpManager();
        }
        return instance;
    }
    /**
     * 初始化本地连接管理器
     */
    public int InitUdpConnect(final Context context, UdpManagerGetIPLintener listener) {
        this.mUdpManagerGetIPLintener = listener;
        if (listener == null) {
            Log.e(TAG, "InitUdpConnect 没有设置回调 SDK 会出现异常,这里必须设置数据结果回调");
        }
        //接受udp探测设备数据
        if (udpPacket == null) {
            udpPacket = new UdpPacket(context, this);
        }
        udpPacket.start();
        Message msg = Message.obtain();
        msg.what = MSG_STOP_CHECK_GETWAY;
        mHandler.sendMessageDelayed(msg, MSG_STOP_CHECK_GETWAY_DELAY);
        //启动状态查询任务，连续发送udp探测设备
        if (udpThread == null) {
            udpThread = new UdpThread(context, udpPacket);
        }
        udpThread.open();
        return 0;
    }
    /**
     * 检查到网关后还要继续检查把收到的网关列一个表
     *
     * @param packet
     */
    @Override
    public void onRecvLocalConnectIp(byte[] packet, String uid) {
        Log.i(TAG, "onRecvLocalConnectIp ip=" + IPV4Util.trans2IpV4Str(packet));
        //不用发送，可以接收udp
        if (udpThread != null) {
            udpThread.cancel();
        }

        mUdpManagerGetIPLintener.onGetLocalConnectIp(IPV4Util.trans2IpV4Str(packet), uid);
    }

    private static final int MSG_STOP_CHECK_GETWAY = 100;
    /**
     * 无论有没有探测到网关，探测时间都设置为10秒
     */
    private static final int MSG_STOP_CHECK_GETWAY_DELAY = 10000;
    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_STOP_CHECK_GETWAY:
                    udpPacket.stop();
                    udpThread.cancel();
                    break;

            }
            return true;
        }
    };
    private Handler mHandler = new WeakRefHandler(mCallback);
    public void registerNetBroadcast(Context conext) {
        //注册网络状态监听
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        conext.registerReceiver(broadCast, filter);
    }

    public void unRegisterNetBroadcast(Context conext) {
        conext.unregisterReceiver(broadCast);
    }

    /**
     * 当前的网络情况
     */
    private int currentNetStatu = 4;
    public static final int NET_TYPE_WIFI_CONNECTED = 0;
    /**
     * WIFI不可用
     */
    public static final int NET_TYPE_WIFI_DISCONNECTED = 4;
    private NetBroadCast broadCast = new NetBroadCast();

    class NetBroadCast extends BroadcastReceiver {
        public final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION.equals(intent.getAction())) {
                Log.i(TAG, "网络连接变化");
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeInfo = manager.getActiveNetworkInfo();
                if (activeInfo != null && NetUtil.isWiFiActive(context)) {
                    if (NetUtil.isWiFiActive(context)) {
                        currentNetStatu = NET_TYPE_WIFI_CONNECTED;
                    } else {
                        currentNetStatu = NET_TYPE_WIFI_DISCONNECTED;
                    }
                    Log.i(TAG, "网络连接变化 currentNetStatu=" + currentNetStatu + "udpPacket!=null" + (udpPacket != null));
                    if (currentNetStatu == NET_TYPE_WIFI_CONNECTED) {
                        //重新连接
                        if (udpPacket != null) {
                            udpPacket.start();
                            udpThread.open();
                            Message msg = Message.obtain();
                            msg.what = MSG_STOP_CHECK_GETWAY;
                            mHandler.sendMessageDelayed(msg, MSG_STOP_CHECK_GETWAY_DELAY);

                        }
                    } else if (currentNetStatu == NET_TYPE_WIFI_DISCONNECTED) {
                        //wifi连接不可用
                        if (udpPacket != null) {
                            udpPacket.stop();
                            udpThread.cancel();
                        }
                    }
                }
            }
        }
    }
}

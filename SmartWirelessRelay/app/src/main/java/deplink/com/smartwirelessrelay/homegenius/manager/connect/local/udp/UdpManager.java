package deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.udp.UdpPacket;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp.interfaces.OnGetIpListener;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp.interfaces.UdpManagerGetIPLintener;
import deplink.com.smartwirelessrelay.homegenius.manager.netStatus.NetStatuChangeReceiver;
import deplink.com.smartwirelessrelay.homegenius.util.IPV4Util;

/**
 * Created by Administrator on 2017/11/7.
 * 发送udp探测包
 * 获取本地连接的ip地址
 * 使用：
 * UdpManager manager=UdpManager.getInstance();
 * manager.InitUdpConnect(this);
 */
public class UdpManager implements OnGetIpListener, NetStatuChangeReceiver.onNetStatuschangeListener {
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

    private Context mContext;

    /**
     * 初始化本地连接管理器
     */
    public int InitUdpConnect(Context context, UdpManagerGetIPLintener listener) {
        this.mUdpManagerGetIPLintener = listener;
        this.mContext = context;
        if (listener == null) {
            Log.e(TAG, "InitUdpConnect 没有设置回调 SDK 会出现异常,这里必须设置数据结果回调");
        }
        initRegisterNetChangeReceive();
        //接受udp探测设备数据
        if (udpPacket == null) {
            udpPacket = new UdpPacket(context, this);
        }
        udpPacket.start();

        //启动状态查询任务，连续发送udp探测设备
        if (udpThread == null) {
            udpThread = new UdpThread(context, udpPacket);
        }
        udpThread.open();
        return 0;
    }

    private NetStatuChangeReceiver mNetStatuChangeReceiver;

    /**
     * 初始化网络连接广播
     */
    private void initRegisterNetChangeReceive() {
        if (mNetStatuChangeReceiver == null) {
            mNetStatuChangeReceiver = new NetStatuChangeReceiver();
            mNetStatuChangeReceiver.setmOnNetStatuschangeListener(this);
            IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            mContext.registerReceiver(mNetStatuChangeReceiver, filter);
        }

    }
    //TODO 添加网络状态，连接状态反馈
    private void unRegisterNetChangeReceive() {
        mContext.unregisterReceiver(mNetStatuChangeReceiver);
    }

    @Override
    public void onRecvLocalConnectIp(byte[] packet) {
        Log.i(TAG, "onRecvLocalConnectIp ip=" + IPV4Util.trans2IpV4Str(packet));
        udpPacket.stop();
        mUdpManagerGetIPLintener.onGetLocalConnectIp(IPV4Util.trans2IpV4Str(packet));
    }

    @Override
    public void onNetStatuChange(int netStatu) {
      if (netStatu != NetStatuChangeReceiver.NET_TYPE_WIFI_CONNECTED) {
            //wifi连接不可用
            if (udpPacket != null) {
                udpPacket.stop();
            }
        } else {
            //重新连接
            if (udpPacket != null) {
                udpPacket.start();
            }

        }
    }
}

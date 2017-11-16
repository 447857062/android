package deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp;

import android.content.Context;
import android.util.Log;

import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.udp.UdpPacket;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp.interfaces.OnGetIpListener;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp.interfaces.UdpManagerGetIPLintener;
import deplink.com.smartwirelessrelay.homegenius.util.IPV4Util;

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
    private UdpThread devStatus;
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
    public int InitUdpConnect(Context context, UdpManagerGetIPLintener listener) {
        this.mUdpManagerGetIPLintener = listener;
        if (listener == null) {
            Log.e(TAG, "InitUdpConnect 没有设置回调 SDK 会出现异常,这里必须设置数据结果回调");
        }
        //发送任务队列
        if (udpPacket == null) {
            udpPacket = new UdpPacket(this);
        }
        udpPacket.start();

        //启动状态查询任务
        if (devStatus == null) {
            devStatus = new UdpThread(context, udpPacket);
        }
        devStatus.open();
        return 0;
    }

    @Override
    public void onRecvLocalConnectIp(byte[] packet) {
        Log.i(TAG, "onRecvLocalConnectIp ip=" + IPV4Util.trans2IpV4Str(packet));
        udpPacket.stop();
        mUdpManagerGetIPLintener.onGetLocalConnectIp(IPV4Util.trans2IpV4Str(packet));
    }
}

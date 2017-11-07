package deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp;

import android.content.Context;
import android.util.Log;

import deplink.com.smartwirelessrelay.homegenius.Devices.DevStatus;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.udp.interfaces.OnGetIpListener;
import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.udp.UdpPacket;
import deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp.LocalConnecteDataListener;

/**
 * Created by Administrator on 2017/11/7.
 * 发送udp探测包
 * 获取本地连接的ip地址
 */
public class UdpManager implements OnGetIpListener{
    private static final String TAG = "UdpManager";
    /**
     * 这个类设计成单例
     */
    private static UdpManager instance;
    private Context mContext;
    private UdpPacket udpPacket;
    private DevStatus devStatus;
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
    public int InitUdpConnect(Context context, LocalConnecteDataListener listener) {
        this.mContext = context;
        if (listener == null) {
            Log.e(TAG, "没有设置回调 SDK 会出现异常,这里必须设置数据结果回调");
        }
        //发送任务队列
        if (udpPacket == null) {
            udpPacket = new UdpPacket(context,this);
            udpPacket.start();
        }
        //启动状态查询任务
        if (devStatus == null) {
            devStatus = new DevStatus(context, udpPacket);
            devStatus.open();
        }
        return 0;
    }

    @Override
    public void onRecvLocalConnectIp(byte[] packet) {
        //TODO

    }
}

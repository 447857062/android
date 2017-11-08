package deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp;

/**
 * Created by Administrator on 2017/11/7.
 */
public interface LocalConnecteListener {
    /**
     * 接收本地连接数据包
     * @param packet
     */
    void onReciveLocalConnectePacket(byte[] packet);

    /**
     * sslsocket握手成功
     */
    void handshakeCompleted();
    /**
     * sslsocket握手失败
     */
    void createSocketFailed(String msg);
    /**
     * 获取本地网关失败
     */
    void OnFailedgetLocalGW(String msg);
    /**
     * 获取uid
     */
    void OnGetUid(String uid);
    /**
     * 获取查询结果
     */
    void OnGetQueryresult(String devList);
    /**
     * 获取设置结果
     */
    void OnGetSetresult(String setResult);

    /**
     * WiFi连接不可用
     */
    void wifiConnectUnReachable();
}

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
}

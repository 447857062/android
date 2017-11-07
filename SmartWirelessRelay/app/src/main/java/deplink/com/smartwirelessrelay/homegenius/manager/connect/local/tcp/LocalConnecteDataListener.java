package deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp;

/**
 * Created by Administrator on 2017/11/7.
 */
public interface LocalConnecteDataListener {
    /**
     * 接收本地连接数据包
     * @param packet
     */
    void onReciveLocalConnectePacket(byte[] packet);

    /**
     * sslsocket握手成功
     */
    void handshakeCompleted();
}

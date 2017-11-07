package deplink.com.smartwirelessrelay.homegenius.constant;

/**
 * Created by Administrator on 2017/10/31.
 */
public class AppConstant {
    /**
     * 数据包基本长度，不带数据
     */
    public static final int BASICLEGTH = 84;
    /**
     * 携带数据的包，表示数据长度的2个字节,在数据包的字节开始的位置
     */
    public static final int PACKET_DATA_LENGTH_START_INDEX = 78;
    /**
     * 表示数据长度的长度字符占用几个字节
     */
    public static final int PACKET_DATA_LENGTHS_TAKES = 2;

    public static final int UDP_CONNECT_PORT = 17999;
    public static final int TCP_CONNECT_PORT = 19999;
    public static final String PASSWORD_FOR_PKCS12 = "1234567890";
    public static final String SERVER_IP = "192.168.68.1";//TCP连接IP
    // public static final  String SERVER_IP="192.168.2.210";//TCP连接IP
    public static final int SERVER_CONNECT_TIMEOUT = 15000;//TCP连接超时设置
    public static final int SERVER_HEARTH_BREATH = 4000;//心跳包时间间隔
    /**
     * 本地连接socket 输入输出流的超时
     */
    public static final int LOCAL_SERVER_SOCKET_TIMEOUT = 5000;
}

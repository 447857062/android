package deplink.com.smartwirelessrelay.homegenius.constant;

/**
 * Created by Administrator on 2017/10/31.
 */
public class AppConstant {
    public static final String USER_LOGIN="logged";
    public interface DEVICE{
        public static String CURRENT_DEVICE_KEY = "CURRENT_DEVICE_KEY";
        public static String CURRENT_DEVICE_SN = "CURRENT_DEVICE_SN";
    }
    /**
     * wfii设置
     */
    public static interface WIFISETTING{
        public static final String WIFI_TYPE="wifiType";
        public static final String WIFI_TYPE_2G="wifiType2G";
        public static final String WIFI_TYPE_VISITOR="wifiTypeVisitor";


        public static final String WIFI_ENCRYPT_TYPE="EncryptType";
        public static final String WIFI_MODE_TYPE="WIFI_MODE";
        public static final String WIFI_CHANNEL_TYPE="CHANNEL";
        public static final String WIFI_BANDWIDTH="BANDWIDTH";
        public static final String WIFI_NAME="wifiname";
        public static final String WIFI_PASSWORD="wifiPassword";
        interface  WIFISETTING_CUSTOM{
            public static final String WIFI_ENCRYPT_TYPE_CUSTOM="EncryptType_CUSTOM";
        }
    }
    /**
     * 操作入口，是本地操作，还是websocket
     */
    public static final String OPERATION_TYPE="op_type";
    public static final String OPERATION_TYPE_LOCAL="op_local";
    public static final String OPERATION_TYPE_WEB="op_web";
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

   public static final int UDP_CONNECT_PORT = 9999;
    public static final int TCP_CONNECT_PORT = 9988;

  /*  public static final int UDP_CONNECT_PORT = 17999;
    public static final int TCP_CONNECT_PORT = 19999;*/

    public static final String PASSWORD_FOR_PKCS12 = "1234567890";
    public static final String SERVER_IP = "192.168.68.1";//TCP连接IP
    // public static final  String SERVER_IP="192.168.2.210";//TCP连接IP
    public static final int SERVER_CONNECT_TIMEOUT = 15000;//TCP连接超时设置
    public static final int SERVER_HEARTH_BREATH = 4000;//心跳包时间间隔
    /**
     * 本地连接socket 输入输出流的超时
     */
    public static final int LOCAL_SERVER_SOCKET_TIMEOUT = 5000;
    public static final String  BIND_APP_MAC = "77685180654101946200316696479445";
}

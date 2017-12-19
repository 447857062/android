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
     * websocket返回的错误码
     */
    public static interface ERROR_CODE{
        public static final String OP_ERRCODE_BAD_REQUEST="100001";
        public static final String OP_ERRCODE_BAD_TOKEN="100002";
        public static final String OP_ERRCODE_BAD_ACCOUNT="100003";
        public static final String OP_ERRCODE_LOGIN_FAIL="100004";
        public static final String OP_ERRCODE_NOT_FOUND="100005";
        public static final String OP_ERRCODE_NO_AUTHORITY="100006";
        public static final String OP_ERRCODE_LOGIN_FAIL_MAX="100007";
        public static final String OP_ERRCODE_CAPTCHA_INCORRECT="100008";
        public static final String OP_ERRCODE_PASSWORD_INCORRECT="100009";
        public static final String OP_ERRCODE_PASSWORD_SHORT="100010";
        public static final String OP_ERRCODE_ALREADY_EXIST="100011";
        public static final String OP_ERRCODE_NO_PACKAGE="100012";
        public static final String OP_ERRCODE_NO_DEVICE="100013";
        public static final String OP_ERRCODE_BAD_ACCOUNT_INFO="100014";
        public static final String OP_ERRCODE_TARGET_ADDRESS_INVALID="100015";
        public static final String OP_ERRCODE_DB_TRANSACTION_ERROR="100016";
        public static final String OP_ERRCODE_ALREADY_DELIVERD="100017";
        public static final String OP_ERRCODE_ALREADY_RETURNED="100018";
        public static final String OP_ERRCODE_UN_RECEIVE ="100019";
    };
    /**
     * websocket返回的错误码解释
     */
    public static interface ERROR_MSG{
        public static final String OP_ERRCODE_BAD_REQUEST="发送的请求未按要求填写参数";
        public static final String OP_ERRCODE_BAD_TOKEN="token无效或已过期，需重新登录";
        public static final String OP_ERRCODE_BAD_ACCOUNT="账号未激活";
        public static final String OP_ERRCODE_LOGIN_FAIL="用户名或密码错";
        public static final String OP_ERRCODE_NOT_FOUND="未找到所请求的资源";
        public static final String OP_ERRCODE_NO_AUTHORITY="没有权限操作";
        public static final String OP_ERRCODE_LOGIN_FAIL_MAX="密码错误满5次，账号被锁定，需管理员解锁";
        public static final String OP_ERRCODE_CAPTCHA_INCORRECT="验证码错误";
        public static final String OP_ERRCODE_PASSWORD_INCORRECT="原密码错误";
        public static final String OP_ERRCODE_PASSWORD_SHORT="密码长度小于6位";
        public static final String OP_ERRCODE_ALREADY_EXIST="所创建的资源已存在";
        public static final String OP_ERRCODE_NO_PACKAGE="没有可操作的package";
        public static final String OP_ERRCODE_NO_DEVICE="没有可操作的设备";
        public static final String OP_ERRCODE_BAD_ACCOUNT_INFO="用户账户信息异常，需管理员处理";
        public static final String OP_ERRCODE_TARGET_ADDRESS_INVALID="无效的地址";
        public static final String OP_ERRCODE_DB_TRANSACTION_ERROR="数据库异常";
        public static final String OP_ERRCODE_ALREADY_DELIVERD="设备已经被领用";
        public static final String OP_ERRCODE_ALREADY_RETURNED="设备已经被归还";
        public static final String OP_ERRCODE_UN_RECEIVE="设备尚未领用";
    };

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

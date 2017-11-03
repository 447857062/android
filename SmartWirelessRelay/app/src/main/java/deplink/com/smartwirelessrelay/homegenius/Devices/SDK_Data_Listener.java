package deplink.com.smartwirelessrelay.homegenius.Devices;


/**
 * Created by benond on 2017/2/7.
 * sdk返回数据给界面的接口
 */

public interface SDK_Data_Listener {
    void onRecvDeviceIp(byte[] packet);
    void onConnectDeviceFail(String ipaddress);
}

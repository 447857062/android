package deplink.com.smartwirelessrelay.homegenius.Devices;


/**
 * Created by benond on 2017/2/7.
 */

public interface EllE_Listener {

    void onRecvDeviceIp(byte[] packet);
    void onConnectDeviceFail(String ipaddress);
}

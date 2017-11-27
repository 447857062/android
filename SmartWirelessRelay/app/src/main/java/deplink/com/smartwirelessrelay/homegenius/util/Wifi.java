package deplink.com.smartwirelessrelay.homegenius.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by Administrator on 2017/9/4.
 */
public class Wifi {
    public static String getConnectedWifiName(Context context) {
        WifiManager wifiManager = (WifiManager)context. getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.d("wifiInfo", wifiInfo.toString());
        Log.d("SSID", wifiInfo.getSSID());
        String ssid = wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1);
        return ssid;
    }
}

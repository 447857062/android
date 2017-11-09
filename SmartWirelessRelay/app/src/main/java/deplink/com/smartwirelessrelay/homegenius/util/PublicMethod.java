package deplink.com.smartwirelessrelay.homegenius.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 */

public class PublicMethod {

    public static long getTimeMs() {
        return System.currentTimeMillis();
    }


    public static String getSSID(Context ctx) {
        WifiManager wifiMgr = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        return info != null ? info.getSSID() : null;
    }


    public static String getIPAddress(Context ctx) {
        WifiManager wifiMan = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMan.getConnectionInfo();
        String mac = info.getMacAddress();// 获得本机的MAC地址
        String ssid = info.getSSID();// 获得本机所链接的WIFI名称
        int ipAddress = info.getIpAddress();
        String ipString = "";// 本机在WIFI状态下路由分配给的IP地址
        if (ipAddress != 0) {
            return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                    + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));

        }
        return null;
    }


    public static String getLocalIP(Context ctx) {
        if (NetStatusUtil.checkConnectionState(ctx) == 1) {

            return PublicMethod.getIPAddress(ctx);
        }
        return null;
    }

    public static String getImei(Context context) {
        TelephonyManager mTm = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        return mTm.getDeviceId();
    }


    public static byte[] getUuid(Context context) {
        byte[] tmp = new byte[4];
        String m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
        String m_szLongID = m_szAndroidID + m_szWLANMAC;
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (m != null) {
            m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
            byte p_md5Data[] = m.digest();
            for (int i = 0; i < 4; i++) {
                tmp[i] = p_md5Data[i];
            }
        }
        return tmp;
    }

}


package deplink.com.smartwirelessrelay.homegenius.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by Administrator on 2017/11/2.
 */
public class NetStatusUtil {
    private static final String TAG="NetStatusUtil";
    public static boolean isWiFiActive(Context inContext) {
        Context context = inContext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equalsIgnoreCase("WIFI") && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static boolean isNetAvailable(Context context) {
        boolean available = false;
        //获取手机的连接服务管理器，这里是连接管理器类
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = cm.getAllNetworks();
            for (Network network : networks) {
                NetworkInfo networkInfo = cm.getNetworkInfo(network);
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED
                        && ((networkInfo.getType() == ConnectivityManager.TYPE_WIFI && isWiFiActive(context)) || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
                    available = true;
                    break;
                }
            }
        } else {
            NetworkInfo.State wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            NetworkInfo.State mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            available = ((wifiState == NetworkInfo.State.CONNECTED && isWiFiActive(context)) || mobileState == NetworkInfo.State.CONNECTED);
        }

        return available ;
    }

    /**
     * 开关wifi,当wifi开的时候手机的数据会被关掉，返回手机网络无数据连接
     * 关闭wifi只有手机网络，手机数据启用,返回当前网络为手机可用
     * 当开启wifi，开关手机数据开关不会发送网络状态改变广播
     * @param context
     * @return
     */
    public static boolean isNetTypePhoneAvailable(Context context) {
        boolean available = false;
        //获取手机的连接服务管理器，这里是连接管理器类
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = cm.getAllNetworks();
            for (Network network : networks) {
                NetworkInfo networkInfo = cm.getNetworkInfo(network);
                Log.i(TAG,"networkInfo="+networkInfo.toString());
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    available = true;
                    break;
                }
            }
        } else {
            NetworkInfo.State mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            available = (mobileState == NetworkInfo.State.CONNECTED);
        }
        return available ;
    }
    public static boolean isNetworkOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("ping -c 1 114.114.114.114");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检查网络状态
     */
    public static int checkConnectionState(Context ctx) {
        ConnectivityManager mConnectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager mTelephony = (TelephonyManager) ctx.getSystemService(TELEPHONY_SERVICE);

    /* 检查有没有网络 */
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            return -1;
        }
    /* 判断网络连接类型,只有在3G 或 wifi 里进行一些数据更新。 */
        int netType = info.getType();
        int netSubtype = info.getSubtype();
        if (netType == ConnectivityManager.TYPE_WIFI) {
            netType = 1;
        } else if (netType == ConnectivityManager.TYPE_MOBILE && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS && !mTelephony.isNetworkRoaming()) {
            return 2;
        } else {
            return 0;
        }
        return 1;
    }
}

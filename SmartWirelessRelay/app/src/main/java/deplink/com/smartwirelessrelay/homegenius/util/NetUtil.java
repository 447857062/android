package deplink.com.smartwirelessrelay.homegenius.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * Created by Administrator on 2017/8/15.
 * 网络操作工具集
 */
public class NetUtil {

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
}

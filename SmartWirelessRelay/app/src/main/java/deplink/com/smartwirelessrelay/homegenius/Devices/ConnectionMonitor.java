package deplink.com.smartwirelessrelay.homegenius.Devices;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.GeneralPacket;
import deplink.com.smartwirelessrelay.homegenius.util.AppConstant;
import deplink.com.smartwirelessrelay.homegenius.util.SharedPreference;

/**
 * Created by Administrator on 2017/11/1.
 */
public class ConnectionMonitor {
    private static final String TAG = "ConnectionMonitor";
    private Timer monitorTimer = null;
    private TimerTask monitorTask = null;
    private Context mContext;
    private GeneralPacket packet;
    private boolean isServerClose;
    public ConnectionMonitor(Context context) {
        this.mContext = context;
        packet = new GeneralPacket(mContext);
    }

    public void startTimer() {
        if (monitorTimer == null) {
            monitorTimer = new Timer();
        }
        if (monitorTask == null) {
            monitorTask = new TimerTask() {
                @Override
                public void run() {
                    SharedPreference sharedPreference = new SharedPreference(mContext, "heathswitch");
                    String open = sharedPreference.getString("heathswitch");
                    Log.i(TAG, "发送心跳包开关 open=" + open);
                    if (open != null && open.equals("open")) {
                        checkConnectionHealth();
                    }
                }
            };
        }
        if (monitorTimer != null) {
            monitorTimer.schedule(monitorTask, 1000, AppConstant.SERVER_HEARTH_BREATH);
        }
        Log.d(TAG, "===>monitor started");
    }

    public void stopTimer() {
        if (monitorTimer != null) {
            monitorTimer.cancel();
            monitorTimer = null;
        }
        if (monitorTask != null) {
            monitorTask.cancel();
            monitorTask = null;
        }
        Log.d(TAG, "===>monitor stopped");
    }



    /**
     * 检查
     */
    public void checkConnectionHealth() {
        Log.i(TAG, "checkConnectionHealth" + isNetworkAvailable(mContext));
        if (isNetworkAvailable(mContext)) {
            isServerClose = isServerClose();
            Log.i(TAG, "===>check connect isServerClose=" + isServerClose);
        }

    }



    /**
     * 判断是否断开连接，断开返回true,没有返回false
     *
     * @return
     */
    public Boolean isServerClose() {
        try {
            packet.packHeathPacket();
            int clientStatus = ConnectManager.getInstance().getOut(packet.data);
            //  socket.sendUrgentData(0);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            Log.i(TAG, "clientStatus=" + clientStatus);
            if (clientStatus == -1) {
                return true;
            }
            return false;
        } catch (Exception se) {
            Log.i(TAG, "断开连接");
            return true;
        }
    }

    private boolean isNetworkAvailable(Context context) {
        boolean available = false;
        //获取手机的连接服务管理器，这里是连接管理器类
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = cm.getAllNetworks();
            for (Network network : networks) {
                NetworkInfo networkInfo = cm.getNetworkInfo(network);
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED
                        && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
                    available = true;
                    break;
                }
            }
        } else {
            NetworkInfo.State wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            NetworkInfo.State mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            available = (wifiState == NetworkInfo.State.CONNECTED || mobileState == NetworkInfo.State.CONNECTED);
        }

        return available;
    }

}

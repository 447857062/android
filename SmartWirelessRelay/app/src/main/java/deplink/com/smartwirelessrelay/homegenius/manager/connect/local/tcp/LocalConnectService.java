package deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import deplink.com.smartwirelessrelay.homegenius.constant.AppConstant;

public class LocalConnectService extends Service {
    public static final String TAG = "LocalConnectService";
    public LocalConnectmanager connectmanager=LocalConnectmanager.getInstance();
    public LocalConnectService() {

    }
    @Override
    public IBinder onBind(Intent intent) {
        connectmanager.InitLocalConnectManager(getApplicationContext(), AppConstant.BIND_APP_MAC);
        return connectmanager;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");
    }
}
package com.deplink.homegenius.manager.connect.local.tcp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.util.Perfence;

public class LocalConnectService extends Service {
    public static final String TAG = "LocalConnectService";
    public LocalConnectmanager connectmanager = LocalConnectmanager.getInstance();
    public LocalConnectService() {

    }
    @Override
    public IBinder onBind(Intent intent) {
        String uuid= Perfence.getPerfence(AppConstant.PERFENCE_BIND_APP_UUID);
        connectmanager.InitLocalConnectManager(getApplicationContext(), uuid);
        connectmanager.registerNetBroadcast(getApplicationContext());
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
        connectmanager.unRegisterNetBroadcast(getApplicationContext());
        Log.d(TAG, "onDestroy() executed");
    }
}

package com.deplink.homegenius.application;

import org.litepal.LitePalApplication;

import com.deplink.homegenius.util.Perfence;


/**
 * Created by luoxiaoha on 2017/2/6.
 */

public class AppDelegate extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Perfence.setContext(getApplicationContext());

    }
    @Override
    public void onTerminate() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}

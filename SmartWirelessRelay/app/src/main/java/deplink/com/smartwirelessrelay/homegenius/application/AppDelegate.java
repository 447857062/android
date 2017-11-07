package deplink.com.smartwirelessrelay.homegenius.application;

import android.app.Application;


/**
 * Created by luoxiaoha on 2017/2/6.
 */

public class AppDelegate extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

    }
    @Override
    public void onTerminate() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}

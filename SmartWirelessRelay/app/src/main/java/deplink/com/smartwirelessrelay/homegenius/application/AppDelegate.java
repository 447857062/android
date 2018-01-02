package deplink.com.smartwirelessrelay.homegenius.application;

import android.content.Intent;
import android.util.Log;

import org.litepal.LitePalApplication;

import deplink.com.smartwirelessrelay.homegenius.activity.homepage.SmartHomeMainActivity;
import deplink.com.smartwirelessrelay.homegenius.util.Perfence;
import deplink.com.smartwirelessrelay.homegenius.util.exceptionHandler.exceptionhandler.ExceptionHandler;


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

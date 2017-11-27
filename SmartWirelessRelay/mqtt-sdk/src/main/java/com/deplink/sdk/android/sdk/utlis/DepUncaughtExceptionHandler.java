package com.deplink.sdk.android.sdk.utlis;

import android.util.Log;

import com.deplink.sdk.android.sdk.DeplinkSDK;

/**
 * Created by billy on 2016/10/12.
 */
public class DepUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static Thread.UncaughtExceptionHandler oriHandler;

    public static void register()
    {
        if(oriHandler == null) {
            oriHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(new DepUncaughtExceptionHandler());
        }
    }

    public static void unRegister() {
        if (oriHandler != null) {
            Thread.setDefaultUncaughtExceptionHandler(oriHandler);
            oriHandler = null;
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if(DeplinkSDK.DEBUG) {
            Log.wtf(DeplinkSDK.SDK_TAG, ex);
        }
        if (oriHandler != null) {
            oriHandler.uncaughtException(thread, ex);
        }
    }
}

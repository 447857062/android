package com.deplink.homegenius.constant;

import android.util.Log;

/**
 * Created by Administrator on 2017/10/31.
 */
public class DeviceNameTranslate {
    private static final String TAG="DeviceNameTranslate";
    private static final String DEV_TYPE_SMARTLOCK = "SMART_LOCK";
    private static final String DEV_TYPE_SMARTLOCK_TRANSLATED_NAME = "智能密码门锁";
    private static final String DEV_TYPE_ROUTER = "路由器";
    private static final String DEV_TYPE_TV = "智能电视";


    public static String getDeviceTranslatedName(String origName) {
        Log.i(TAG,"origName:"+origName);
        switch (origName) {
            case DEV_TYPE_SMARTLOCK:
                return DEV_TYPE_SMARTLOCK_TRANSLATED_NAME;
            case DEV_TYPE_ROUTER:
                return DEV_TYPE_ROUTER;
            case DEV_TYPE_TV:
                return DEV_TYPE_TV;
        }
        return "未翻译智能设备";
    }

}

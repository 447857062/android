package com.deplink.sdk.android.sdk;

import android.content.Context;

import com.deplink.sdk.android.sdk.manager.SDKManager;
import com.deplink.sdk.android.sdk.rest.RestfulTools;
import com.deplink.sdk.android.sdk.utlis.DepUncaughtExceptionHandler;

/**
 * Created by huqs on 2016/6/29.
 */
public class DeplinkSDK {

    public static boolean DEBUG = false;
    public static final String SDK_TAG = "DeplinkSDK";

    /**
     * TODO 自定义的code：
     * <p/>
     * 400：当前账号正确其他设备上登录，已被迫下线
     * 401：用户名密码错误
     * 402：参数，即服务器返回400
     * 403：当前账号已在其他设备上登录
     * 404：当前没有用户登录
     * 405：无法访问网络
     * 406：登录失败
     * 407：获取设备绑定列表失败，请先登录
     * 408：当前设备部不支持这样操作
     * 409：当前设备操作控制失败
     * 410：获取已绑定设备失败
     * 411：解除绑定设备失败
     * 412：绑定设备失败
     * 413：绑定设备失败,设备序列号不存在
     * 414：解除绑定设备失败
     * 415：注册失败
     * 416：重置密码失败
     * 417：重置密码失败，账号不存在
     * 418：注册失败当前账号已存在
     * 419：当前账号没有权限
     *
     * 500：服务器内部错误
     */

    private volatile static DeplinkSDK singleton;

    private volatile static SDKManager sdkManager = null;
    private volatile static String  key;

    private DeplinkSDK(Context context,String key) {
        this.key = key;
        sdkManager = new SDKManager();
    }

    public static DeplinkSDK initSDK(Context context,String key) {
        if (singleton == null) {
            synchronized (DeplinkSDK.class) {
                if (singleton == null) {
                    singleton = new DeplinkSDK(context,key);
                }
            }
        }
        RestfulTools.getSingleton();
        DepUncaughtExceptionHandler.register();

        return singleton;
    }

    public static String getVersion() {
        return "1.0.1";
    }

    public static String getAppKey() {
        return key;
    }

    public static SDKManager getSDKManager() {
        if (singleton == null) {
            String var0 = "Please call DeplinkSDK.initSDK(Context, String, String) before getSDKManager() action.";
            throw new NullPointerException(var0);
        }
        return sdkManager;
    }

    public static synchronized void destroy() {
        if (sdkManager != null) {
            sdkManager.onDestroy();

        }
        DepUncaughtExceptionHandler.unRegister();
    }
}






package com.deplink.homegenius.application;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.util.Perfence;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushManager;

import org.litepal.LitePalApplication;


/**
 * Created by luoxiaoha on 2017/2/6.
 */

public class AppDelegate extends LitePalApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        Perfence.setContext(getApplicationContext());
        String uuid= Perfence.getPerfence(AppConstant.PERFENCE_BIND_APP_UUID);
        if(!uuid.equalsIgnoreCase("")){
          XGPushManager.registerPush(getApplicationContext(),uuid,new XGIOperateCallback() {
                @Override
                public void onSuccess(Object data, int flag) {
                    Log.d("TPush", "注册成功，设备token为：" + data);
                }
                @Override
                public void onFail(Object data, int errCode, String msg) {
                    Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
                }
            });
        }

    }
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    public void onTerminate() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}

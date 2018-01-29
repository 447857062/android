package com.deplink.sdk.android.sdk;

import android.graphics.Bitmap;

import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;

/**
 * Created by huqs on 2016/7/1.
 */
public abstract class EventCallback {

    /**
     * 请求成功
     * @param action
     */
    public abstract void onSuccess(SDKAction action);
    /**
     * 绑定成功
     * @param action
     */
    public abstract void onBindSuccess(SDKAction action,String devicekey);
    /**
     * 获取图片成功
     * @param action
     */
    public  void onGetImageSuccess(SDKAction action,Bitmap bm){

    }
    public  void onGetUserInfouccess(String info){}
    public  void alertUserInfo(DeviceOperationResponse info){}

    /**
     * 请求失败
     * @param action
     * @param  throwable
     */
    public abstract void onFailure(SDKAction action, Throwable throwable);

    /**
     * 用户掉线或者登录失效
     * @param  throwable
     */
    public void connectionLost(Throwable throwable){

    }

    /**
     * 设备状态改变
     * @param deviceKey
     * @param type
     */
    public void notifyDeviceDataChanged(String deviceKey, int type){

    }

    /**
     * 设备执行命令成功
     * @param op
     * @param deviceKey
     */
    public void deviceOpSuccess(String op, String deviceKey){

    }

    /**
     * 设备执行命令成功
     * @param op
     * @param deviceKey
     */
    public void deviceOpFailure(String op, String deviceKey, Throwable throwable){

    }

    /**
     * 设备升级通知
     * @param deviceKey
     */
    public void notifyDeviceUpgrade(String deviceKey) {

    }
    /**
     * 设备升级通知
     */
    public void notifyHomeGeniusResponse(String result) {

    }
}

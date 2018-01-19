package com.deplink.homegenius.manager.device.remoteControl;

import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;

/**
 * Created by Administrator on 2017/11/9.
 */
public abstract class RemoteControlListener {
    /**
     * 返回智能锁查询结果
     */
    public void responseQueryResult(String result) {};

    public void responseDeleteVirtualDevice(DeviceOperationResponse result) {};
    public void responseAlertVirtualDevice(DeviceOperationResponse result) {};

}

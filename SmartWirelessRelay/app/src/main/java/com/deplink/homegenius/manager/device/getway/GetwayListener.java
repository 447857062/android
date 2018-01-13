package com.deplink.homegenius.manager.device.getway;

import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;

/**
 * Created by Administrator on 2017/11/9.
 */
public interface GetwayListener {
    /**
     *返回结果
     */
    void responseResult(String result);
    void responseDeleteDeviceHttpResult(DeviceOperationResponse result);
    void responseSetWifirelayResult(int result);
}

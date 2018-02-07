package com.deplink.homegenius.manager.device;

import com.deplink.homegenius.Protocol.json.device.lock.SSIDList;
import com.deplink.sdk.android.sdk.homegenius.DeviceOperationResponse;
import com.deplink.sdk.android.sdk.homegenius.Deviceprops;

import java.util.List;

/**
 * Created by Administrator on 2017/11/9.
 */
public abstract class DeviceListener {
    /**
     *返回智能锁查询结果
     */
    public  void responseQueryResult(String result){

    };
    /**
     *返回绑定结果
     */
    public  void responseBindDeviceResult(String result){

    };
    /**
     *返回wifi列表
     */
    public  void responseWifiListResult(List<SSIDList>wifiList){

    };

    public  void responseAddDeviceHttpResult(DeviceOperationResponse deviceOperationResponse){

    };
    public  void responseAddVirtualDeviceHttp(DeviceOperationResponse deviceOperationResponse){

    };
    public  void responseDeleteDeviceHttpResult(DeviceOperationResponse result){

    };
    public  void responseAlertDeviceHttpResult(DeviceOperationResponse result){

    };
    public  void responseGetDeviceInfoHttpResult(String result){

    };
    public  void responseGetDeviceShareInfo(String result){

    };
    public  void responseDeviceShareResult(DeviceOperationResponse result){

    };
    public  void responseCancelDeviceShare(DeviceOperationResponse result){

    };
    public  void responseQueryHttpResult(List<Deviceprops>devices){

    };
}

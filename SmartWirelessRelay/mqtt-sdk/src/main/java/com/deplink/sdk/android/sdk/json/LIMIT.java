package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/5.
 */
public class LIMIT implements Serializable{

    private String DeviceMac;
    private DataSpeed DataSpeed;

    public String getDeviceMac() {
        return DeviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        DeviceMac = deviceMac;
    }

    public com.deplink.sdk.android.sdk.json.DataSpeed getDataSpeed() {
        return DataSpeed;
    }

    public void setDataSpeed(com.deplink.sdk.android.sdk.json.DataSpeed dataSpeed) {
        DataSpeed = dataSpeed;
    }

    @Override
    public String toString() {
        return "LIMIT{" +
                "DeviceMac='" + DeviceMac + '\'' +
                ", DataSpeed=" + DataSpeed +
                '}';
    }
}

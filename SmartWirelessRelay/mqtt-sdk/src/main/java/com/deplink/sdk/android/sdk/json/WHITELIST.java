package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;


/**
 * Created by Administrator on 2017/8/4.
 */
public class WHITELIST implements Serializable{

    private String DeviceMac;


    public String getDeviceMac() {
        return DeviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        DeviceMac = deviceMac;
    }

    @Override
    public String toString() {
        return "WHITELIST{" +
                "DeviceMac='" + DeviceMac + '\'' +
                '}';
    }
}

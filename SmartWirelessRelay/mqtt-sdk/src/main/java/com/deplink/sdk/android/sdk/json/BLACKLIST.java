package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;


/**
 * Created by Administrator on 2017/8/4.
 */
public class BLACKLIST implements Serializable{

    private String DeviceName;
    private String MAC;
    private String DeviceMac;

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getDeviceMac() {
        return DeviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        DeviceMac = deviceMac;
    }

    @Override
    public String toString() {
        return "BLACKLIST{" +
                "DeviceName='" + DeviceName + '\'' +
                ", MAC='" + MAC + '\'' +
                ", DeviceMac='" + DeviceMac + '\'' +
                '}';
    }
}

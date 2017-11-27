package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;


/**
 * Created by Administrator on 2017/8/4.
 */
public class DevicesOnline implements Serializable{

    private String DeviceName;
    private String MAC;
    private String IP;
    private String DataSpeedRx;
    private String DataSpeedTx;

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

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getDataSpeedRx() {
        return DataSpeedRx;
    }

    public void setDataSpeedRx(String dataSpeedRx) {
        DataSpeedRx = dataSpeedRx;
    }

    public String getDataSpeedTx() {
        return DataSpeedTx;
    }

    public void setDataSpeedTx(String dataSpeedTx) {
        DataSpeedTx = dataSpeedTx;
    }

    @Override
    public String toString() {
        return "DevicesOnline{" +
                "DeviceName='" + DeviceName + '\'' +
                ", MAC='" + MAC + '\'' +
                ", IP='" + IP + '\'' +
                ", DataSpeedRx='" + DataSpeedRx + '\'' +
                ", DataSpeedTx='" + DataSpeedTx + '\'' +
                '}';
    }
}

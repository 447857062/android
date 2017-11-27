package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/4.
 */
public class VISITOR implements Serializable{
    private String WifiStatus;
    private String WifiSSID;
    private String WifiPassword;
    private String Encryption;
    public String getWifiStatus() {
        return WifiStatus;
    }

    public void setWifiStatus(String wifiStatus) {
        WifiStatus = wifiStatus;
    }

    public String getWifiSSID() {
        return WifiSSID;
    }

    public void setWifiSSID(String wifiSSID) {
        WifiSSID = wifiSSID;
    }

    public String getWifiPassword() {
        return WifiPassword;
    }

    public void setWifiPassword(String wifiPassword) {
        WifiPassword = wifiPassword;
    }

    public String getEncryption() {
        return Encryption;
    }

    public void setEncryption(String encryption) {
        Encryption = encryption;
    }

    @Override
    public String toString() {
        return "VISITOR{" +
                "WifiStatus='" + WifiStatus + '\'' +
                ", WifiSSID='" + WifiSSID + '\'' +
                ", WifiPassword='" + WifiPassword + '\'' +
                ", Encryption='" + Encryption + '\'' +
                '}';
    }
}

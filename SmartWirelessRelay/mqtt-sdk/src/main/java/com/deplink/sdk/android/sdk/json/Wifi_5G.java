package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/4.
 */
public class Wifi_5G implements Serializable{
    private String WifiStatus;
    private String WifiSSID;
    private String WifiPassword;
    private String POWER;
    private String HIDDEN;
    private String HTMODE;
    private String HWMODE;
    private String CHANNEL;
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

    public String getPOWER() {
        return POWER;
    }

    public void setPOWER(String POWER) {
        this.POWER = POWER;
    }

    public String getHIDDEN() {
        return HIDDEN;
    }

    public void setHIDDEN(String HIDDEN) {
        this.HIDDEN = HIDDEN;
    }

    public String getHTMODE() {
        return HTMODE;
    }

    public void setHTMODE(String HTMODE) {
        this.HTMODE = HTMODE;
    }

    public String getHWMODE() {
        return HWMODE;
    }

    public void setHWMODE(String HWMODE) {
        this.HWMODE = HWMODE;
    }

    public String getCHANNEL() {
        return CHANNEL;
    }

    public void setCHANNEL(String CHANNEL) {
        this.CHANNEL = CHANNEL;
    }

    public String getEncryption() {
        return Encryption;
    }

    public void setEncryption(String encryption) {
        Encryption = encryption;
    }

    @Override
    public String toString() {
        return "Wifi_5G{" +
                "WifiStatus='" + WifiStatus + '\'' +
                ", WifiSSID='" + WifiSSID + '\'' +
                ", WifiPassword='" + WifiPassword + '\'' +
                ", POWER='" + POWER + '\'' +
                ", HIDDEN='" + HIDDEN + '\'' +
                ", HTMODE='" + HTMODE + '\'' +
                ", HWMODE='" + HWMODE + '\'' +
                ", CHANNEL='" + CHANNEL + '\'' +
                ", Encryption='" + Encryption + '\'' +
                '}';
    }
}

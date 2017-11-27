package com.deplink.sdk.android.sdk.json;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/4.
 */
public class Wifi implements Serializable{
    private String  OP;
    private String  Method;
    @SerializedName("2G")
    private Wifi_2G wifi_2G;
    @SerializedName("5G")
    private Wifi_5G wifi_5G;
    private VISITOR VISITOR;
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOP() {
        return OP;
    }

    public void setOP(String OP) {
        this.OP = OP;
    }

    public String getMethod() {
        return Method;
    }

    public void setMethod(String method) {
        Method = method;
    }



    public Wifi_2G getWifi_2G() {
        return wifi_2G;
    }

    public void setWifi_2G(Wifi_2G wifi_2G) {
        this.wifi_2G = wifi_2G;
    }

    public Wifi_5G getWifi_5G() {
        return wifi_5G;
    }

    public void setWifi_5G(Wifi_5G wifi_5G) {
        this.wifi_5G = wifi_5G;
    }

    public VISITOR getVISITOR() {
        return VISITOR;
    }

    public void setVISITOR(VISITOR VISITOR) {
        this.VISITOR = VISITOR;
    }

    @Override
    public String toString() {
        return "Wifi{" +
                "OP='" + OP + '\'' +
                ", Method='" + Method + '\'' +
                ", wifi_2G=" + wifi_2G +
                ", wifi_5G=" + wifi_5G +
                ", VISITOR=" + VISITOR +
                '}';
    }
}

package com.deplink.sdk.android.sdk.json;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


/**
 * Created by Administrator on 2017/8/4.
 */
public class Device implements Serializable{
    private DataTraffic DataTraffic;
    @SerializedName("2G")
    private Wifi_2G wifi_2G;
   @SerializedName("5G")
    private Wifi_5G wifi_5G;
    private VISITOR VISITOR;
    private String FWVersion;
    private String DeviceName;
    private String WANIP;
    private String CPU;
    private String MEM;
    private String OP;
    private String Method;
    public DataTraffic getDataTraffic() {
        return DataTraffic;
    }

    public void setDataTraffic(DataTraffic dataTraffic) {
        DataTraffic = dataTraffic;
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

    public String getFWVersion() {
        return FWVersion;
    }

    public void setFWVersion(String FWVersion) {
        this.FWVersion = FWVersion;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getWANIP() {
        return WANIP;
    }

    public void setWANIP(String WANIP) {
        this.WANIP = WANIP;
    }

    public String getCPU() {
        return CPU;
    }

    public void setCPU(String CPU) {
        this.CPU = CPU;
    }

    public String getMEM() {
        return MEM;
    }

    public void setMEM(String MEM) {
        this.MEM = MEM;
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

    @Override
    public String toString() {
        return "Device{" +
                "DataTraffic=" + DataTraffic +
                ", wifi_2G=" + wifi_2G +
                ", wifi_5G=" + wifi_5G +
                ", VISITOR=" + VISITOR +
                ", FWVersion='" + FWVersion + '\'' +
                ", DeviceName='" + DeviceName + '\'' +
                ", WANIP='" + WANIP + '\'' +
                ", CPU='" + CPU + '\'' +
                ", MEM='" + MEM + '\'' +
                ", OP='" + OP + '\'' +
                ", Method='" + Method + '\'' +
                '}';
    }
}

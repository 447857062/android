package com.deplink.sdk.android.sdk.rest;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/2.
 */
public class wifiScanRoot implements Serializable{
    private encryption encryption;
    private int quality_max;
    private String ssid;
    private int channel;
    private int signal;
    private String bssid;
    private String mode;
    private int quality;

    public encryption getencryption() {
        return encryption;
    }

    public  void setencryption(encryption encryption) {
        this.encryption = encryption;
    }

    public int getQuality_max() {
        return quality_max;
    }

    public void setQuality_max(int quality_max) {
        this.quality_max = quality_max;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    @Override
    public String toString() {
        return "wifiScanRoot{" +
                "mEncryption=" + encryption +
                ", quality_max='" + quality_max + '\'' +
                ", ssid='" + ssid + '\'' +
                ", channel=" + channel +
                ", signal=" + signal +
                ", bssid='" + bssid + '\'' +
                ", mode='" + mode + '\'' +
                ", quality=" + quality +
                '}';
    }
}

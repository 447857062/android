package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/5.
 */
public class SSIDList implements Serializable{
    private String SSID;
    private String Channel;
    private String CRYTP;
    private String Quality;
    private String MAC;
    private String Encryption;

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getChannel() {
        return Channel;
    }

    public void setChannel(String channel) {
        Channel = channel;
    }



    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getEncryption() {
        return Encryption;
    }

    public void setEncryption(String encryption) {
        Encryption = encryption;
    }

    public String getCRYTP() {
        return CRYTP;
    }

    public void setCRYTP(String CRYTP) {
        this.CRYTP = CRYTP;
    }

    public String getQuality() {
        return Quality;
    }

    public void setQuality(String quality) {
        Quality = quality;
    }

    @Override
    public String toString() {
        return "SSIDList{" +
                "SSID='" + SSID + '\'' +
                ", Channel='" + Channel + '\'' +
                ", CRYTP='" + CRYTP + '\'' +
                ", Quality='" + Quality + '\'' +
                ", MAC='" + MAC + '\'' +
                ", Encryption='" + Encryption + '\'' +
                '}';
    }
}

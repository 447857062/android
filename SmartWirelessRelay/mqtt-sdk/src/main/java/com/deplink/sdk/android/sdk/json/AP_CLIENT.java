package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/5.
 */
public class AP_CLIENT implements Serializable{
    private String ApCliSsid;
    private String ApCliWPAPSK;
    private String ApCliAuthMode;
    private String ApCliEncrypType;
    private String channel;

    public String getApCliSsid() {
        return ApCliSsid;
    }

    public void setApCliSsid(String apCliSsid) {
        ApCliSsid = apCliSsid;
    }

    public String getApCliWPAPSK() {
        return ApCliWPAPSK;
    }

    public void setApCliWPAPSK(String apCliWPAPSK) {
        ApCliWPAPSK = apCliWPAPSK;
    }

    public String getApCliAuthMode() {
        return ApCliAuthMode;
    }

    public void setApCliAuthMode(String apCliAuthMode) {
        ApCliAuthMode = apCliAuthMode;
    }

    public String getApCliEncrypType() {
        return ApCliEncrypType;
    }

    public void setApCliEncrypType(String apCliEncrypType) {
        ApCliEncrypType = apCliEncrypType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "AP_CLIENT{" +
                "ApCliSsid='" + ApCliSsid + '\'' +
                ", ApCliWPAPSK='" + ApCliWPAPSK + '\'' +
                ", ApCliAuthMode='" + ApCliAuthMode + '\'' +
                ", ApCliEncrypType='" + ApCliEncrypType + '\'' +
                ", channel='" + channel + '\'' +
                '}';
    }
}

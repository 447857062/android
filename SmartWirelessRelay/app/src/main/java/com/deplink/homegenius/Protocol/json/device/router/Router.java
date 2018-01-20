package com.deplink.homegenius.Protocol.json.device.router;

import com.deplink.homegenius.Protocol.json.device.SmartDev;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/5.
 */
public class Router extends DataSupport implements Serializable {
    private String sign_seed;
    private String signature;
    private SmartDev smartDev;
    private String channels;
    private String receveChannels;
    public SmartDev getSmartDev() {
        return smartDev;
    }
    public void setSmartDev(SmartDev smartDev) {
        this.smartDev = smartDev;
    }
    public String getSign_seed() {
        return sign_seed;
    }

    public void setSign_seed(String sign_seed) {
        this.sign_seed = sign_seed;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getChannels() {
        return channels;
    }

    public void setChannels(String channels) {
        this.channels = channels;
    }

    public String getReceveChannels() {
        return receveChannels;
    }

    public void setReceveChannels(String receveChannels) {
        this.receveChannels = receveChannels;
    }

    @Override
    public String toString() {
        return "Router{" +
                "sign_seed='" + sign_seed + '\'' +
                ", signature='" + signature + '\'' +
                ", smartDev=" + smartDev +
                ", channels=" + channels +
                '}';
    }
}

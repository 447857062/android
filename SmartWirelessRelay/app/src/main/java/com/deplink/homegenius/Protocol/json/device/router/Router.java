package com.deplink.homegenius.Protocol.json.device.router;

import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.sdk.android.sdk.bean.Channels;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/5.
 */
public class Router extends DataSupport implements Serializable {
    private String routerDeviceKey;
    private SmartDev smartDev;
    private Channels channels;
    public SmartDev getSmartDev() {
        return smartDev;
    }
    public void setSmartDev(SmartDev smartDev) {
        this.smartDev = smartDev;
    }
    public String getRouterDeviceKey() {
        return routerDeviceKey;
    }
    public void setRouterDeviceKey(String routerDeviceKey) {
        this.routerDeviceKey = routerDeviceKey;
    }

    public Channels getChannels() {
        return channels;
    }

    public void setChannels(Channels channels) {
        this.channels = channels;
    }

    @Override
    public String toString() {
        return "Router{" +
                "routerDeviceKey='" + routerDeviceKey + '\'' +
                ", smartDev=" + smartDev +
                ", channels=" + channels +
                '}';
    }
}

package com.deplink.homegenius.Protocol.packet.ellisdk;

import java.net.InetAddress;

/**
 * Created by benond on 2017/2/6.
 */

public class OneDev {
    public static final int ConnTypeNULL = 0;
    public static final int ConnTypeLocal = 1;
    public static final int ConnTypeRemote = 2;

    public static final long DevOfflineTime = 8000;
    //设备MAC 地址，每个设备都有唯一的MAC值
    public long mac;
    //设备类型  用来区分设备
    public byte type;
    //设备版本  用来区分设备的固件版本，对应不同的功能
    public byte ver;
    //状态
    public char status;
    public InetAddress remoteIP = null;
    public int remotePort;
    public long remoteTime;
    public long localTime;

    public OneDev(long mac, byte type, byte ver) {
        this.mac = mac;
        this.type = type;
        this.ver = ver;
        this.remoteIP = null;
        this.remoteTime = 0;
        this.localTime = 0;
    }

    public int getDevStatus(long time) {
        if (Math.abs(time - localTime) <= DevOfflineTime)
            return ConnTypeLocal;
        if (Math.abs(time - remoteTime) <= DevOfflineTime)
            return ConnTypeRemote;
        return ConnTypeNULL;
    }

    public void freshLocalTime(long time) {
        localTime = time;
    }

    public void freshRemoteTime(long time) {
        remoteTime = time;
    }

    public void setRemoteIP(InetAddress ip) {
        remoteIP = ip;
    }

    public void setRemotePort(int port) {
        this.remotePort = port;
    }


    public long getMac() {
        return mac;

    }

    public byte getType() {
        return type;

    }

    public byte getVer() {
        return ver;
    }

}

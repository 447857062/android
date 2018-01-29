package com.deplink.homegenius.Protocol.packet.ellisdk;

/**
 * Created by benond on 2017/2/24.
 */

public class WIFIData {

    public WIFIData(String ssid, String pwd) {
        this.pwd = pwd;
        this.ssid = ssid;
    }

    public String ssid;
    public String pwd;
}

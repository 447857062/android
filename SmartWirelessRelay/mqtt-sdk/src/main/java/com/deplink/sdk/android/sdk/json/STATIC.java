package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/5.
 */
public class STATIC implements Serializable{
    private String IPADDR;
    private String NETMASK;
    private String GATEWAY;
    private String DNS;
    private String MTU;

    public String getIPADDR() {
        return IPADDR;
    }

    public void setIPADDR(String IPADDR) {
        this.IPADDR = IPADDR;
    }

    public String getNETMASK() {
        return NETMASK;
    }

    public void setNETMASK(String NETMASK) {
        this.NETMASK = NETMASK;
    }

    public String getGATEWAY() {
        return GATEWAY;
    }

    public void setGATEWAY(String GATEWAY) {
        this.GATEWAY = GATEWAY;
    }

    public String getDNS() {
        return DNS;
    }

    public void setDNS(String DNS) {
        this.DNS = DNS;
    }

    public String getMTU() {
        return MTU;
    }

    public void setMTU(String MTU) {
        this.MTU = MTU;
    }

    @Override
    public String toString() {
        return "STATIC{" +
                "IPADDR='" + IPADDR + '\'' +
                ", NETMASK='" + NETMASK + '\'' +
                ", GATEWAY='" + GATEWAY + '\'' +
                ", DNS='" + DNS + '\'' +
                ", MTU='" + MTU + '\'' +
                '}';
    }
}

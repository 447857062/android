package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/5.
 */
public class Lan implements Serializable{
    private String OP;
    private String Method;
    private String LANIP;
    private String NETMASK;
    private String IpStart;
    private String IpOver;
    private String Leasetime;
    private String DhcpStatus;
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

    public String getLANIP() {
        return LANIP;
    }

    public void setLANIP(String LANIP) {
        this.LANIP = LANIP;
    }

    public String getNETMASK() {
        return NETMASK;
    }

    public void setNETMASK(String NETMASK) {
        this.NETMASK = NETMASK;
    }

    public String getIpStart() {
        return IpStart;
    }

    public void setIpStart(String ipStart) {
        IpStart = ipStart;
    }

    public String getIpOver() {
        return IpOver;
    }

    public void setIpOver(String ipOver) {
        IpOver = ipOver;
    }

    public String getLeasetime() {
        return Leasetime;
    }

    public void setLeasetime(String leasetime) {
        Leasetime = leasetime;
    }

    public String getDhcpStatus() {
        return DhcpStatus;
    }

    public void setDhcpStatus(String dhcpStatus) {
        DhcpStatus = dhcpStatus;
    }

    @Override
    public String toString() {
        return "Lan{" +
                "OP='" + OP + '\'' +
                ", Method='" + Method + '\'' +
                ", LANIP='" + LANIP + '\'' +
                ", NETMASK='" + NETMASK + '\'' +
                ", IpStart='" + IpStart + '\'' +
                ", IpOver='" + IpOver + '\'' +
                ", Leasetime='" + Leasetime + '\'' +
                ", DhcpStatus='" + DhcpStatus + '\'' +
                '}';
    }
}

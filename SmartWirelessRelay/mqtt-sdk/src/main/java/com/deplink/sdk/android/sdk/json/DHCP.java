package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/5.
 */
public class DHCP implements Serializable{

    private String DNS;
    private String MTU;

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
        return "DHCP{" +
                "DNS='" + DNS + '\'' +
                ", MTU='" + MTU + '\'' +
                '}';
    }
}

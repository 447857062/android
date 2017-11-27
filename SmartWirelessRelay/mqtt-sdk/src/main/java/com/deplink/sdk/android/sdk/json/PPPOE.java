package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/5.
 */
public class PPPOE implements Serializable{
    private String username;
    private String passwd;
    private String DNS;
    private String MTU;
    private String MAC;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
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

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    @Override
    public String toString() {
        return "PPPOE{" +
                "username='" + username + '\'' +
                ", passwd='" + passwd + '\'' +
                ", DNS='" + DNS + '\'' +
                ", MTU='" + MTU + '\'' +
                ", MAC='" + MAC + '\'' +
                '}';
    }
}

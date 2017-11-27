package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;
import java.util.List;


/**
 * Created by Administrator on 2017/8/4.
 */
public class DevicesOnlineRoot implements Serializable{

    private String OP="DEVICES";
    private String Method="REPORT   ";
    private List<DevicesOnline>DevicesOnline;
    private List<BLACKLIST>BLACKLIST;
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

    public List<DevicesOnline> getDevicesOnline() {
        return DevicesOnline;
    }

    public void setDevicesOnline(List<DevicesOnline> devicesOnline) {
        DevicesOnline = devicesOnline;
    }

    public List<BLACKLIST> getBLACKLIST() {
        return BLACKLIST;
    }

    public void setBLACKLIST(List<BLACKLIST> BLACKLIST) {
        this.BLACKLIST = BLACKLIST;
    }

    @Override
    public String toString() {
        return "DevicesOnlineRoot{" +
                "OP='" + OP + '\'' +
                ", Method='" + Method + '\'' +
                ", DevicesOnline=" + DevicesOnline +
                ", BLACKLIST=" + BLACKLIST +
                '}';
    }
}

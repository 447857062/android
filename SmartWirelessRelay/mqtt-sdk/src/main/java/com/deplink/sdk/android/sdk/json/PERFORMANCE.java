package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/8/4.
 */
public class PERFORMANCE implements Serializable{
    private String  OP;
    private String  Method;
    private List<BLACKLIST> BLACKLIST;
    private Proto Proto;
    private List<SSIDList> SSIDList;
    private String User;
    private Device Device;
    private long timestamp;
    private String Result;
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

    public Proto getProto() {
        return Proto;
    }

    public void setProto(Proto proto) {
        Proto = proto;
    }

    public List<BLACKLIST> getBLACKLIST() {
        return BLACKLIST;
    }

    public void setBLACKLIST(List<BLACKLIST> BLACKLIST) {
        this.BLACKLIST = BLACKLIST;
    }


    public List<SSIDList> getSSIDList() {
        return SSIDList;
    }

    public void setSSIDList(List<SSIDList> SSIDList) {
        this.SSIDList = SSIDList;
    }



    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public Device getDevice() {
        return Device;
    }

    public void setDevice(Device device) {
        Device = device;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    @Override
    public String toString() {
        return "PERFORMANCE{" +
                "OP='" + OP + '\'' +
                ", Method='" + Method + '\'' +
                ", BLACKLIST=" + BLACKLIST +
                ", Proto=" + Proto +
                ", SSIDList=" + SSIDList +
                ", User='" + User + '\'' +
                ", Device=" + Device +
                ", timestamp=" + timestamp +
                ", Result='" + Result + '\'' +
                '}';
    }
}

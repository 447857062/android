package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;


/**
 * Created by Administrator on 2017/8/4.
 */
public class DeviceControl implements Serializable{
    private String OP;
    private String Method;
    private LIMIT LIMIT;
    private BLACKLIST BLACKLIST;
    private WHITELIST WHITELIST;
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

    public com.deplink.sdk.android.sdk.json.LIMIT getLIMIT() {
        return LIMIT;
    }

    public void setLIMIT(com.deplink.sdk.android.sdk.json.LIMIT LIMIT) {
        this.LIMIT = LIMIT;
    }

    public com.deplink.sdk.android.sdk.json.BLACKLIST getBLACKLIST() {
        return BLACKLIST;
    }

    public void setBLACKLIST(com.deplink.sdk.android.sdk.json.BLACKLIST BLACKLIST) {
        this.BLACKLIST = BLACKLIST;
    }

    public com.deplink.sdk.android.sdk.json.WHITELIST getWHITELIST() {
        return WHITELIST;
    }

    public void setWHITELIST(com.deplink.sdk.android.sdk.json.WHITELIST WHITELIST) {
        this.WHITELIST = WHITELIST;
    }

    @Override
    public String toString() {
        return "DeviceControl{" +
                "OP='" + OP + '\'' +
                ", Method='" + Method + '\'' +
                ", LIMIT=" + LIMIT +
                ", BLACKLIST=" + BLACKLIST +
                ", WHITELIST=" + WHITELIST +
                '}';
    }
}

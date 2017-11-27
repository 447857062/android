package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/5.
 */
public class Qos implements Serializable{
    private String OP;
    private String Method;
    private String SWITCH;
    private String CLASSIFY;
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

    public String getSWITCH() {
        return SWITCH;
    }

    public void setSWITCH(String SWITCH) {
        this.SWITCH = SWITCH;
    }

    public String getCLASSIFY() {
        return CLASSIFY;
    }

    public void setCLASSIFY(String CLASSIFY) {
        this.CLASSIFY = CLASSIFY;
    }

    @Override
    public String toString() {
        return "Qos{" +
                "OP='" + OP + '\'' +
                ", Method='" + Method + '\'' +
                ", SWITCH='" + SWITCH + '\'' +
                ", CLASSIFY='" + CLASSIFY + '\'' +
                '}';
    }
}

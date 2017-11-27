package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;

/**
 * Created by billy on 2016/8/19.
 */
public class DeviceUpgradeReport implements Serializable{
    private String OP;

    private String Method;

    private String State;

    private int Progress;

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

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public int getProgress() {
        return Progress;
    }

    public void setProgress(int progress) {
        Progress = progress;
    }

    @Override
    public String toString() {
        return "DeviceUpgradeReport{" +
                "OP='" + OP + '\'' +
                ", Method='" + Method + '\'' +
                ", State='" + State + '\'' +
                ", Progress=" + Progress +
                '}';
    }
}

package com.deplink.sdk.android.sdk.bean;

import java.io.Serializable;

/**
 * Created by billy on 2016/8/17.
 */
public class CommonRes implements Serializable{
    private String status;
    private String msg;

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "CommonRes{" +
                "status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}

package com.deplink.sdk.android.sdk.rest;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/31.
 */
public class RouterResponse implements Serializable{
    private String msg;
    private int code;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "RouterResponse{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                '}';
    }
}

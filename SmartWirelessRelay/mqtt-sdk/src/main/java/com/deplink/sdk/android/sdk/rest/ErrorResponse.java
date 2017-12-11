package com.deplink.sdk.android.sdk.rest;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/31.
 */
public class ErrorResponse implements Serializable {
    private String errcode;
    private String msg;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "errcode='" + errcode + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}

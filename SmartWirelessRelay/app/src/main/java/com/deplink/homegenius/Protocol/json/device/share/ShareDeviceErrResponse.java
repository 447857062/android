package com.deplink.homegenius.Protocol.json.device.share;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 */
public class ShareDeviceErrResponse implements Serializable {
  private int errcode;
  private String msg;

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
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
        return "ShareDeviceErrResponse{" +
                "errcode=" + errcode +
                ", msg='" + msg + '\'' +
                '}';
    }
}

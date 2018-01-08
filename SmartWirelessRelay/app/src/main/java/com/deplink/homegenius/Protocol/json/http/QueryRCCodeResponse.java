package com.deplink.homegenius.Protocol.json.http;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/22.
 */
public class QueryRCCodeResponse implements Serializable{
    private boolean result;
    private int errno;
    private String err;
    private CodeValue value;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public CodeValue getValue() {
        return value;
    }

    public void setValue(CodeValue value) {
        this.value = value;
    }
}

package com.deplink.homegenius.Protocol.json.http;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/22.
 */
public class QueryTestCodeResponse implements Serializable{
    private boolean result;
    private int errno;
    private String err;
    private List<TestCode>value=new ArrayList<>();

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

    public List<TestCode> getValue() {
        return value;
    }

    public void setValue(List<TestCode> value) {
        this.value = value;
    }
}

package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/19.
 */
public class Content implements Serializable{
    private String  OP;
    private String  Method;

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

    @Override
    public String toString() {
        return "Content{" +
                "OP='" + OP + '\'' +
                ", Method='" + Method + '\'' +
                '}';
    }
}

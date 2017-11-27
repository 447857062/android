package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/5.
 */
public class DataSpeed implements Serializable{

    private String Rx;
    private String Tx;

    public String getRx() {
        return Rx;
    }

    public void setRx(String rx) {
        Rx = rx;
    }

    public String getTx() {
        return Tx;
    }

    public void setTx(String tx) {
        Tx = tx;
    }

    @Override
    public String toString() {
        return "DataSpeed{" +
                "Rx='" + Rx + '\'' +
                ", Tx='" + Tx + '\'' +
                '}';
    }
}

package com.deplink.homegenius.Protocol.json.device.lock.alertreport;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 */
public class ALARM_INFO implements Serializable {
    private String INFO;

    public String getINFO() {
        return INFO;
    }

    public void setINFO(String INFO) {
        this.INFO = INFO;
    }

    @Override
    public String toString() {
        return "ALARM_INFO{" +
                "INFO='" + INFO + '\'' +
                '}';
    }
}

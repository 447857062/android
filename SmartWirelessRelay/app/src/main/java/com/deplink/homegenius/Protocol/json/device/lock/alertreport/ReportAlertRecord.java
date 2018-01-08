package com.deplink.homegenius.Protocol.json.device.lock.alertreport;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/30.
 */
public class ReportAlertRecord implements Serializable {
    private String OP="REPORT";
    private String Method="ALARM_INFO";
    private List<Info> Info;
    private String Type;
    private String SmartDev;

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getSmartDev() {
        return SmartDev;
    }

    public void setSmartDev(String smartDev) {
        SmartDev = smartDev;
    }

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


    public List<com.deplink.homegenius.Protocol.json.device.lock.alertreport.Info> getInfo() {
        return Info;
    }

    public void setInfo(List<com.deplink.homegenius.Protocol.json.device.lock.alertreport.Info> info) {
        Info = info;
    }

}

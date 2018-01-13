package com.deplink.homegenius.Protocol.json.device;

import com.deplink.homegenius.Protocol.json.device.getway.GatwayDevice;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/30.
 */
public class DeviceList   implements Serializable {
    private String OP;
    private String Method;
    private List<GatwayDevice> Device;
    private List<SmartDev>SmartDev;
    public List<GatwayDevice> getDevice() {
        return Device;
    }

    public void setDevice(List<GatwayDevice> device) {
        Device = device;
    }

    public List<SmartDev> getSmartDev() {
        return SmartDev;
    }

    public void setSmartDev(List<SmartDev> smartDev) {
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

}

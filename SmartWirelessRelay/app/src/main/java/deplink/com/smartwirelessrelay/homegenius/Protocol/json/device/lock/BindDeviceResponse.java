package deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock;

import java.io.Serializable;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;

/**
 * Created by Administrator on 2017/10/30.
 */
public class BindDeviceResponse implements Serializable {
    private String OP="REPORT";
    private String Method="DevList";
    private List<Device> Device;
    private List<SmartDev>SmartDev;
    public List<Device> getDevice() {
        return Device;
    }

    public void setDevice(List<Device> device) {
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

package deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.router;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;

/**
 * Created by Administrator on 2018/1/5.
 */
public class Router extends DataSupport implements Serializable {
    private String routerDeviceKey;
    private SmartDev smartDev;
    public SmartDev getSmartDev() {
        return smartDev;
    }

    public void setSmartDev(SmartDev smartDev) {
        this.smartDev = smartDev;
    }

    public String getRouterDeviceKey() {
        return routerDeviceKey;
    }

    public void setRouterDeviceKey(String routerDeviceKey) {
        this.routerDeviceKey = routerDeviceKey;
    }
}

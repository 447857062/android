package com.deplink.homegenius.Protocol.json.wifi;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/5.
 */
public class Proto implements Serializable{
    private AP_CLIENT AP_CLIENT;
    public AP_CLIENT getAP_CLIENT() {
        return AP_CLIENT;
    }
    public void setAP_CLIENT(AP_CLIENT AP_CLIENT) {
        this.AP_CLIENT = AP_CLIENT;
    }
}

package com.deplink.sdk.android.sdk.json;

/**
 * Created by Administrator on 2017/8/5.
 */
public class Proto {
    private PPPOE PPPOE;
    private STATIC STATIC;
    private DHCP DHCP;
    private AP_CLIENT AP_CLIENT;

    public PPPOE getPPPOE() {
        return PPPOE;
    }

    public void setPPPOE(PPPOE PPPOE) {
        this.PPPOE = PPPOE;
    }

    public STATIC getSTATIC() {
        return STATIC;
    }

    public void setSTATIC(STATIC STATIC) {
        this.STATIC = STATIC;
    }

    public DHCP getDHCP() {
        return DHCP;
    }

    public void setDHCP(DHCP DHCP) {
        this.DHCP = DHCP;
    }

    public AP_CLIENT getAP_CLIENT() {
        return AP_CLIENT;
    }

    public void setAP_CLIENT(AP_CLIENT AP_CLIENT) {
        this.AP_CLIENT = AP_CLIENT;
    }

    @Override
    public String toString() {
        return "Proto{" +
                "PPPOE=" + PPPOE +
                ", STATIC=" + STATIC +
                ", DHCP=" + DHCP +
                ", AP_CLIENT=" + AP_CLIENT +
                '}';
    }
}

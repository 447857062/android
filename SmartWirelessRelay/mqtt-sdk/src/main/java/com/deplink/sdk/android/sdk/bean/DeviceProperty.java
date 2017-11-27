package com.deplink.sdk.android.sdk.bean;

/**
 * Created by billy on 2016/8/17.
 */
public class DeviceProperty {
    private String status;
    private String msg;
    private String name;
    private boolean auto_upgrade;

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setAuto_upgrade(boolean auto) {
        auto_upgrade = auto;
    }
    public boolean isAuto_upgrade() {
        return auto_upgrade;
    }
}

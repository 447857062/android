package com.deplink.sdk.android.sdk.bean;

import java.io.Serializable;

/**
 * Created by billy on 2016/9/18.
 */
public class DeviceUpgradeRes  implements Serializable{
    private DeviceUpgradeInfo upgrade_info;
    private String status;
    public DeviceUpgradeInfo getUpgrade_info() {
        return upgrade_info;
    }

    public void setUpgrade_info(DeviceUpgradeInfo upgrade_info) {
        this.upgrade_info = upgrade_info;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DeviceUpgradeRes{" +
                "upgrade_info=" + upgrade_info +
                ", status='" + status + '\'' +
                '}';
    }
}

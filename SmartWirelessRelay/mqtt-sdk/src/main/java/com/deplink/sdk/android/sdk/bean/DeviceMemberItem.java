package com.deplink.sdk.android.sdk.bean;

/**
 * Created by billy on 2016/8/24.
 */
public class DeviceMemberItem {
    private int id;
    private String mobile;
    private String remark;
    private String avatar;
    private boolean bound;
    private boolean primary;
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getMobile() {
        return mobile;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getRemark() {
        return remark;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getAvatar() {
        return avatar;
    }

    public void setBound(boolean bound) {
        this.bound = bound;
    }
    public boolean isBound() {
        return bound;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }
    public boolean isPrimary() {
        return primary;
    }
}

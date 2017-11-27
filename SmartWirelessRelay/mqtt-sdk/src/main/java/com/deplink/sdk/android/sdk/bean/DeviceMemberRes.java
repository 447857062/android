package com.deplink.sdk.android.sdk.bean;

import java.util.List;

/**
 * Created by billy on 2016/8/24.
 */
public class DeviceMemberRes {
    private String status;
    private int id;
    private List<DeviceMemberItem> members;
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setMembers(List<DeviceMemberItem> cookies) {
        this.members = members;
    }
    public List<DeviceMemberItem> getMembers() {
        return members;
    }
}

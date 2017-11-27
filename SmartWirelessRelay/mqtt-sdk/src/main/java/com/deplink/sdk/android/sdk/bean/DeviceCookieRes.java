package com.deplink.sdk.android.sdk.bean;

import java.util.List;

/**
 * Created by billy on 2016/8/17.
 */
public class DeviceCookieRes {

    private String status;
    private int id;
    private List<DeviceCookieItem> cookies;
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

    public void setCookies(List<DeviceCookieItem> cookies) {
        this.cookies = cookies;
    }
    public List<DeviceCookieItem> getCookies() {
        return cookies;
    }

}

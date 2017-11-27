package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/15.
 */
public class Password implements Serializable{
    private String password_org;
    private String password;
    private String application_key=null;
    private long timestamp;

    public String getApplication_key() {
        return application_key;
    }

    public void setApplication_key(String application_key) {
        this.application_key = application_key;
    }

    public String getPassword_org() {
        return password_org;
    }

    public void setPassword_org(String password_org) {
        this.password_org = password_org;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Password{" +
                "password_org='" + password_org + '\'' +
                ", password='" + password + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

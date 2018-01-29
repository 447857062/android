package com.deplink.homegenius.Protocol.json.device.lock;

import org.litepal.crud.DataSupport;

/**
 * Created by ${kelijun} on 2018/1/17.
 */
public class UserIdPairs extends DataSupport{
    private String userid;
    private String username;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserIdPairs{" +
                "userid='" + userid + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}

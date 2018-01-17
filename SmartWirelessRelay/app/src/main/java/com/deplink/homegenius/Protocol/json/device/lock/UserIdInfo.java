package com.deplink.homegenius.Protocol.json.device.lock;

import java.util.List;

/**
 * Created by ${kelijun} on 2018/1/17.
 */
public class UserIdInfo {
    private String selfid;
    private List<UserIdPairs> alluser;

    public String getSelfid() {
        return selfid;
    }

    public void setSelfid(String selfid) {
        this.selfid = selfid;
    }

    public List<UserIdPairs> getAlluser() {
        return alluser;
    }

    public void setAlluser(List<UserIdPairs> alluser) {
        this.alluser = alluser;
    }

    @Override
    public String toString() {
        return "UserIdInfo{" +
                "selfid='" + selfid + '\'' +
                ", alluser=" + alluser +
                '}';
    }
}

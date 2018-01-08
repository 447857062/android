package com.deplink.homegenius.Protocol.json.device.lock;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 */
public class Record implements Serializable{
    private String Time;
    private String UserID;

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    @Override
    public String toString() {
        return "Record{" +
                "Time='" + Time + '\'' +
                ", UserID='" + UserID + '\'' +
                '}';
    }
}

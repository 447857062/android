package com.deplink.homegenius.Protocol.json.device.lock.alertreport;

import com.deplink.homegenius.Protocol.json.device.lock.SmartLock;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 */
public class Info extends DataSupport implements Serializable {

    private String Control_ID;
    private String Time;
    private String UserID;
    private SmartLock mSmartLock;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public SmartLock getmSmartLock() {
        return mSmartLock;
    }

    public void setmSmartLock(SmartLock mSmartLock) {
        this.mSmartLock = mSmartLock;
    }

    public String getID() {
        return Control_ID;
    }

    public void setID(String ID) {
        this.Control_ID = ID;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        this.Time = time;
    }

    public String getUserid() {
        return UserID;
    }

    public void setUserid(String userid) {
        this.UserID = userid;
    }

    @Override
    public String toString() {
        return "Info{" +
                "Control_ID='" + Control_ID + '\'' +
                ", Time='" + Time + '\'' +
                ", UserID='" + UserID + '\'' +
                ", mSmartLock=" + mSmartLock +
                '}';
    }
}

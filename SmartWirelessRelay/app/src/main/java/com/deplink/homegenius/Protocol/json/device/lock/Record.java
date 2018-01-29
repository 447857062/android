package com.deplink.homegenius.Protocol.json.device.lock;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 */
public class Record extends DataSupport implements Serializable{
    @Column(unique = true, nullable = false)
    private int recordIndex;
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

    public int getIndex() {
        return recordIndex;
    }

    public void setIndex(int index) {
        this.recordIndex = index;
    }

}

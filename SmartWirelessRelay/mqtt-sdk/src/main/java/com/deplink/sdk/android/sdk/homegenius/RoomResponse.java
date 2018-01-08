/**
  * Copyright 2016 aTool.org 
  */
package com.deplink.sdk.android.sdk.homegenius;

import java.util.List;

/**
 * Auto-generated: 2016-07-06 12:3:50
 *
 * @author aTool.org (i@aTool.org)
 * @website http://www.atool.org/json2javabean.php
 */
public class RoomResponse {
    private List<Room> rooms;
    private String msg;
    private int errcode;

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    @Override
    public String toString() {
        return "RoomResponse{" +
                "rooms=" + rooms +
                ", msg='" + msg + '\'' +
                ", errcode=" + errcode +
                '}';
    }
}
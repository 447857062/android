/**
  * Copyright 2016 aTool.org 
  */
package com.deplink.sdk.android.sdk.homegenius;

/**
 * Auto-generated: 2016-07-06 12:3:50
 *
 * @author aTool.org (i@aTool.org)
 * @website http://www.atool.org/json2javabean.php
 */
public class Room {
    private String uid;
    private String room_name;
    private String room_type;
    private int device_num;
    private int sort_num;

    public int getSort_num() {
        return sort_num;
    }

    public void setSort_num(int sort_num) {
        this.sort_num = sort_num;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public int getDevice_num() {
        return device_num;
    }

    public void setDevice_num(int device_num) {
        this.device_num = device_num;
    }

    @Override
    public String toString() {
        return "Room{" +
                "uid='" + uid + '\'' +
                ", room_name='" + room_name + '\'' +
                ", room_type='" + room_type + '\'' +
                ", device_num=" + device_num +
                ", sort_num=" + sort_num +
                '}';
    }
}
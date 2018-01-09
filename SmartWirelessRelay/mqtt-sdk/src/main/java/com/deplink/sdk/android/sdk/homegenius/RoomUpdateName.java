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
public class RoomUpdateName {
    private String room_uid;
    private String room_name;

    public String getRoom_uid() {
        return room_uid;
    }

    public void setRoom_uid(String room_uid) {
        this.room_uid = room_uid;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    @Override
    public String toString() {
        return "RoomUpdateName{" +
                "room_uid='" + room_uid + '\'' +
                ", room_name='" + room_name + '\'' +
                '}';
    }
}
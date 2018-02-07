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
public class ShareDeviceBody {
    private int assuper;
    private String user_name;

    public int getAssuper() {
        return assuper;
    }

    public void setAssuper(int assuper) {
        this.assuper = assuper;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    @Override
    public String toString() {
        return "ShareDeviceBody{" +
                "assuper='" + assuper + '\'' +
                ", user_name='" + user_name + '\'' +
                '}';
    }
}
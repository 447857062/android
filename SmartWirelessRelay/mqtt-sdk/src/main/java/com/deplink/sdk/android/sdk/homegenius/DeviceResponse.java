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
public class DeviceResponse {
    private List<Deviceprops> deviceprops;
    private String msg;
    private int errcode;

    @Override
    public String toString() {
        return "DeviceResponse{" +
                "deviceprops=" + deviceprops +
                ", msg='" + msg + '\'' +
                ", errcode=" + errcode +
                '}';
    }
}
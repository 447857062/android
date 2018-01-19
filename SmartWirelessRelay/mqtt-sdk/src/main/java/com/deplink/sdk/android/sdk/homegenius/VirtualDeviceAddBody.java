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
public class VirtualDeviceAddBody {
    private String device_type;
    private String device_name;
    private String irmote_uid;
    private String irmote_mac;

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getIrmote_uid() {
        return irmote_uid;
    }

    public void setIrmote_uid(String irmote_uid) {
        this.irmote_uid = irmote_uid;
    }

    public String getIrmote_mac() {
        return irmote_mac;
    }

    public void setIrmote_mac(String irmote_mac) {
        this.irmote_mac = irmote_mac;
    }

    @Override
    public String toString() {
        return "VirtualDeviceAddBody{" +
                "device_type='" + device_type + '\'' +
                ", device_name='" + device_name + '\'' +
                ", irmote_uid='" + irmote_uid + '\'' +
                ", irmote_mac='" + irmote_mac + '\'' +
                '}';
    }
}
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
public class VirtualDeviceAlertBody {
    private String uid;
    private String device_name;
    private String key_codes;
    private String iremote_uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getKey_codes() {
        return key_codes;
    }

    public void setKey_codes(String key_codes) {
        this.key_codes = key_codes;
    }

    public String getIremote_uid() {
        return iremote_uid;
    }

    public void setIremote_uid(String iremote_uid) {
        this.iremote_uid = iremote_uid;
    }

    @Override
    public String toString() {
        return "VirtualDeviceAlertBody{" +
                "uid='" + uid + '\'' +
                ", device_name='" + device_name + '\'' +
                ", key_codes='" + key_codes + '\'' +
                '}';
    }
}
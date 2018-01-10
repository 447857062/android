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
public class DeviceAddBody{
    private String mac;
    private String sn;
    private String device_type;
    private String device_name;
    private String org_code;
    private String version;
    private String gw_uid;
    private String room_uid;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

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

    public String getOrg_code() {
        return org_code;
    }

    public void setOrg_code(String org_code) {
        this.org_code = org_code;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGw_uid() {
        return gw_uid;
    }

    public void setGw_uid(String gw_uid) {
        this.gw_uid = gw_uid;
    }

    public String getRoom_uid() {
        return room_uid;
    }

    public void setRoom_uid(String room_uid) {
        this.room_uid = room_uid;
    }

    @Override
    public String toString() {
        return "DeviceAddBody{" +
                "mac='" + mac + '\'' +
                ", sn='" + sn + '\'' +
                ", device_type='" + device_type + '\'' +
                ", device_name='" + device_name + '\'' +
                ", org_code='" + org_code + '\'' +
                ", version='" + version + '\'' +
                ", gw_uid='" + gw_uid + '\'' +
                ", room_uid='" + room_uid + '\'' +
                '}';
    }
}
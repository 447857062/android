/**
  * Copyright 2016 aTool.org 
  */
package com.deplink.sdk.android.sdk.homegenius;

import com.deplink.sdk.android.sdk.bean.Channels;

/**
 * Auto-generated: 2016-07-06 12:3:50
 *
 * @author aTool.org (i@aTool.org)
 * @website http://www.atool.org/json2javabean.php
 */
public class DeviceOperationResponse {
    private String msg;
    private String mac;
    private String status;
    private String uid;
    private int errcode;
    private String device_type;
    private String topic;
    private Channels channels;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Channels getChannels() {
        return channels;
    }

    public void setChannels(Channels channels) {
        this.channels = channels;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    @Override
    public String toString() {
        return "DeviceOperationResponse{" +
                "msg='" + msg + '\'' +
                ", mac='" + mac + '\'' +
                ", status='" + status + '\'' +
                ", uid='" + uid + '\'' +
                ", errcode=" + errcode +
                ", device_type='" + device_type + '\'' +
                ", topic='" + topic + '\'' +
                ", channels=" + channels +
                '}';
    }
}
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
    private int errCode;
    private int sort_num;
    private String device_type;
    private String device_name;
    private String irmote_uid;
    private String irmote_mac;
    private String key_codes;
    private String topic;
    private Channels channels;
    private String sign_seed;
    private String signature;

    public String getSign_seed() {
        return sign_seed;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public void setSign_seed(String sign_seed) {
        this.sign_seed = sign_seed;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
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

    public String getKey_codes() {
        return key_codes;
    }

    public void setKey_codes(String key_codes) {
        this.key_codes = key_codes;
    }

    public int getSort_num() {
        return sort_num;
    }

    public void setSort_num(int sort_num) {
        this.sort_num = sort_num;
    }

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
                ", sort_num=" + sort_num +
                ", device_type='" + device_type + '\'' +
                ", device_name='" + device_name + '\'' +
                ", irmote_uid='" + irmote_uid + '\'' +
                ", irmote_mac='" + irmote_mac + '\'' +
                ", key_codes='" + key_codes + '\'' +
                ", topic='" + topic + '\'' +
                ", channels=" + channels +
                '}';
    }
}
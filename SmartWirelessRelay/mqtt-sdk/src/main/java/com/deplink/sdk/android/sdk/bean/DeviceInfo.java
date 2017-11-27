/**
  * Copyright 2016 aTool.org 
  */
package com.deplink.sdk.android.sdk.bean;
/**
 * Auto-generated: 2016-07-06 12:3:50
 *
 * @author aTool.org (i@aTool.org)
 * @website http://www.atool.org/json2javabean.php
 */
public class DeviceInfo {
    private String device_key;
    private String manufacturer;
    private String device_sn;
    private String product;
    private String product_key;
    private Channels channels;
    private String device_name;
    private int auto_upgrade;

    public String getDeviceSn() {
        return device_sn;
    }

    public void setDeviceSn(String device_sn) {
        this.device_sn = device_sn;
    }

    public String getDeviceName() {
        return device_name;
    }

    public void setDeviceName(String device_name) {
        this.device_name = device_name;
    }

    public String getProduct_key() {
        return product_key;
    }

    public void setProduct_key(String product_key) {
        this.product_key = product_key;
    }

    public String getDevice_key() {
        return device_key;
    }

    public void setDevice_key(String device_key) {
        this.device_key = device_key;
    }

    public void setManufacturer(String manufacturer) {
         this.manufacturer = manufacturer;
     }
    public String getManufacturer() {
         return manufacturer;
     }

    public void setProduct(String product) {
         this.product = product;
     }
    public String getProduct() {
         return product;
     }

    public void setChannels(Channels channels) {
         this.channels = channels;
     }
    public Channels getChannels() {
     return channels;
    }

    public void setAuto_upgrade(int auto) {
        auto_upgrade = auto;
    }
    public int getAuto_upgrade() {
        return auto_upgrade;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "deviceKey='" + device_key + '\'' +
                "productKey='" + product_key + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", product='" + product + '\'' +
                ", channels=" + channels +
                '}';
    }
}
/**
  * Copyright 2016 aTool.org 
  */
package com.deplink.sdk.android.sdk.bean;
import java.util.List;
/**
 * Auto-generated: 2016-07-06 12:3:50
 *
 * @author aTool.org (i@aTool.org)
 * @website http://www.atool.org/json2javabean.php
 */
public class DeviceRoot {

    private String status;
    private List<DeviceInfo> device_list;

    public void setStatus(String status) {
         this.status = status;
     }
    public String getStatus() {
         return status;
     }

    public List<DeviceInfo> getDevice_list() {
        return device_list;
    }
    public void setDevice_list(List<DeviceInfo> device_list) {
        this.device_list = device_list;
    }

    @Override
    public String toString() {
        return "DeviceRoot{" +
                "status='" + status + '\'' +
                ", deviceList=" + device_list +
                '}';
    }
}
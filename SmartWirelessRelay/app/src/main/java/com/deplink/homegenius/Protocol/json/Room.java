package com.deplink.homegenius.Protocol.json;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.deplink.homegenius.Protocol.json.device.getway.Device;
import com.deplink.homegenius.Protocol.json.device.SmartDev;


/**
 * Created by Administrator on 2017/10/30.
 * 房间管理
 */
public class Room extends DataSupport implements Serializable{

    @Column(unique = true,nullable = false)
    /**
     * 房间名称
     */
    private String roomName;
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Column(nullable = false)
    /**
     * 房间序号（用于显示的时候排序）
     */
    private int roomOrdinalNumber;
    private String roomType;
    /**
     * 智能设备和房间是多对对的关系
     */
    private List<SmartDev>mDevices=new ArrayList<>();
    /**
     * 网关设备（中继器）和房间也有一个多对多的关系
     */
    private List<Device>mGetwayDevices=new ArrayList<>();
    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public List<Device> getmGetwayDevices() {
        return mGetwayDevices;
    }

    public void setmGetwayDevices(List<Device> mGetwayDevices) {
        this.mGetwayDevices = mGetwayDevices;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }
    public int getRoomOrdinalNumber() {
        return roomOrdinalNumber;
    }
    public void setRoomOrdinalNumber(int roomOrdinalNumber) {
        this.roomOrdinalNumber = roomOrdinalNumber;
    }

    public List<SmartDev> getmDevices() {
        return mDevices;
    }

    public void setmDevices(List<SmartDev> mDevices) {
        this.mDevices = mDevices;
    }


}
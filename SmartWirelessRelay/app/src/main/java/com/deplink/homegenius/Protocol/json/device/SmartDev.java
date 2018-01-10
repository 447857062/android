package com.deplink.homegenius.Protocol.json.device;

import com.deplink.homegenius.Protocol.json.Room;
import com.deplink.homegenius.Protocol.json.device.getway.Device;
import com.deplink.homegenius.Protocol.json.device.lock.alertreport.Info;
import com.deplink.homegenius.Protocol.json.device.router.Router;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/30.
 */
public class SmartDev extends DataSupport implements Serializable {
    @Column(unique = true, nullable = false)
    private String Uid;
    private String CtrUid;
    private String Type;
    /*设备类型子类型，比如开关，下面又会分1,2,3，4,路开关*/
    private String subType;
    private String Status;
    private String Org;
    private String Ver;
    private String sn;
    private String mac;
    private String name;
    /*绑定的网关*/
    private Device getwayDevice;
    private boolean switch_one_open;
    private boolean switch_two_open;
    private boolean switch_three_open;
    private boolean switch_four_open;
    private Router router;

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

    public Router getRouter() {
        return router;
    }
    public void setRouter(Router router) {
        this.router = router;
    }
    /**
     * 数据库中的关联关系必须要初始化好列表
     */
    @Column(nullable = false)
    private List<Room> rooms = new ArrayList<>();
    /**
     * 各种类型的遥控器需要指定物理遥控器
     */
    private String remotecontrolUid;
    private String lockPassword = "";
    private boolean remerberPassword = true;
    private List<Info> alarmInfo = new ArrayList<>();
    public boolean isSwitch_one_open() {
        return switch_one_open;
    }
    public void setSwitch_one_open(boolean switch_one_open) {
        this.switch_one_open = switch_one_open;
    }

    public boolean isSwitch_two_open() {
        return switch_two_open;
    }

    public void setSwitch_two_open(boolean switch_two_open) {
        this.switch_two_open = switch_two_open;
    }

    public boolean isSwitch_three_open() {
        return switch_three_open;
    }

    public void setSwitch_three_open(boolean switch_three_open) {
        this.switch_three_open = switch_three_open;
    }

    public boolean isSwitch_four_open() {
        return switch_four_open;
    }

    public void setSwitch_four_open(boolean switch_four_open) {
        this.switch_four_open = switch_four_open;
    }


    public String getRemotecontrolUid() {
        return remotecontrolUid;
    }

    public void setRemotecontrolUid(String remotecontrolUid) {
        this.remotecontrolUid = remotecontrolUid;
    }

    public Device getGetwayDevice() {
        return getwayDevice;
    }

    public void setGetwayDevice(Device getwayDevice) {
        this.getwayDevice = getwayDevice;
    }

    public List<Info> getAlarmInfo() {
        return alarmInfo;
    }

    public void setAlarmInfo(List<Info> alarmInfo) {
        this.alarmInfo = alarmInfo;
    }

    public boolean isRemerberPassword() {
        return remerberPassword;
    }

    public void setRemerberPassword(boolean remerberPassword) {
        this.remerberPassword = remerberPassword;
    }

    public String getLockPassword() {
        return lockPassword;
    }

    public void setLockPassword(String lockPassword) {
        this.lockPassword = lockPassword;
    }



    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public String getOrg() {
        return Org;
    }

    public void setOrg(String org) {
        Org = org;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getCtrUid() {
        return CtrUid;
    }

    public void setCtrUid(String ctrUid) {
        CtrUid = ctrUid;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getVer() {
        return Ver;
    }

    public void setVer(String ver) {
        Ver = ver;
    }

    @Override
    public String toString() {
        return "SmartDev{" +
                "Uid='" + Uid + '\'' +
                ", CtrUid='" + CtrUid + '\'' +
                ", Type='" + Type + '\'' +
                ", subType='" + subType + '\'' +
                ", Status='" + Status + '\'' +
                ", Org='" + Org + '\'' +
                ", Ver='" + Ver + '\'' +
                ", name='" + name + '\'' +
                ", room=" + rooms.size() +
                '}';
    }
}

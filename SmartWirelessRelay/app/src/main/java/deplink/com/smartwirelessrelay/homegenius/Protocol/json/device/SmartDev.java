package deplink.com.smartwirelessrelay.homegenius.Protocol.json.device;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport.Info;

/**
 * Created by Administrator on 2017/10/30.
 */
public class SmartDev extends DataSupport implements Serializable{
    @Column(unique = true,nullable = false)
    private String Uid;
    private String CtrUid;
    private String Type;
    /*设备类型子类型，比如开关，下面又会分1,2,3，4,路开关*/
    private String subType;
    private String Status;
    private String Org;
    private String Ver;
    private String name;
    private List<Info> alarmInfo = new ArrayList<>();

    public List<Info> getAlarmInfo() {
        return alarmInfo;
    }

    public void setAlarmInfo(List<Info> alarmInfo) {
        this.alarmInfo = alarmInfo;
    }

    private String lockPassword="";
    private boolean remerberPassword=true;

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

    /**
     * 数据库中的关联关系必须要初始化好列表
     */
    @Column(nullable = false)
    private List<Room> rooms=new ArrayList<>();
    private String routerDeviceKey;

    public String getRouterDeviceKey() {
        return routerDeviceKey;
    }

    public void setRouterDeviceKey(String routerDeviceKey) {
        this.routerDeviceKey = routerDeviceKey;
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

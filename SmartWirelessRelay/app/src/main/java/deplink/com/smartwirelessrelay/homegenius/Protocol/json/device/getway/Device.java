package deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.getway;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.Room;

/**
 * Created by Administrator on 2017/10/30.
 */
public class Device extends DataSupport implements Serializable{
    @Column(unique = true,nullable = false)
    private String Uid;
    private String Status;
    private String name;
    /**
     * 网关的IP地址
     */
    private String ipAddress;
    private List<Room>roomList=new ArrayList<>();

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }


    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
    }

    @Override
    public String toString() {
        return "Device{" +
                "Uid='" + Uid + '\'' +
                ", Status='" + Status + '\'' +
                '}';
    }
}

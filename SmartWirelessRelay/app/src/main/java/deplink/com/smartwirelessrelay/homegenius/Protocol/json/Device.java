package deplink.com.smartwirelessrelay.homegenius.Protocol.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 */
public class Device implements Serializable{
    private String Uid;
    private String Ip;
    private String Status;
    private String index;

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getIp() {
        return Ip;
    }

    public void setIp(String ip) {
        Ip = ip;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Device{" +
                "Uid='" + Uid + '\'' +
                ", Ip='" + Ip + '\'' +
                ", Status='" + Status + '\'' +
                ", index='" + index + '\'' +
                '}';
    }
}

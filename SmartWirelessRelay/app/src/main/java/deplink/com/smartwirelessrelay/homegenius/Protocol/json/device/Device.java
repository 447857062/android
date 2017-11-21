package deplink.com.smartwirelessrelay.homegenius.Protocol.json.device;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 */
public class Device extends DataSupport implements Serializable{
    @Column(unique = true,nullable = false)
    private String Uid;
    private String Status;

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


    @Override
    public String toString() {
        return "Device{" +
                "Uid='" + Uid + '\'' +
                ", Status='" + Status + '\'' +
                '}';
    }
}

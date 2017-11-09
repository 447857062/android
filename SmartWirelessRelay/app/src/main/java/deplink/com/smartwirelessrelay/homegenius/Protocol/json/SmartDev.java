package deplink.com.smartwirelessrelay.homegenius.Protocol.json;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 */
public class SmartDev extends DataSupport implements Serializable{
    @Column(unique = true,nullable = false)
    private String DevUid;
    private String CtrUid;
    private String Type;
    private String Status;
    private String Org;

    public String getOrg() {
        return Org;
    }

    public void setOrg(String org) {
        Org = org;
    }

    public String getDevUid() {
        return DevUid;
    }

    public void setDevUid(String devUid) {
        DevUid = devUid;
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


    @Override
    public String toString() {
        return "SmartDev{" +
                "DevUid='" + DevUid + '\'' +
                ", CtrUid='" + CtrUid + '\'' +
                ", Type='" + Type + '\'' +
                ", Status='" + Status + '\'' +
                '}';
    }
}

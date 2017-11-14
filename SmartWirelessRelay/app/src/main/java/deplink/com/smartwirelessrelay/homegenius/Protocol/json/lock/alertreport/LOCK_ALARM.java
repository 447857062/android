package deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock.alertreport;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock.SmartLock;

/**
 * Created by Administrator on 2017/10/30.
 */
public class LOCK_ALARM extends DataSupport implements Serializable {
    //建立表关联
    private SmartLock mSmartLock;
    //别名 ,sqlite不能使用id名称
    @SerializedName("ID")
    private String alarm_id;
    private String Alarm_Type;
    private String time;
    private String userid;

    public String getAlarm_id() {
        return alarm_id;
    }

    public void setAlarm_id(String alarm_id) {
        this.alarm_id = alarm_id;
    }

    public String getAlarm_Type() {
        return Alarm_Type;
    }

    public void setAlarm_Type(String alarm_Type) {
        Alarm_Type = alarm_Type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public SmartLock getmSmartLock() {
        return mSmartLock;
    }

    public void setmSmartLock(SmartLock mSmartLock) {
        this.mSmartLock = mSmartLock;
    }

    @Override
    public String toString() {
        return "LOCK_ALARM{" +
                "mSmartLock=" + mSmartLock +
                ", alarm_id='" + alarm_id + '\'' +
                ", Alarm_Type='" + Alarm_Type + '\'' +
                ", time='" + time + '\'' +
                ", userid='" + userid + '\'' +
                '}';
    }
}

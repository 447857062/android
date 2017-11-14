package deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 */
public class LockHistory implements Serializable{
    private String time;
    private String userid;

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

    @Override
    public String toString() {
        return "LockHistory{" +
                "time='" + time + '\'' +
                ", userid='" + userid + '\'' +
                '}';
    }
}

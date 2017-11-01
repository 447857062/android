package deplink.com.smartwirelessrelay.homegenius.Protocol.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 */
public class Record implements Serializable{
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
        return "Record{" +
                "time='" + time + '\'' +
                ", userid='" + userid + '\'' +
                '}';
    }
}

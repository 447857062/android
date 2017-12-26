package deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/30.
 */
public class QueryWifiListResult implements Serializable{
    private String OP="REPORT";
    private String Method="WIFIRELAY";
    private long timestamp;
    private List<SSIDList>SSIDList;

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.SSIDList> getSSIDList() {
        return SSIDList;
    }

    public void setSSIDList(List<deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.SSIDList> SSIDList) {
        this.SSIDList = SSIDList;
    }

    public String getOP() {
        return OP;
    }

    public void setOP(String OP) {
        this.OP = OP;
    }

    public String getMethod() {
        return Method;
    }

    public void setMethod(String method) {
        Method = method;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp() {
        this.timestamp = timestamp;
    }
}

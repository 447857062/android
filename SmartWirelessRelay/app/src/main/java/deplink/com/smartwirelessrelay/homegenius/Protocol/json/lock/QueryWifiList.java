package deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 * 保存管理密码
 */
public class QueryWifiList implements Serializable{
    private String OP="QUERY";
    private String Method="WIFIRELAY";
    private long timestamp;
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

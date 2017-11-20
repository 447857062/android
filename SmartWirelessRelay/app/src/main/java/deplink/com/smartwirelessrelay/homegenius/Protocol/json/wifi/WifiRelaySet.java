package deplink.com.smartwirelessrelay.homegenius.Protocol.json.wifi;

/**
 * Created by Administrator on 2017/8/5.
 */
public class WifiRelaySet {
    private String OP="WAN";
    private String Method="SET";
    private long timestamp;
    private Proto Proto;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp() {
        this.timestamp = System.currentTimeMillis();
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

    public deplink.com.smartwirelessrelay.homegenius.Protocol.json.wifi.Proto getProto() {
        return Proto;
    }

    public void setProto(deplink.com.smartwirelessrelay.homegenius.Protocol.json.wifi.Proto proto) {
        Proto = proto;
    }
}

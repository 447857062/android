package deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 * 保存管理密码
 */
public class BindAppResult implements Serializable{
    private String OP="REPORT";
    private String Method="BindApp";
    private long timestamp;
    private String Result;
    private int Init;

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

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }

    public int getInit() {
        return Init;
    }

    public void setInit(int init) {
        Init = init;
    }

}

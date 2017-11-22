package deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 * 保存管理密码
 */
public class OpResult  implements Serializable{
    private String OP="REPORT";
    private String Method="SmartLock";
    private int Result;
    private int Cmd;

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

    public int getResult() {
        return Result;
    }

    public void setResult(int result) {
        Result = result;
    }

    public int getCmd() {
        return Cmd;
    }

    public void setCmd(int cmd) {
        Cmd = cmd;
    }

    @Override
    public String toString() {
        return "OpResult{" +
                "OP='" + OP + '\'' +
                ", Method='" + Method + '\'' +
                ", Result=" + Result +
                ", Cmd=" + Cmd +
                '}';
    }
}

package deplink.com.smartwirelessrelay.homegenius.Protocol.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 * 保存管理密码
 */
public class OpResult  implements Serializable{
    private String OP="REPORT";
    private String Method="SmartLock";
    private int Result;
    private String SmartUid;
    private String Command;
    private long timestamp_echo;

    public String getSmartUid() {
        return SmartUid;
    }

    public void setSmartUid(String smartUid) {
        SmartUid = smartUid;
    }

    public String getCommand() {
        return Command;
    }

    public void setCommand(String command) {
        Command = command;
    }

    public long getTimestamp_echo() {
        return timestamp_echo;
    }

    public void setTimestamp_echo(long timestamp_echo) {
        this.timestamp_echo = timestamp_echo;
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

    public int getResult() {
        return Result;
    }

    public void setResult(int result) {
        Result = result;
    }

    @Override
    public String toString() {
        return "OpResult{" +
                "OP='" + OP + '\'' +
                ", Method='" + Method + '\'' +
                ", Result=" + Result +
                ", SmartUid='" + SmartUid + '\'' +
                ", Command='" + Command + '\'' +
                ", timestamp_echo=" + timestamp_echo +
                '}';
    }
}

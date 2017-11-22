package deplink.com.smartwirelessrelay.homegenius.Protocol.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 * 保存管理密码
 */
public class RemoteControlOpResult implements Serializable{
    private String OP="REPORT";
    private String Method="";
    private long timestamp_echo;
    private String Command;
    private String Result;


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

    public long getTimestamp_echo() {
        return timestamp_echo;
    }

    public void setTimestamp_echo(long timestamp_echo) {
        this.timestamp_echo = timestamp_echo;
    }

    public String getCommand() {
        return Command;
    }

    public void setCommand(String command) {
        Command = command;
    }

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }
}

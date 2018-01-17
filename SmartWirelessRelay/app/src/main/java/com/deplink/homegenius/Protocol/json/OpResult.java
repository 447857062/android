package com.deplink.homegenius.Protocol.json;

import java.io.Serializable;
import java.util.List;

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
    private String SwitchStatus;
    private int RecordNum;
    private int LockStatus;

    public int getRecordNum() {
        return RecordNum;
    }

    public void setRecordNum(int recordNum) {
        RecordNum = recordNum;
    }

    public int getLockStatus() {
        return LockStatus;
    }

    public void setLockStatus(int lockStatus) {
        LockStatus = lockStatus;
    }

    private List<com.deplink.homegenius.Protocol.json.device.lock.SSIDList> SSIDList;

    public List<com.deplink.homegenius.Protocol.json.device.lock.SSIDList> getSSIDList() {
        return SSIDList;
    }

    public void setSSIDList(List<com.deplink.homegenius.Protocol.json.device.lock.SSIDList> SSIDList) {
        this.SSIDList = SSIDList;
    }

    public String getSwitchStatus() {
        return SwitchStatus;
    }

    public void setSwitchStatus(String switchStatus) {
        SwitchStatus = switchStatus;
    }

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
                ", SwitchStatus='" + SwitchStatus + '\'' +
                ", SSIDList=" + SSIDList +
                '}';
    }
}

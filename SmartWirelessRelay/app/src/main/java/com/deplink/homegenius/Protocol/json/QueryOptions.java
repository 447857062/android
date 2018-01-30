package com.deplink.homegenius.Protocol.json;

import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.Protocol.json.device.getway.GatwayDevice;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/30.
 */
public class QueryOptions implements Serializable {
    private String OP;
    private String Method;
    private String Command;
    private String UserID;
    private String ManagePwd;
    private String AuthPwd;
    private String Time;
    private String Result;
    private List<SmartDev>SmartDev;
    private List<com.deplink.homegenius.Protocol.json.device.getway.GatwayDevice> Device;
    private long timestamp;
    private String  AuthId;
    private String  Data;
    private int  yellow;
    private int  white;
    private int  open;
    private String SenderId;
    private int Query_Num;
    private int Total;

    public int getQuery_Num() {
        return Query_Num;
    }

    public void setQuery_Num(int query_Num) {
        Query_Num = query_Num;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }

    private com.deplink.homegenius.Protocol.json.wifi.Proto Proto;

    public com.deplink.homegenius.Protocol.json.wifi.Proto getProto() {
        return Proto;
    }

    public void setProto(com.deplink.homegenius.Protocol.json.wifi.Proto proto) {
        Proto = proto;
    }

    /**
     * 查询智能设备使用
     */
    private String SmartUid;
    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getYellow() {
        return yellow;
    }

    public void setYellow(int yellow) {
        this.yellow = yellow;
    }

    public int getWhite() {
        return white;
    }

    public void setWhite(int white) {
        this.white = white;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }
    public String getResult() {
        return Result;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp() {
        this.timestamp = System.currentTimeMillis()/1000;
    }

    public String getAuthId() {
        return AuthId;
    }

    public void setAuthId(String authId) {
        AuthId = authId;
    }

    public List<com.deplink.homegenius.Protocol.json.device.SmartDev> getSmartDev() {
        return SmartDev;
    }

    public void setSmartDev(List<com.deplink.homegenius.Protocol.json.device.SmartDev> smartDev) {
        SmartDev = smartDev;
    }

    public List<GatwayDevice> getDevice() {
        return Device;
    }

    public void setDevice(List<GatwayDevice> device) {
        Device = device;
    }

    public void setResult(String result) {
        Result = result;
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

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getManagePwd() {
        return ManagePwd;
    }

    public void setManagePwd(String managePwd) {
        ManagePwd = managePwd;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAuthPwd() {
        return AuthPwd;
    }

    public void setAuthPwd(String authPwd) {
        AuthPwd = authPwd;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    @Override
    public String toString() {
        return "QueryOptions{" +
                "OP='" + OP + '\'' +
                ", Method='" + Method + '\'' +
                ", Command='" + Command + '\'' +
                ", UserID='" + UserID + '\'' +
                ", ManagePwd='" + ManagePwd + '\'' +
                ", AuthPwd='" + AuthPwd + '\'' +
                ", Time='" + Time + '\'' +
                ", Result='" + Result + '\'' +
                ", SmartDev=" + SmartDev +
                ", Device=" + Device +
                ", timestamp=" + timestamp +
                ", AuthId='" + AuthId + '\'' +
                ", Data='" + Data + '\'' +
                ", yellow=" + yellow +
                ", white=" + white +
                ", open=" + open +
                ", SenderId='" + SenderId + '\'' +
                ", Proto=" + Proto +
                ", SmartUid='" + SmartUid + '\'' +
                '}';
    }
}

package deplink.com.smartwirelessrelay.homegenius.Protocol.json;

import java.io.Serializable;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.Device;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;

/**
 * Created by Administrator on 2017/10/30.
 */
public class QueryOptions implements Serializable {
    private String OP;
    private String Method;
    private String Command;
    private String UserID;
    private String ManagePasswd;
    private String AuthPwd;
    private String LimitedTime;
    private String Result;
    private List<SmartDev>SmartDev;
    private List<deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.Device> Device;
    private long timestamp;
    private String  AuthId;
    public String getResult() {
        return Result;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp() {
        this.timestamp = System.currentTimeMillis();
    }

    public String getAuthId() {
        return AuthId;
    }

    public void setAuthId(String authId) {
        AuthId = authId;
    }

    public List<deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev> getSmartDev() {
        return SmartDev;
    }

    public void setSmartDev(List<deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev> smartDev) {
        SmartDev = smartDev;
    }

    public List<Device> getDevice() {
        return Device;
    }

    public void setDevice(List<Device> device) {
        Device = device;
    }

    public void setResult(String result) {
        Result = result;
    }

    /**
     * 查询智能设备使用
     */
    private String SmartUid;

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

    public String getManagePasswd() {
        return ManagePasswd;
    }

    public void setManagePasswd(String managePasswd) {
        ManagePasswd = managePasswd;
    }

    public String getAuthPwd() {
        return AuthPwd;
    }

    public void setAuthPwd(String authPwd) {
        AuthPwd = authPwd;
    }

    public String getLimitedTime() {
        return LimitedTime;
    }

    public void setLimitedTime(String limitedTime) {
        LimitedTime = limitedTime;
    }
}

package com.deplink.homegenius.Protocol.json.device.share;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 */
public class UserShareInfo implements Serializable {
    private int shareid;
    private String username;
    private int issuper;
    private int status;

    public int getShareid() {
        return shareid;
    }

    public void setShareid(int shareid) {
        this.shareid = shareid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getIssuper() {
        return issuper;
    }

    public void setIssuper(int issuper) {
        this.issuper = issuper;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserShareInfo{" +
                "shareid=" + shareid +
                ", username='" + username + '\'' +
                ", issuper=" + issuper +
                ", status=" + status +
                '}';
    }
}

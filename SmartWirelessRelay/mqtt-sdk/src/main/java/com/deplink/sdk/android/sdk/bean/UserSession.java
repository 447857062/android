package com.deplink.sdk.android.sdk.bean;

import java.io.Serializable;
import java.util.List;

public class UserSession implements Serializable {
    private String status;

    private String msg;

    private String mobile;

    private List<String> servers;

    private String username;

    private String password;

    private String clientid;

    private List<String> topic_pub;

    private List<String> topic_sub;

    private String token;

    private String avatar;
    private String uuid;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    public List<String> getServers() {
        return this.servers;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getClientid() {
        return this.clientid;
    }

    public void setTopic_pub(List<String> topic_pub) {
        this.topic_pub = topic_pub;
    }

    public List<String> getTopic_pub() {
        return this.topic_pub;
    }

    public void setTopic_sub(List<String> topic_sub) {
        this.topic_sub = topic_sub;
    }

    public List<String> getTopic_sub() {
        return this.topic_sub;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "UserSession{" +
                "status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                ", mobile='" + mobile + '\'' +
                ", servers=" + servers +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", clientid='" + clientid + '\'' +
                ", topic_pub=" + topic_pub +
                ", topic_sub=" + topic_sub +
                ", token='" + token + '\'' +
                ", avatar='" + avatar + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }
}

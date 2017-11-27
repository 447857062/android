package com.deplink.sdk.android.sdk;

import java.util.List;

/**
 * Created by billy on 2016/8/8.
 */
public class MqttConfig {
    private List<String> servers;

    private String username;

    private String password;

    private String clientid;

    private List<String> topic_pub;

    private List<String> topic_sub;

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
}

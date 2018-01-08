package com.deplink.homegenius.activity.device.router.connectType;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/8/2.
 */
public class encryption implements Serializable{
    private boolean enabled;
    private AuthAlgs authAlgs;
    private String description;
    private boolean wep;
    private List<String> authSuites;
    private int wpa;
    private List<String> pairCiphers;
    private List<String> groupCiphers;
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public boolean getEnabled() {
        return enabled;
    }

    public void setAuthAlgs(AuthAlgs authAlgs) {
        this.authAlgs = authAlgs;
    }
    public AuthAlgs getAuthAlgs() {
        return authAlgs;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    public void setWep(boolean wep) {
        this.wep = wep;
    }
    public boolean getWep() {
        return wep;
    }

    public void setAuthSuites(List<String> authSuites) {
        this.authSuites = authSuites;
    }
    public List<String> getAuthSuites() {
        return authSuites;
    }

    public void setWpa(int wpa) {
        this.wpa = wpa;
    }
    public int getWpa() {
        return wpa;
    }

    public void setPairCiphers(List<String> pairCiphers) {
        this.pairCiphers = pairCiphers;
    }
    public List<String> getPairCiphers() {
        return pairCiphers;
    }

    public void setGroupCiphers(List<String> groupCiphers) {
        this.groupCiphers = groupCiphers;
    }
    public List<String> getGroupCiphers() {
        return groupCiphers;
    }

    @Override
    public String toString() {
        return "encryption{" +
                "enabled=" + enabled +
                ", authAlgs=" + authAlgs +
                ", description='" + description + '\'' +
                ", wep=" + wep +
                ", authSuites=" + authSuites +
                ", wpa=" + wpa +
                ", pairCiphers=" + pairCiphers +
                ", groupCiphers=" + groupCiphers +
                '}';
    }
}

package com.deplink.sdk.android.sdk.device.router;

import com.deplink.sdk.android.sdk.bean.DeviceCookieItem;
import com.deplink.sdk.android.sdk.bean.DeviceMemberItem;
import com.deplink.sdk.android.sdk.bean.DeviceUpgradeInfo;
import com.deplink.sdk.android.sdk.bean.TopicPair;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huqs on 2016/6/29.
 */
public abstract class BaseDevice {
    public static final String UPGRADE_STATE_READY = "ready";
    public static final String UPGRADE_STATE_RUNNING = "running";
    public static final String UPGRADE_STATE_FINISHED = "finished";
    public static final String UPGRADE_STATE_FAILED = "failed";
    /**
     * 产品编码
     */
    protected String productKey;
    /**
     * 设备名称
     */
    protected String deviceName;
    /**
     * 设备序列号
     */
    protected String deviceSn;
    /**
     * 设备编码
     */
    protected String deviceKey;
    /**
     * 设备型号
     */
    protected String type;
    /**
     * 设备版本
     */
    protected String version;
    /**
     * 设备MAC地址
     */
    protected String mac;
    /**
     * 设备是否在线
     */
    protected boolean isOnline;
    /**
     * 是否允许自动升级
     */
    protected boolean autoUpgrade;
    /**
     * 设备的通用mqtt通道
     */
    protected TopicPair common;
    /**
     * 设备与本APP用户的专用通道
     */
    protected TopicPair exclusive;
    /**
     * 设备cookie
     */
    protected List<DeviceCookieItem> cookies = new ArrayList<DeviceCookieItem>();
    /**
     * 设备成员
     */
    protected List<DeviceMemberItem> members = new ArrayList<DeviceMemberItem>();
    /**
     * 固件升级信息
     */
    protected DeviceUpgradeInfo upgradeInfo;

    public static final int MSG_DUMMY = 0;
    public static final int MSG_ONLINE_STATUS = 1000;

    public abstract int processMqttMsg(String topic, MqttMessage message);
    public String getProductKey() {
        return productKey;
    }
    public void setProductKey(String key) {
        productKey = key;
    }

    public String getName() {
        return deviceName;
    }
    public void setName(String name) {
        this.deviceName = name;
    }

    public String getDeviceSN() {
        return deviceSn;
    }
    public void setDeviceSN(String deviceSN) {
        this.deviceSn = deviceSN;
    }

    public void setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
    }
    public String getDeviceKey() {
        return deviceKey;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    public String getMac() {
        return mac;
    }
    public void setMac(String mac) {
        this.mac = mac;
    }

    public boolean getOnline() {
        return isOnline;
    }
    public void setOnline(boolean online) {
        this.isOnline = online;
    }

    public TopicPair getCommonCh() {
        return common;
    }
    public void setCommonCh(TopicPair ch) {
        common = ch;
    }

    public TopicPair getExclusiveCh() {
        return exclusive;
    }
    public void setExclusiveCh(TopicPair ch) {
        exclusive = ch;
    }

    public List<DeviceCookieItem> getCookies() {
        return cookies;
    }
    public void setCookies(List<DeviceCookieItem> cookies) {
        this.cookies = cookies;
    }

    public boolean getAutoUpgrade() {
        return autoUpgrade;
    }
    public void setAutoUpgrade(boolean auto) {
        autoUpgrade = auto;
    }

    public DeviceUpgradeInfo getUpgradeInfo() {
        return upgradeInfo;
    }

    public void setUpgradeInfo(DeviceUpgradeInfo info) {
        upgradeInfo = info;
    }

    @Override
    public String toString() {
        return "BaseDevice{" +
                "productKey='" + productKey + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceSn='" + deviceSn + '\'' +
                ", deviceKey='" + deviceKey + '\'' +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                ", mac='" + mac + '\'' +
                ", isOnline=" + isOnline +
                ", autoUpgrade=" + autoUpgrade +
                ", common=" + common +
                ", exclusive=" + exclusive +
                ", cookies=" + cookies +
                ", members=" + members +
                ", upgradeInfo=" + upgradeInfo +
                '}';
    }
}




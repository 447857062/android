package com.deplink.sdk.android.sdk.json;

import java.io.Serializable;

/**
 * Created by billy on 2016/8/18.
 */
public class DeviceImageUpgrade implements Serializable{
    private String OP;

    private String Method;

    private String ProductKey;

    private String SoftwareVersion;

    private String Protocol;

    private String ImgUrl;

    private String BakProtocol;

    private String BakImgUrl;

    private long FileLen;

    private String MD5;

    private String UpgradeTime;

    private int RandomTime;

    private int Type;

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

    public String getProductKey() {
        return ProductKey;
    }

    public void setProductKey(String productKey) {
        ProductKey = productKey;
    }

    public String getSoftwareVersion() {
        return SoftwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        SoftwareVersion = softwareVersion;
    }

    public String getProtocol() {
        return Protocol;
    }

    public void setProtocol(String protocol) {
        Protocol = protocol;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }

    public String getBakProtocol() {
        return BakProtocol;
    }

    public void setBakProtocol(String bakProtocol) {
        BakProtocol = bakProtocol;
    }

    public String getBakImgUrl() {
        return BakImgUrl;
    }

    public void setBakImgUrl(String bakImgUrl) {
        BakImgUrl = bakImgUrl;
    }

    public long getFileLen() {
        return FileLen;
    }

    public void setFileLen(long fileLen) {
        FileLen = fileLen;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public String getUpgradeTime() {
        return UpgradeTime;
    }

    public void setUpgradeTime(String upgradeTime) {
        UpgradeTime = upgradeTime;
    }

    public int getRandomTime() {
        return RandomTime;
    }

    public void setRandomTime(int randomTime) {
        RandomTime = randomTime;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    @Override
    public String toString() {
        return "DeviceImageUpgrade{" +
                "OP='" + OP + '\'' +
                ", Method='" + Method + '\'' +
                ", ProductKey='" + ProductKey + '\'' +
                ", SoftwareVersion='" + SoftwareVersion + '\'' +
                ", Protocol='" + Protocol + '\'' +
                ", ImgUrl='" + ImgUrl + '\'' +
                ", BakProtocol='" + BakProtocol + '\'' +
                ", BakImgUrl='" + BakImgUrl + '\'' +
                ", FileLen=" + FileLen +
                ", MD5='" + MD5 + '\'' +
                ", UpgradeTime='" + UpgradeTime + '\'' +
                ", RandomTime=" + RandomTime +
                ", Type=" + Type +
                '}';
    }
}

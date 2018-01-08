package com.deplink.homegenius.Protocol.json.http;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/22.
 * 测试码的意思是，选取某型号遥控器中的一个按键，给出对应的codeData，发送到智能遥控产生红外信号，
 * 观察被遥控设备是否有正确的反应。这里的codeData是已经拼装好的编码，可直接发送。
 若测试发现设备有正确的反应，则按这条数据里面的brandId和controlId去下载遥控器码库。
 */
public class TestCode implements Serializable{
    private String deviceType;
    private int codeID;
    private String brandEN;
    private String brandCN;
    private String brandID;
    private String keyName;
    private String codeData;
    private int codeLen;
    private String id;
    private int order;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getCodeID() {
        return codeID;
    }

    public void setCodeID(int codeID) {
        this.codeID = codeID;
    }

    public String getBrandEN() {
        return brandEN;
    }

    public void setBrandEN(String brandEN) {
        this.brandEN = brandEN;
    }

    public String getBrandCN() {
        return brandCN;
    }

    public void setBrandCN(String brandCN) {
        this.brandCN = brandCN;
    }

    public String getBrandID() {
        return brandID;
    }

    public void setBrandID(String brandID) {
        this.brandID = brandID;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getCodeData() {
        return codeData;
    }

    public void setCodeData(String codeData) {
        this.codeData = codeData;
    }

    public int getCodeLen() {
        return codeLen;
    }

    public void setCodeLen(int codeLen) {
        this.codeLen = codeLen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "TestCode{" +
                "deviceType='" + deviceType + '\'' +
                ", codeID=" + codeID +
                ", brandEN='" + brandEN + '\'' +
                ", brandCN='" + brandCN + '\'' +
                ", brandID='" + brandID + '\'' +
                ", keyName='" + keyName + '\'' +
                ", codeData='" + codeData + '\'' +
                ", codeLen=" + codeLen +
                ", id='" + id + '\'' +
                ", order=" + order +
                '}';
    }
}

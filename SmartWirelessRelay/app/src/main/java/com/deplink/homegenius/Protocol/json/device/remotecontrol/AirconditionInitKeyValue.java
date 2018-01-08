package com.deplink.homegenius.Protocol.json.device.remotecontrol;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 * 按键的学习状态
 * 不能使用byte数据
 */
public class AirconditionInitKeyValue extends DataSupport implements Serializable {
    private int tempature;
    private int wind;
    private int directionHand;
    private int directionAuto;
    private int keyPower;
    private int currentPressedKey;
    private int mode;

    public int getTempature() {
        return tempature;
    }

    public void setTempature(int tempature) {
        this.tempature = tempature;
    }

    public int getWind() {
        return wind;
    }

    public void setWind(int wind) {
        this.wind = wind;
    }

    public int getDirectionHand() {
        return directionHand;
    }

    public void setDirectionHand(int directionHand) {
        this.directionHand = directionHand;
    }

    public int getDirectionAuto() {
        return directionAuto;
    }

    public void setDirectionAuto(int directionAuto) {
        this.directionAuto = directionAuto;
    }

    public int getKeyPower() {
        return keyPower;
    }

    public void setKeyPower(int keyPower) {
        this.keyPower = keyPower;
    }

    public int getCurrentPressedKey() {
        return currentPressedKey;
    }

    public void setCurrentPressedKey(int currentPressedKey) {
        this.currentPressedKey = currentPressedKey;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}

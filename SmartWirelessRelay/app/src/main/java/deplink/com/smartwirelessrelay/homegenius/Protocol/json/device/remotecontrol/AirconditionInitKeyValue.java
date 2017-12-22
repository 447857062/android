package deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 * 按键的学习状态
 */
public class AirconditionInitKeyValue extends DataSupport implements Serializable{
   private byte tempature;
   private byte wind;
   private byte directionHand;
   private byte directionAuto;
   private byte power;
   private byte currentPressedKey;
   private byte mode;
    public byte getTempature() {
        return tempature;
    }

    public void setTempature(byte tempature) {
        this.tempature = tempature;
    }

    public byte getWind() {
        return wind;
    }

    public void setWind(byte wind) {
        this.wind = wind;
    }

    public byte getDirectionHand() {
        return directionHand;
    }

    public void setDirectionHand(byte directionHand) {
        this.directionHand = directionHand;
    }

    public byte getDirectionAuto() {
        return directionAuto;
    }

    public void setDirectionAuto(byte directionAuto) {
        this.directionAuto = directionAuto;
    }

    public byte getPower() {
        return power;
    }

    public void setPower(byte power) {
        this.power = power;
    }

    public byte getCurrentPressedKey() {
        return currentPressedKey;
    }

    public void setCurrentPressedKey(byte currentPressedKey) {
        this.currentPressedKey = currentPressedKey;
    }

    public byte getMode() {
        return mode;
    }

    public void setMode(byte mode) {
        this.mode = mode;
    }
}

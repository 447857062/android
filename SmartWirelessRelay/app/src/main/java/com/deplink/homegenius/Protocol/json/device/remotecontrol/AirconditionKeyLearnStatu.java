package com.deplink.homegenius.Protocol.json.device.remotecontrol;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 * 按键的学习状态
 */
public class AirconditionKeyLearnStatu extends DataSupport implements Serializable {
    private boolean key_tempature_reduce;
    private boolean key_tempature_plus;
    private boolean key_power;
    private boolean key_mode_hot;
    private boolean key_mode_cold;
    private boolean key_mode_dehumit;
    private boolean key_mode_wind;
    private boolean key_mode_auto;
    private boolean key_windspeed_hight;
    private boolean key_windspeed_middle;
    private boolean key_windspeed_low;
    private boolean key_windspeed_auto;
    private boolean key_winddirection_up;
    private boolean key_winddirection_middle;
    private boolean key_winddirection_down;
    private boolean key_winddirection_auto;
    /**
     * 当前code绑定的智能设备UID
     */
    private String mAirconditionUid;


    public String getmAirconditionUid() {
        return mAirconditionUid;
    }

    public void setmAirconditionUid(String mAirconditionUid) {
        this.mAirconditionUid = mAirconditionUid;
    }
    public void seAllKeyLearned() {
        key_tempature_reduce = true;
        key_tempature_plus = true;
        key_power = true;
        key_mode_hot = true;
        key_mode_cold = true;
        key_mode_dehumit = true;
        key_mode_wind = true;
        key_mode_auto = true;
        key_windspeed_hight = true;
        key_windspeed_middle = true;
        key_windspeed_low = true;
        key_windspeed_auto = true;
        key_winddirection_up = true;
        key_winddirection_middle = true;
        key_winddirection_down = true;
        key_winddirection_auto = true;
    }

    public boolean isKey_tempature_reduce() {
        return key_tempature_reduce;
    }

    public void setKey_tempature_reduce(boolean key_tempature_reduce) {
        this.key_tempature_reduce = key_tempature_reduce;
    }

    public boolean isKey_tempature_plus() {
        return key_tempature_plus;
    }

    public void setKey_tempature_plus(boolean key_tempature_plus) {
        this.key_tempature_plus = key_tempature_plus;
    }

    public boolean isKey_power() {
        return key_power;
    }

    public void setKey_power(boolean key_power) {
        this.key_power = key_power;
    }

    public boolean isKey_mode_hot() {
        return key_mode_hot;
    }

    public void setKey_mode_hot(boolean key_mode_hot) {
        this.key_mode_hot = key_mode_hot;
    }

    public boolean isKey_mode_cold() {
        return key_mode_cold;
    }

    public void setKey_mode_cold(boolean key_mode_cold) {
        this.key_mode_cold = key_mode_cold;
    }

    public boolean isKey_mode_dehumit() {
        return key_mode_dehumit;
    }

    public void setKey_mode_dehumit(boolean key_mode_dehumit) {
        this.key_mode_dehumit = key_mode_dehumit;
    }

    public boolean isKey_mode_wind() {
        return key_mode_wind;
    }

    public void setKey_mode_wind(boolean key_mode_wind) {
        this.key_mode_wind = key_mode_wind;
    }

    public boolean isKey_mode_auto() {
        return key_mode_auto;
    }

    public void setKey_mode_auto(boolean key_mode_auto) {
        this.key_mode_auto = key_mode_auto;
    }

    public boolean isKey_windspeed_hight() {
        return key_windspeed_hight;
    }

    public void setKey_windspeed_hight(boolean key_windspeed_hight) {
        this.key_windspeed_hight = key_windspeed_hight;
    }

    public boolean isKey_windspeed_middle() {
        return key_windspeed_middle;
    }

    public void setKey_windspeed_middle(boolean key_windspeed_middle) {
        this.key_windspeed_middle = key_windspeed_middle;
    }

    public boolean isKey_windspeed_low() {
        return key_windspeed_low;
    }

    public void setKey_windspeed_low(boolean key_windspeed_low) {
        this.key_windspeed_low = key_windspeed_low;
    }

    public boolean isKey_windspeed_auto() {
        return key_windspeed_auto;
    }

    public void setKey_windspeed_auto(boolean key_windspeed_auto) {
        this.key_windspeed_auto = key_windspeed_auto;
    }

    public boolean isKey_winddirection_up() {
        return key_winddirection_up;
    }

    public void setKey_winddirection_up(boolean key_winddirection_up) {
        this.key_winddirection_up = key_winddirection_up;
    }

    public boolean isKey_winddirection_middle() {
        return key_winddirection_middle;
    }

    public void setKey_winddirection_middle(boolean key_winddirection_middle) {
        this.key_winddirection_middle = key_winddirection_middle;
    }

    public boolean isKey_winddirection_down() {
        return key_winddirection_down;
    }

    public void setKey_winddirection_down(boolean key_winddirection_down) {
        this.key_winddirection_down = key_winddirection_down;
    }

    public boolean isKey_winddirection_auto() {
        return key_winddirection_auto;
    }

    public void setKey_winddirection_auto(boolean key_winddirection_auto) {
        this.key_winddirection_auto = key_winddirection_auto;
    }
}

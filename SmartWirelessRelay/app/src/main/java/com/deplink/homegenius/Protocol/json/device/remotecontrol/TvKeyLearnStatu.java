package com.deplink.homegenius.Protocol.json.device.remotecontrol;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 按键的学习状态
 */
public class TvKeyLearnStatu extends DataSupport implements Serializable{
   private boolean key_up;
   private boolean key_down;
   private boolean key_left;
   private boolean key_right;
   private boolean key_ok;
   private boolean key_power;
   private boolean key_ch_reduce;
   private boolean key_ch_plus;
   private boolean key_volum_reduce;
   private boolean key_volum_plus;
   private boolean key_mute;
   private boolean key_list;
   private boolean key_return;
   private boolean key_number_0;
   private boolean key_number_1;
   private boolean key_number_2;
   private boolean key_number_3;
   private boolean key_number_4;
   private boolean key_number_5;
   private boolean key_number_6;
   private boolean key_number_7;
   private boolean key_number_8;
   private boolean key_number_9;
    //-/-- 按钮
   private boolean key_number_left;
   private boolean key_number_avtv;
    public void seAllKeyLearned() {
        key_up=true;
        key_down=true;
        key_left=true;
        key_right=true;
        key_ok=true;
        key_power=true;
        key_ch_reduce=true;
        key_ch_plus=true;
        key_volum_reduce=true;
        key_volum_plus=true;
        key_mute=true;
        key_list=true;
        key_return=true;
        key_number_0=true;
        key_number_1=true;
        key_number_2=true;
        key_number_3=true;
        key_number_4=true;
        key_number_5=true;
        key_number_6=true;
        key_number_7=true;
        key_number_8=true;
        key_number_9=true;
        key_number_avtv=true;
        key_number_left=true;
    }
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
    public boolean isKey_up() {
        return key_up;
    }

    public void setKey_up(boolean key_up) {
        this.key_up = key_up;
    }

    public boolean isKey_down() {
        return key_down;
    }

    public void setKey_down(boolean key_down) {
        this.key_down = key_down;
    }

    public boolean isKey_left() {
        return key_left;
    }

    public void setKey_left(boolean key_left) {
        this.key_left = key_left;
    }

    public boolean isKey_right() {
        return key_right;
    }

    public void setKey_right(boolean key_right) {
        this.key_right = key_right;
    }

    public boolean isKey_ok() {
        return key_ok;
    }

    public void setKey_ok(boolean key_ok) {
        this.key_ok = key_ok;
    }

    public boolean isKey_power() {
        return key_power;
    }

    public void setKey_power(boolean key_power) {
        this.key_power = key_power;
    }

    public boolean isKey_ch_reduce() {
        return key_ch_reduce;
    }

    public void setKey_ch_reduce(boolean key_ch_reduce) {
        this.key_ch_reduce = key_ch_reduce;
    }

    public boolean isKey_ch_plus() {
        return key_ch_plus;
    }

    public void setKey_ch_plus(boolean key_ch_plus) {
        this.key_ch_plus = key_ch_plus;
    }

    public boolean isKey_volum_reduce() {
        return key_volum_reduce;
    }

    public void setKey_volum_reduce(boolean key_volum_reduce) {
        this.key_volum_reduce = key_volum_reduce;
    }

    public boolean isKey_volum_plus() {
        return key_volum_plus;
    }

    public void setKey_volum_plus(boolean key_volum_plus) {
        this.key_volum_plus = key_volum_plus;
    }

    public boolean isKey_mute() {
        return key_mute;
    }

    public void setKey_mute(boolean key_mute) {
        this.key_mute = key_mute;
    }

    public boolean isKey_list() {
        return key_list;
    }

    public void setKey_list(boolean key_list) {
        this.key_list = key_list;
    }

    public boolean isKey_return() {
        return key_return;
    }

    public void setKey_return(boolean key_return) {
        this.key_return = key_return;
    }

    public boolean isKey_number_0() {
        return key_number_0;
    }

    public void setKey_number_0(boolean key_number_0) {
        this.key_number_0 = key_number_0;
    }

    public boolean isKey_number_1() {
        return key_number_1;
    }

    public void setKey_number_1(boolean key_number_1) {
        this.key_number_1 = key_number_1;
    }

    public boolean isKey_number_2() {
        return key_number_2;
    }

    public void setKey_number_2(boolean key_number_2) {
        this.key_number_2 = key_number_2;
    }

    public boolean isKey_number_3() {
        return key_number_3;
    }

    public void setKey_number_3(boolean key_number_3) {
        this.key_number_3 = key_number_3;
    }

    public boolean isKey_number_4() {
        return key_number_4;
    }

    public void setKey_number_4(boolean key_number_4) {
        this.key_number_4 = key_number_4;
    }

    public boolean isKey_number_5() {
        return key_number_5;
    }

    public void setKey_number_5(boolean key_number_5) {
        this.key_number_5 = key_number_5;
    }

    public boolean isKey_number_6() {
        return key_number_6;
    }

    public void setKey_number_6(boolean key_number_6) {
        this.key_number_6 = key_number_6;
    }

    public boolean isKey_number_7() {
        return key_number_7;
    }

    public void setKey_number_7(boolean key_number_7) {
        this.key_number_7 = key_number_7;
    }

    public boolean isKey_number_8() {
        return key_number_8;
    }

    public void setKey_number_8(boolean key_number_8) {
        this.key_number_8 = key_number_8;
    }

    public boolean isKey_number_9() {
        return key_number_9;
    }

    public void setKey_number_9(boolean key_number_9) {
        this.key_number_9 = key_number_9;
    }

    public boolean isKey_number_left() {
        return key_number_left;
    }

    public void setKey_number_left(boolean key_number_left) {
        this.key_number_left = key_number_left;
    }

    public boolean isKey_number_avtv() {
        return key_number_avtv;
    }

    public void setKey_number_avtv(boolean key_number_avtv) {
        this.key_number_avtv = key_number_avtv;
    }
}

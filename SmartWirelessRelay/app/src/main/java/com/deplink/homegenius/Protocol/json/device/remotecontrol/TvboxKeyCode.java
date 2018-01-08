package com.deplink.homegenius.Protocol.json.device.remotecontrol;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 * 按键的学习状态
 */
public class TvboxKeyCode extends DataSupport implements Serializable{
    private int groupData;
    private String keycode;
    //码不用组装直接发送
    private String key_up;
    private String key_down;
    private String key_left;
    private String key_right;
    private String key_ok;
    private String key_power;
    private String key_ch_reduce;
    private String key_ch_plus;
    private String key_volum_reduce;
    private String key_volum_plus;
    private String key_navi;
    private String key_list;
    private String key_return;
    private String key_number_0;
    private String key_number_1;
    private String key_number_2;
    private String key_number_3;
    private String key_number_4;
    private String key_number_5;
    private String key_number_6;
    private String key_number_7;
    private String key_number_8;
    private String key_number_9;
    /**
     * 当前code绑定的智能设备UID
     */
    private String mAirconditionUid;

    //手动学习的码不用组装直接发送


    public String getmAirconditionUid() {
        return mAirconditionUid;
    }

    public void setmAirconditionUid(String mAirconditionUid) {
        this.mAirconditionUid = mAirconditionUid;
    }
    public String getKeycode() {
        return keycode;
    }

    public void setKeycode(String keycode) {
        this.keycode = keycode;
    }

    public int getGroupData() {
        return groupData;
    }

    public void setGroupData(int groupData) {
        this.groupData = groupData;
    }

    public String getKey_up() {
        return key_up;
    }

    public void setKey_up(String key_up) {
        this.key_up = key_up;
    }

    public String getKey_down() {
        return key_down;
    }

    public void setKey_down(String key_down) {
        this.key_down = key_down;
    }

    public String getKey_left() {
        return key_left;
    }

    public void setKey_left(String key_left) {
        this.key_left = key_left;
    }

    public String getKey_right() {
        return key_right;
    }

    public void setKey_right(String key_right) {
        this.key_right = key_right;
    }

    public String getKey_ok() {
        return key_ok;
    }

    public void setKey_ok(String key_ok) {
        this.key_ok = key_ok;
    }

    public String getKey_power() {
        return key_power;
    }

    public void setKey_power(String key_power) {
        this.key_power = key_power;
    }

    public String getKey_ch_reduce() {
        return key_ch_reduce;
    }

    public void setKey_ch_reduce(String key_ch_reduce) {
        this.key_ch_reduce = key_ch_reduce;
    }

    public String getKey_ch_plus() {
        return key_ch_plus;
    }

    public void setKey_ch_plus(String key_ch_plus) {
        this.key_ch_plus = key_ch_plus;
    }

    public String getKey_volum_reduce() {
        return key_volum_reduce;
    }

    public void setKey_volum_reduce(String key_volum_reduce) {
        this.key_volum_reduce = key_volum_reduce;
    }

    public String getKey_volum_plus() {
        return key_volum_plus;
    }

    public void setKey_volum_plus(String key_volum_plus) {
        this.key_volum_plus = key_volum_plus;
    }

    public String getKey_navi() {
        return key_navi;
    }

    public void setKey_navi(String key_navi) {
        this.key_navi = key_navi;
    }

    public String getKey_list() {
        return key_list;
    }

    public void setKey_list(String key_list) {
        this.key_list = key_list;
    }

    public String getKey_return() {
        return key_return;
    }

    public void setKey_return(String key_return) {
        this.key_return = key_return;
    }

    public String getKey_number_0() {
        return key_number_0;
    }

    public void setKey_number_0(String key_number_0) {
        this.key_number_0 = key_number_0;
    }

    public String getKey_number_1() {
        return key_number_1;
    }

    public void setKey_number_1(String key_number_1) {
        this.key_number_1 = key_number_1;
    }

    public String getKey_number_2() {
        return key_number_2;
    }

    public void setKey_number_2(String key_number_2) {
        this.key_number_2 = key_number_2;
    }

    public String getKey_number_3() {
        return key_number_3;
    }

    public void setKey_number_3(String key_number_3) {
        this.key_number_3 = key_number_3;
    }

    public String getKey_number_4() {
        return key_number_4;
    }

    public void setKey_number_4(String key_number_4) {
        this.key_number_4 = key_number_4;
    }

    public String getKey_number_5() {
        return key_number_5;
    }

    public void setKey_number_5(String key_number_5) {
        this.key_number_5 = key_number_5;
    }

    public String getKey_number_6() {
        return key_number_6;
    }

    public void setKey_number_6(String key_number_6) {
        this.key_number_6 = key_number_6;
    }

    public String getKey_number_7() {
        return key_number_7;
    }

    public void setKey_number_7(String key_number_7) {
        this.key_number_7 = key_number_7;
    }

    public String getKey_number_8() {
        return key_number_8;
    }

    public void setKey_number_8(String key_number_8) {
        this.key_number_8 = key_number_8;
    }

    public String getKey_number_9() {
        return key_number_9;
    }

    public void setKey_number_9(String key_number_9) {
        this.key_number_9 = key_number_9;
    }
}

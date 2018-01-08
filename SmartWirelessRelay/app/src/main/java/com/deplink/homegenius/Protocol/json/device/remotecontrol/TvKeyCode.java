package com.deplink.homegenius.Protocol.json.device.remotecontrol;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 * 按键的学习状态
 */
public class TvKeyCode extends DataSupport implements Serializable{
    private int groupData;
    private String keycode;
    //码不用组装直接发送
    private String data_key_vol_reduce;
    private String data_key_ch_add;
    private String data_key_menu;
    private String data_key_ch_reduce;
    private String data_key_vol_add;
    private String data_key_power;
    private String data_key_mute;
    private String data_key_1;
    private String data_key_2;
    private String data_key_3;
    private String data_key_4;
    private String data_key_5;
    private String data_key_6;
    private String data_key_7;
    private String data_key_8;
    private String data_key_9;
    private String data_key_0;
    private String data_key_enter;//--/-按键
    private String data_key_avtv;
    private String data_key_back;
    private String data_key_sure;
    private String data_key_up;
    private String data_key_down;
    private String data_key_left;
    private String data_key_right;
    private String data_key_home;

    public String getData_key_home() {
        return data_key_home;
    }

    public void setData_key_home(String data_key_home) {
        this.data_key_home = data_key_home;
    }

    public String getData_key_vol_reduce() {
        return data_key_vol_reduce;
    }

    public void setData_key_vol_reduce(String data_key_vol_reduce) {
        this.data_key_vol_reduce = data_key_vol_reduce;
    }

    public String getData_key_ch_add() {
        return data_key_ch_add;
    }

    public void setData_key_ch_add(String data_key_ch_add) {
        this.data_key_ch_add = data_key_ch_add;
    }

    public String getData_key_menu() {
        return data_key_menu;
    }

    public void setData_key_menu(String data_key_menu) {
        this.data_key_menu = data_key_menu;
    }

    public String getData_key_ch_reduce() {
        return data_key_ch_reduce;
    }

    public void setData_key_ch_reduce(String data_key_ch_reduce) {
        this.data_key_ch_reduce = data_key_ch_reduce;
    }

    public String getData_key_vol_add() {
        return data_key_vol_add;
    }

    public void setData_key_vol_add(String data_key_vol_add) {
        this.data_key_vol_add = data_key_vol_add;
    }

    public String getData_key_power() {
        return data_key_power;
    }

    public void setData_key_power(String data_key_power) {
        this.data_key_power = data_key_power;
    }

    public String getData_key_mute() {
        return data_key_mute;
    }

    public void setData_key_mute(String data_key_mute) {
        this.data_key_mute = data_key_mute;
    }

    public String getData_key_1() {
        return data_key_1;
    }

    public void setData_key_1(String data_key_1) {
        this.data_key_1 = data_key_1;
    }

    public String getData_key_2() {
        return data_key_2;
    }

    public void setData_key_2(String data_key_2) {
        this.data_key_2 = data_key_2;
    }

    public String getData_key_3() {
        return data_key_3;
    }

    public void setData_key_3(String data_key_3) {
        this.data_key_3 = data_key_3;
    }

    public String getData_key_4() {
        return data_key_4;
    }

    public void setData_key_4(String data_key_4) {
        this.data_key_4 = data_key_4;
    }

    public String getData_key_5() {
        return data_key_5;
    }

    public void setData_key_5(String data_key_5) {
        this.data_key_5 = data_key_5;
    }

    public String getData_key_6() {
        return data_key_6;
    }

    public void setData_key_6(String data_key_6) {
        this.data_key_6 = data_key_6;
    }

    public String getData_key_7() {
        return data_key_7;
    }

    public void setData_key_7(String data_key_7) {
        this.data_key_7 = data_key_7;
    }

    public String getData_key_8() {
        return data_key_8;
    }

    public void setData_key_8(String data_key_8) {
        this.data_key_8 = data_key_8;
    }

    public String getData_key_9() {
        return data_key_9;
    }

    public void setData_key_9(String data_key_9) {
        this.data_key_9 = data_key_9;
    }

    public String getData_key_0() {
        return data_key_0;
    }

    public void setData_key_0(String data_key_0) {
        this.data_key_0 = data_key_0;
    }

    public String getData_key_enter() {
        return data_key_enter;
    }

    public void setData_key_enter(String data_key_enter) {
        this.data_key_enter = data_key_enter;
    }

    public String getData_key_avtv() {
        return data_key_avtv;
    }

    public void setData_key_avtv(String data_key_avtv) {
        this.data_key_avtv = data_key_avtv;
    }

    public String getData_key_back() {
        return data_key_back;
    }

    public void setData_key_back(String data_key_back) {
        this.data_key_back = data_key_back;
    }

    public String getData_key_sure() {
        return data_key_sure;
    }

    public void setData_key_sure(String data_key_sure) {
        this.data_key_sure = data_key_sure;
    }

    public String getData_key_up() {
        return data_key_up;
    }

    public void setData_key_up(String data_key_up) {
        this.data_key_up = data_key_up;
    }

    public String getData_key_down() {
        return data_key_down;
    }

    public void setData_key_down(String data_key_down) {
        this.data_key_down = data_key_down;
    }

    public String getData_key_left() {
        return data_key_left;
    }

    public void setData_key_left(String data_key_left) {
        this.data_key_left = data_key_left;
    }

    public String getData_key_right() {
        return data_key_right;
    }

    public void setData_key_right(String data_key_right) {
        this.data_key_right = data_key_right;
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

    @Override
    public String toString() {
        return "TvKeyCode{" +
                "groupData=" + groupData +
                ", keycode='" + keycode + '\'' +
                ", data_key_vol_reduce='" + data_key_vol_reduce + '\'' +
                ", data_key_ch_add='" + data_key_ch_add + '\'' +
                ", data_key_menu='" + data_key_menu + '\'' +
                ", data_key_ch_reduce='" + data_key_ch_reduce + '\'' +
                ", data_key_vol_add='" + data_key_vol_add + '\'' +
                ", data_key_power='" + data_key_power + '\'' +
                ", data_key_mute='" + data_key_mute + '\'' +
                ", data_key_1='" + data_key_1 + '\'' +
                ", data_key_2='" + data_key_2 + '\'' +
                ", data_key_3='" + data_key_3 + '\'' +
                ", data_key_4='" + data_key_4 + '\'' +
                ", data_key_5='" + data_key_5 + '\'' +
                ", data_key_6='" + data_key_6 + '\'' +
                ", data_key_7='" + data_key_7 + '\'' +
                ", data_key_8='" + data_key_8 + '\'' +
                ", data_key_9='" + data_key_9 + '\'' +
                ", data_key_0='" + data_key_0 + '\'' +
                ", data_key_enter='" + data_key_enter + '\'' +
                ", data_key_avtv='" + data_key_avtv + '\'' +
                ", data_key_back='" + data_key_back + '\'' +
                ", data_key_sure='" + data_key_sure + '\'' +
                ", data_key_up='" + data_key_up + '\'' +
                ", data_key_down='" + data_key_down + '\'' +
                ", data_key_left='" + data_key_left + '\'' +
                ", data_key_right='" + data_key_right + '\'' +
                ", mAirconditionUid='" + mAirconditionUid + '\'' +
                '}';
    }
}

package com.deplink.homegenius.constant;

/**
 * Created by Administrator on 2017/10/31.
 */
public  class ComandID {
    /**
     * 探测设备
     */
    public static final byte DETEC_DEV = (byte) 0x0;
    /**
     * 探测设备回应
     */
    public static final byte DETEC_DEV_RESPONSE = (byte) 0x1;
    /**
     * 查询
     */
    public static final byte QUERY_OPTION = (byte) 0x2;
    /**
     * 查询设备回应
     */
    public static final byte QUERY_DEV_RESPONSE = (byte) 0x3;
    /**
     * 设置命令
     */
    public static final byte SET_CMD = (byte) 0x4;
    /**
     * 设置结果返回
     */
    public static final byte SET_CMD_RESPONSE = (byte) 0x5;
    /**
     * 报警信息上报
     */
    public static final byte ALARM_REPORT = (byte) 0x6;
    /**
     * 绑定
     */
    public static final byte CMD_BIND = (byte) 0x7;
    /**
     * 绑定APP回应
     */
    public static final byte CMD_BIND_APP_RESPONSE = (byte) 0x8;
    /**
     * 心跳
     */
    public static final byte HEARTBEAT = (byte) 0x9;
    /**
     * 心跳回应
     */
    public static final byte HEARTBEAT_RESPONSE = (byte) 0xa;
    /**
     * 智能设备列表下发
     */
    public static final byte CMD_SEND_SMART_DEV = (byte) 0xb;
    /**
     * 智能设备列表回应
     */
    public static final byte CMD_SEND_SMART_DEV_RESPONSE = (byte) 0xc;
    /**
     * 扫描wifi列表
     */
    public static final byte CMD_DEV_SCAN_WIFI = (byte) 0xd;
    /**
     * 扫描wifi列表回应
     */
    public static final byte CMD_DEV_SCAN_WIFI_ACK = (byte) 0xe;
    /**
     * 设置wifi中继
     */
    public static final byte CMD_DEV_SET_WIFI = (byte) 0xf;
    /**
     * 设置wifi中继回应
     */
    public static final byte CMD_DEV_SET_WIFI_ACK = (byte) 0x10;


}

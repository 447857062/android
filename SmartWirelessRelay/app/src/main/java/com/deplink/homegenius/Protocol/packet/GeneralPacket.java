package com.deplink.homegenius.Protocol.packet;

import android.content.Context;

import com.deplink.homegenius.constant.ComandID;
import com.deplink.homegenius.util.PublicMethod;

import java.net.InetAddress;

/**
 * Created by benond on 2017/2/6.
 */

public class GeneralPacket extends BasicPacket {
    private Context mContext;
    public GeneralPacket(InetAddress ip, int port, Context context) {
        super(context, ip, port);
    }
    public GeneralPacket(Context context) {
        super(context);
    }
    /**
     * 绑定设备
     * @return
     */
    public int packBindUnbindAppPacket(String uid, byte cmdID,byte[]xdata) {
        return packBindData(uid,cmdID,xdata);
    }
    /**
     * 打包心跳包
     * @return
     */
    public int packHeathPacket() {
        return packData( null, ComandID.HEARTBEAT,false,null);
    }
    /**
     * 发送广播包,探测设备
     * ip 255.255.255.255
     * @return
     */
    public int packCheckPacketWithUID() {
        return packUdpDetectData();
    }
    /**
     * 查询设备列表
     * @return
     */
    public int packQueryDevListData(byte[]xdata,boolean addControlUid,String controlUid ) {

        return packData( xdata,ComandID.QUERY_OPTION,addControlUid,controlUid);
    }
    /**
     * 查询开锁记录
     * @return
     */
    public int packOpenLockListData(byte[]xdata,String controlUid) {

        return packData(xdata,ComandID.QUERY_OPTION,true,controlUid);
    }
    /**
     *设置智能设备参数
     * @return
     */
    public int packSetCmdData(byte[]xdata, String controlUid) {
        return packData(xdata,ComandID.SET_CMD,true,controlUid);
    }
    /**
     *智能设备列表下发
     * @return
     */
    public int packSendSmartDevsData( byte[]xdata) {
        return packData(xdata,ComandID.CMD_SEND_SMART_DEV,false,null);
    }
    public int packQueryWifiListData( byte[]xdata) {
        return packData(xdata,ComandID.CMD_DEV_SCAN_WIFI,false,null);
    }
    public int packRemoteControlData( byte[]xdata,String controlUid) {
        return packData(xdata,ComandID.SET_CMD,true,controlUid);
    }
    public int packSetWifiListData( byte[]xdata) {
        return packData(xdata,ComandID.CMD_DEV_SET_WIFI,false,null);
    }
    /**
     *设备列表下发
     * @return
     */
    public int packSendDevsData( byte[]xdata) {
        return packData( xdata,ComandID.CMD_SEND_SMART_DEV,false,null);
    }
    public static final byte FunWiFiConfig = (byte) 0xf0;
    public int packWiFiConfigPacket(long mac, byte type, byte ver, boolean isLocal, String SSID, String pwd, OnRecvListener listener) {
      //  this.listener = listener;
        if (SSID == null || pwd == null)
            return -1;

        if (SSID.length() == 0 || pwd.length() == 0)
            return -2;

        int xlen = 3 + SSID.length() + pwd.length();
        byte[] xdata = new byte[xlen];
        xdata[0] = 0x03;
        xdata[1] = (byte) (SSID.length() & 0xff);
        System.arraycopy(SSID.getBytes(), 0, xdata, 2, SSID.length());
        xdata[2 + SSID.length()] = (byte) (pwd.length() & 0xff);
        System.arraycopy(pwd.getBytes(), 0, xdata, 3 + SSID.length(), pwd.length());
        return packDoorbeelData(FunWiFiConfig, PublicMethod.getUuid(mContext), PublicMethod.getSeq(), xdata, isLocal, mac, type, ver);
    }
    public int packRebootWiFiConfigPacket(long mac, byte type, byte ver, boolean isLocal, OnRecvListener listener) {
        byte[] xdata = new byte[1];
        xdata[0] = 0x05;
     //   this.listener = listener;
        return packDoorbeelData(FunWiFiConfig, PublicMethod.getUuid(mContext), PublicMethod.getSeq(), xdata, isLocal, mac, type, ver);

    }
}

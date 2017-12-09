package deplink.com.smartwirelessrelay.homegenius.Protocol.packet;

import android.content.Context;

import java.net.InetAddress;

import deplink.com.smartwirelessrelay.homegenius.constant.ComandID;

/**
 * Created by benond on 2017/2/6.
 */

public class GeneralPacket extends BasicPacket {
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
        return packData( null, ComandID.HEARTBEAT,false);
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
    public int packQueryDevListData(byte[]xdata,boolean addControlUid) {

        return packData( xdata,ComandID.QUERY_OPTION,addControlUid);
    }
    /**
     * 查询开锁记录
     * @return
     */
    public int packOpenLockListData(byte[]xdata) {

        return packData(xdata,ComandID.QUERY_OPTION,true);
    }
    /**
     *设置智能设备参数
     * @return
     */
    public int packSetSmartLockData( byte[]xdata) {


        return packData(xdata,ComandID.SET_CMD,true);
    }
    /**
     *智能设备列表下发
     * @return
     */
    public int packSendSmartDevsData( byte[]xdata) {
        return packData(xdata,ComandID.CMD_SEND_SMART_DEV,true);
    }
    public int packQueryWifiListData( byte[]xdata) {
        return packData(xdata,ComandID.CMD_DEV_SCAN_WIFI,false);
    }
    public int packRemoteControlData( byte[]xdata) {
        return packData(xdata,ComandID.SET_CMD,true);
    }
    public int packSetWifiListData( byte[]xdata) {
        return packData(xdata,ComandID.CMD_DEV_SET_WIFI,false);
    }
    /**
     *设备列表下发
     * @return
     */
    public int packSendDevsData( byte[]xdata) {
        return packData( xdata,ComandID.CMD_SEND_SMART_DEV,false);
    }

}

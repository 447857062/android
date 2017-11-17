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
    public int packBindUnbindAppPacket(String uid, byte cmdID) {

        return packBindData(uid,cmdID);

    }

    /**
     * 打包心跳包
     * @return
     */
    public int packHeathPacket() {

        return packData( null, ComandID.HEARTBEAT);

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
    public int packQueryDevListData(byte[]xdata) {

        return packData( xdata,ComandID.QUERY_OPTION);
    }
    /**
     * 查询开锁记录
     * @return
     */
    public int packOpenLockListData(byte[]xdata) {

        return packData(xdata,ComandID.QUERY_OPTION);
    }
    /**
     *设置智能设备参数
     * @return
     */
    public int packSetSmartLockData( byte[]xdata) {


        return packData(xdata,ComandID.SET_CMD);
    }
    /**
     *智能设备列表下发
     * @return
     */
    public int packSendSmartDevsData( byte[]xdata) {
        return packData(xdata,ComandID.CMD_SEND_SMART_DEV);
    }
    /**
     *设备列表下发
     * @return
     */
    public int packSendDevsData( byte[]xdata) {
        return packData( xdata,ComandID.CMD_SEND_SMART_DEV);
    }

}

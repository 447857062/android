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
        byte[]ip = new byte[4];
        ip[0]=(byte)0xC0;
        ip[1]=(byte)0xA8;
        ip[2]=(byte) 0x44;
        ip[3]=(byte) 0xCD;
        return packBindData(ip,uid,cmdID);

    }

    /**
     * 打包心跳包
     * @return
     */
    public int packHeathPacket() {
        byte[]ip = new byte[4];
        ip[0]=(byte)0xC0;
        ip[1]=(byte)0xA8;
        ip[2]=(byte) 0x44;
        ip[3]=(byte) 0xCD;
        return packWirelessData(  ip,true,null, ComandID.HEARTBEAT);

    }

    /**
     * 发送广播包,探测设备
     * ip 255.255.255.255
     * @return
     */
    public int packCheckPacketWithUID() {
        byte[]ip = new byte[4];
        ip[0]=(byte) 0xFF;
        ip[1]=(byte)0xFF;
        ip[2]=(byte) 0xFF;
        ip[3]=(byte) 0xFF;
        return packWirelessData( ip,false,null,ComandID.DETEC_DEV);
    }

    /**
     * 查询设备列表
     * @return
     */
    public int packQueryDevListData(byte[]xdata) {
        byte[]ip = new byte[4];
        ip[0]=(byte) 0xFF;
        ip[1]=(byte)0xFF;
        ip[2]=(byte) 0xFF;
        ip[3]=(byte) 0xFF;
        return packWirelessData( ip,false,xdata,ComandID.QUERY_DEV);
    }
    /**
     *设置智能设备参数
     * @return
     */
    public int packSetSmartLockData( byte[]xdata) {

        byte[]ip = new byte[4];
        ip[0]=(byte) 0xFF;
        ip[1]=(byte)0xFF;
        ip[2]=(byte) 0xFF;
        ip[3]=(byte) 0xFF;
        return packWirelessData( ip,false,xdata,ComandID.SET_CMD);
    }
    /**
     *智能设备列表下发
     * @return
     */
    public int packSendSmartDevsData( byte[]xdata) {
        byte[]ip = new byte[4];
        ip[0]=(byte) 0xFF;
        ip[1]=(byte)0xFF;
        ip[2]=(byte) 0xFF;
        ip[3]=(byte) 0xFF;
        return packWirelessData( ip,false,xdata,ComandID.CMD_SEND_SMART_DEV);
    }

}

package deplink.com.smartwirelessrelay.homegenius.Protocol.packet;

import android.content.Context;

import java.net.InetAddress;

import deplink.com.smartwirelessrelay.homegenius.Protocol.interfaces.OnRecvListener;

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

    //tcp/ip连接获取uuid,IP地址目前是固定的,测试使用
    public int packWirelessPacket() {
        byte[]ip = new byte[4];
        ip[0]=(byte)0xC0;
        ip[1]=(byte)0xA8;
        ip[2]=(byte) 0x44;
        ip[3]=(byte) 0xCD;
        return packWirelessData(  ip,true,null,(byte) 0x0);

    }
    //tcp/ip连接获取uuid,IP地址目前是固定的,测试使用
    public int packBindPacket() {
        byte[]ip = new byte[4];
        ip[0]=(byte)0xC0;
        ip[1]=(byte)0xA8;
        ip[2]=(byte) 0x44;
        ip[3]=(byte) 0xCD;
        return packWirelessData(  ip,true,null,(byte) 0x7);

    }
    //tcp/ip连接获取uuid,IP地址目前是固定的,测试使用
    public int packHeathPacket() {
        byte[]ip = new byte[4];
        ip[0]=(byte)0xC0;
        ip[1]=(byte)0xA8;
        ip[2]=(byte) 0x44;
        ip[3]=(byte) 0xCD;
        return packWirelessData(  ip,true,null,(byte) 0x9);

    }

    /**
     * 发送广播包,探测设备
     * ip 255.255.255.255
     * @param listener
     * @return
     */
    public int packCheckPacketWithUID(OnRecvListener listener, byte[]xdata) {
        this.listener = listener;
        byte[]ip = new byte[4];
        ip[0]=(byte) 0xFF;
        ip[1]=(byte)0xFF;
        ip[2]=(byte) 0xFF;
        ip[3]=(byte) 0xFF;
        return packWirelessData( ip,false,/*xdata*/null,(byte)0x0);
    }
    /**
     * 发送广播包,查询设备
     * ip 255.255.255.255
     * @param listener
     * @return
     */
    public int packQueryData(OnRecvListener listener, byte[]xdata) {
        this.listener = listener;
        byte[]ip = new byte[4];
        ip[0]=(byte) 0xFF;
        ip[1]=(byte)0xFF;
        ip[2]=(byte) 0xFF;
        ip[3]=(byte) 0xFF;
        return packWirelessData( ip,false,xdata,(byte)0x0);
    }
    /**
     * 发送广播包,查询设备
     * ip 255.255.255.255
     * @param listener
     * @return
     */
    public int packQueryDevListData(OnRecvListener listener, byte[]xdata) {
        this.listener = listener;
        byte[]ip = new byte[4];
        ip[0]=(byte) 0xFF;
        ip[1]=(byte)0xFF;
        ip[2]=(byte) 0xFF;
        ip[3]=(byte) 0xFF;
        return packWirelessData( ip,false,xdata,(byte)0x02);
    }
    /**
     * 发送广播包,查询设备
     * ip 255.255.255.255
     * @param listener
     * @return
     */
    public int packSetSmartLockData(OnRecvListener listener, byte[]xdata) {
        this.listener = listener;
        byte[]ip = new byte[4];
        ip[0]=(byte) 0xFF;
        ip[1]=(byte)0xFF;
        ip[2]=(byte) 0xFF;
        ip[3]=(byte) 0xFF;
        return packWirelessData( ip,false,xdata,(byte)0x04);
    }
    /**
     * 发送广播包,查询设备
     * ip 255.255.255.255
     * @param listener
     * @return
     */
    public int packQueryRecordListData(OnRecvListener listener, byte[]xdata) {
        this.listener = listener;
        byte[]ip = new byte[4];
        ip[0]=(byte) 0xFF;
        ip[1]=(byte)0xFF;
        ip[2]=(byte) 0xFF;
        ip[3]=(byte) 0xFF;
        return packWirelessData( ip,false,xdata,(byte)0x02);
    }
    /**
     * 发送广播包,查询设备
     * ip 255.255.255.255
     * @param listener
     * @return
     */
    public int packSendSmartDevsData(OnRecvListener listener, byte[]xdata) {
        this.listener = listener;
        byte[]ip = new byte[4];
        ip[0]=(byte) 0xFF;
        ip[1]=(byte)0xFF;
        ip[2]=(byte) 0xFF;
        ip[3]=(byte) 0xFF;
        return packWirelessData( ip,false,xdata,(byte)0x11);
    }

}

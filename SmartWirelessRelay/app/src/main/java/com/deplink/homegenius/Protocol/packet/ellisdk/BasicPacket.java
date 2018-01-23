package com.deplink.homegenius.Protocol.packet.ellisdk;

import android.content.Context;
import android.util.Log;

import com.deplink.homegenius.util.DataExchange;
import com.deplink.homegenius.util.IPV4Util;
import com.deplink.homegenius.util.PublicMethod;

import java.net.DatagramPacket;
import java.net.InetAddress;


/**
 * Created by benond on 2017/2/6.
 */

public class BasicPacket {
    public static final String TAG = "BasicPacket";

    public static final int BasicLen = 37;

    public static final int MaxWiFiDevSendCount = 8;
    public static final int WiFiDevTimeout = 300;
    public static final int PacketWait = 0;
    public static final int PacketSend = 1;
    public static final int PacketTimeout = -1;


    //最终要发送的数据
    public byte[] data;
    //seq编号，方便返回的时候回掉到界面
    public int seq;
    /**
     * 要求返回的编号
     */
    public int seqfun;

    //回调调用者
    public com.deplink.homegenius.Protocol.packet.ellisdk.OnRecvListener listener;

    //设定超时时间
    private long idleTimeout = 800;
    //设定重发次数
    private long resendTimes = 8;

    //当前发送次数
    private long sendCount;
    //当前发送时间
    private long sendTime;

    public byte fun;
    public byte[] xdata;


    public InetAddress ip=null;
    public int port;
    //该条消息的建立时间
    public long createTime;

    public boolean isLocal;

    public long mac;
    public byte type;
    public byte ver;
    int frameLen;
    int frameCount;
    int uuid;
    int xdataLen;

    public boolean isFinish;
    public boolean isSetIp;

    public BasicPacket(Context context){
        this.mContext=context;
        isFinish = false;
        isSetIp = false;
        this.createTime = PublicMethod.getTimeMs();
    }


    public BasicPacket(Context context, InetAddress ip, int port) {
        this.mContext = context;
        isFinish = false;
        isSetIp = true;
        this.ip = ip;
        this.port = port;
        this.createTime = PublicMethod.getTimeMs();
    }

    /**
     * 基础打包函数
     **/
    public int packData(byte devfun, byte[] controlid, int seq, byte[] xdata, boolean isLocal, long mac, byte type, byte ver) {
        Log.i(TAG,"基础打包函数 devfun="+devfun+"mac="+mac+"type="+type+"ver="+ver);
        fun = devfun;
        this.xdata = xdata;
        byte[] tmp;
        int len = 0;

        isFinish = false;
        this.seq = seq;

        if (xdata != null)
            data = new byte[BasicLen + xdata.length];
        else {
            xdata = new byte[0];
            data = new byte[BasicLen];
        }
        //head

        this.isLocal = isLocal;
        if (isLocal) {
            data[len++] = 0x55;
            data[len++] = (byte) 0xaa;
        } else {
            data[len++] = 0x55;
            data[len++] = 0x66;
        }

        //data len
        tmp = DataExchange.intToTwoByte(BasicLen + xdata.length);
        data[len++] = tmp[0];
        data[len++] = tmp[1];//765920768
        //frame len
        data[len++] = 1;
        //frame num
        data[len++] = 0;
        //frame key
        data[len++] = 0;
        //mac
        this.mac = mac;
        System.arraycopy(DataExchange.longToEightByte(mac), 0, data, len, 8);
        len = len + 8;
        //frame quality
        data[len++] = 0;
        data[len++] = 0;
        //dev status
        data[len++] = 0;
        //dev code

        data[len++] = type;
        this.type=type;
        //dev ver
        data[len++] = ver;
        this.ver=ver;
        //dev fun
        data[len++] = devfun;
        //typebig
        data[len++] = 0;
        //typesmall
        data[len++] = 0;
        //control id
        System.arraycopy(controlid, 0, data, len, 4);
        len = len + 4;
        //reserved
        len = len + 4;
        //seq
        tmp = DataExchange.intToTwoByte(seq);
        data[len++] = tmp[0];
        data[len++] = tmp[1];
        //xdatalen
        tmp = DataExchange.intToTwoByte(xdata.length);
        data[len++] = tmp[0];
        data[len++] = tmp[1];
        //crc
        data[len++] = 0;
        data[len++] = 0;

        if (xdata.length > 0) {
            System.arraycopy(xdata, 0, data, len, xdata.length);
        }

        return data.length;
    }

    private Context mContext;

    public int unpackPacketWithData(byte[] data, int len) {
        if (len < BasicLen)
            return -1;
        if (data[0] != 0x55)
            return -2;
        if (data[1] != (byte) 0x66 && data[1] != (byte) 0xaa)
            return -3;
        IPV4Util ipv4Util = new IPV4Util();

        if (PublicMethod.checkConnectionState(mContext) == 1) {   //如果是WiFi情况下，则判断ip地址
            if (ipv4Util.checkSameSegment(this.ip.getHostAddress(), PublicMethod.getLocalIP(mContext))) {
                isLocal = true;
//            NSLog(@"收到一个本地包");
            } else {
                isLocal = false;
//            NSLog(@"收到一个远程包");
            }
        } else {
            isLocal = false;
//        NSLog(@"收到一个远程包");
        }
        byte[] bytesLen = new byte[2];
        System.arraycopy(data, 2, bytesLen, 0, 2);
        len = DataExchange.twoCharToInt(bytesLen);
        frameLen = data[4];
        frameCount = data[5];

        mac = DataExchange.eightByteToLong(data[7], data[8], data[9], data[10], data[11], data[12], data[13], data[14]);
        uuid = DataExchange.fourByteToInt(data[23], data[24], data[25], data[26]);
        fun = data[20];
        ver = data[19];
        type = data[18];
        frameLen = data[4];
        frameCount = data[5];
        seq = DataExchange.bytesToInt(data, 31, 2);
        xdataLen = DataExchange.bytesToInt(data, 33, 2);
        xdata = new byte[this.xdataLen];
        System.arraycopy(data, 37, xdata, 0, xdataLen);
        Log.i(TAG,"解码 mac="+mac+"uuid="+uuid+"fun="+fun+"ver="+ver+"type="+type);
        return 0;
    }


    /**
     * 当前包是否可以发送
     *
     * @return
     */
    public int isPacketCouldSend(long time) {
        if(sendCount>MaxWiFiDevSendCount)
            return PacketTimeout;
        if(Math.abs(time-sendTime)>WiFiDevTimeout){
            sendTime = time;
            return PacketSend;
        }
        return PacketWait;
    }

    /**
     * 判断包是否超时
     *
     * @return
     */
    public boolean isTimeout() {
        if (sendCount >= resendTimes) {
            if (Math.abs(PublicMethod.getTimeMs() - sendTime) > idleTimeout)
                return true;
        }
        return false;
    }


    /**
     * 得到需要发送的UDP包
     *
     * @return
     */
    public DatagramPacket getUdpData() {
        DatagramPacket packet = null;

        packet = new DatagramPacket(this.data, this.data.length, ip, port);

        return packet;
    }

    public DatagramPacket getUdpData(InetAddress ip, int port) {
        DatagramPacket packet = null;
        if (data != null)
            packet = new DatagramPacket(this.data, this.data.length, ip, port);
//        LogInfo.d(TAG,"网络包:"+DataExchange.byteArrayToHexString(data));
        return packet;
    }

    @Override
    public String toString() {
        StringBuilder hexString = new StringBuilder();
        hexString.append("设备ID: ")
                .append(Long.toHexString(mac))
                .append(" Action:")
                .append(Integer.toHexString(fun & 0xFF))
                .append(" " + "SEQ:")
                .append(seq)
                .append(" " + "TYPE:")
                .append(Integer.toHexString(type & 0xFF))
                .append(" XData:");
        if (xdata == null) return hexString.toString();
        for (byte b : xdata) {
            int intVal = b & 0xff;
            hexString.append(" 0x");
            if (intVal < 0x10)
                hexString.append("0");
            hexString.append(Integer.toHexString(intVal));
        }
        return hexString.toString();
    }


}

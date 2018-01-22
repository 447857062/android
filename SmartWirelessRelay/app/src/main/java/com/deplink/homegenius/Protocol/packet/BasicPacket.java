package com.deplink.homegenius.Protocol.packet;

import android.content.Context;
import android.util.Log;

import com.deplink.homegenius.constant.AppConstant;
import com.deplink.homegenius.constant.ComandID;
import com.deplink.homegenius.util.DataExchange;
import com.deplink.homegenius.util.IPV4Util;
import com.deplink.homegenius.util.Perfence;

import java.net.DatagramPacket;
import java.net.InetAddress;


/**
 * Created by benond on 2017/2/6.
 */

public class BasicPacket {
    public static final String TAG = "BasicPacket";
    //最终要发送的数据
    public byte[] data;
    public InetAddress ip = null;
    public int port;
    public boolean isFinish;
    private Context mContext;
    public byte fun;
    public byte[] xdata;
    public long mac;
    public BasicPacket(Context context) {
        this.mContext = context;
        isFinish = false;
    }

    public BasicPacket(Context context, InetAddress ip, int port) {
        this.mContext = context;
        isFinish = false;
        this.ip = ip;
        this.port = port;
    }

    /**
     * 基础打包函数
     *
     * @param addControlUid 是否需要加上控制id
     **/
    public int packData(byte[] xdata, byte cmdId, boolean addControlUid, String controlUid) {
        int len = 0;
        if (xdata != null)
            data = new byte[AppConstant.BASICLEGTH + xdata.length];
        else {
            data = new byte[AppConstant.BASICLEGTH];
        }
        //head
        data[len++] = (byte) 0xAA;
        data[len++] = (byte) 0xBB;
        data[len++] = (byte) 0xCC;
        data[len++] = (byte) 0xDD;
        //协议主版本号
        data[len++] = 0x01;
        //协议次版本号
        data[len++] = 0x0;
        // 命令id
        data[len++] = cmdId;
        // 设备uid，必填
        String uid;
        //tcp连接发送默认的uid
        uid = Perfence.getPerfence(AppConstant.PERFENCE_BIND_APP_UUID);
        System.arraycopy(uid.getBytes(), 0, data, len, 32);
        //uid32位，最后一个结束标志0
        len += 32;
        data[len++] = 0x0;//
        //设备ip（网络字节序），响应数据必填
        System.arraycopy(new byte[4], 0, data, len, 4);
        len += 4;
        //设备类型 0x0：中继器  0x1：app
        data[len++] = 0x01;
        //  SmartUid 智能设备uid,可以为空33,先为空调试
        if (addControlUid) {
            System.arraycopy(controlUid.getBytes(), 0, data, len, 23);
            len += 23;
            data[len++] = 0x0;
            data[len++] = 0x0;
            data[len++] = 0x0;
            data[len++] = 0x0;
            data[len++] = 0x0;
            data[len++] = 0x0;
            data[len++] = 0x0;
            data[len++] = 0x0;
            data[len++] = 0x0;
            data[len++] = 0x0;//结束符号
        } else {
            System.arraycopy(new byte[32], 0, data, len, 32);
            len += 32;
            data[len++] = 0x0;//结束符号
        }

        //数据长度，命令内容长度 (2)debug-0x0,0x0
        byte[] temp;
        if (xdata != null) {
            temp = DataExchange.intToTwoByte(xdata.length);
            data[len++] = temp[0];
            data[len++] = temp[1];
        } else {
            data[len++] = (byte) 0x0;
            data[len++] = (byte) 0x0;
        }
        //检验值crc，8位，占位
        //计算crc校验，data数据全部赋值后再计算(先不校验xdata数据)
     /*   CRC32 c = new CRC32();
        c.update(data);
        byte[] crc = DataExchange.longToEightByte(c.getValue());
       System.arraycopy(crc, 0, data, len, 8);
        len += 8;*/
        len += 4;
        //xdata数据
        if (xdata != null && xdata.length > 0) {
            System.arraycopy(xdata, 0, data, len, xdata.length);
        }
        Log.e(TAG, "打包数据长度data.length=" + data.length+"数据是："+DataExchange.byteArrayToHexString(data));
        return data.length;
    }
    /**
     * 基础打包函数
     **/
    public static final int BasicLen = 37;
    public int packDoorbeelData(byte devfun, byte[] controlid, int seq, byte[] xdata, boolean isLocal, long mac, byte type, byte ver) {
        Log.i(TAG,"基础打包函数 devfun="+devfun+"mac="+mac+"type="+type+"ver="+ver);
       fun = devfun;
       this.xdata = xdata;
        byte[] tmp;
        int len = 0;

        isFinish = false;
     //   this.seq = seq;

        if (xdata != null)
            data = new byte[BasicLen + xdata.length];
        else {
            xdata = new byte[0];
            data = new byte[BasicLen];
        }
        //head

     //   this.isLocal = isLocal;
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
   //     this.type=type;
        //dev ver
        data[len++] = ver;
    //    this.ver=ver;
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
    /**
     * udp探测包
     **/
    public int packUdpDetectData() {
        int len = 0;
        data = new byte[AppConstant.BASICLEGTH];
        //head
        data[len++] = (byte) 0xAA;
        data[len++] = (byte) 0xBB;
        data[len++] = (byte) 0xCC;
        data[len++] = (byte) 0xDD;
        //协议主版本号
        data[len++] = 0x01;
        //协议次版本号
        data[len++] = 0x0;
        // 命令id
        data[len++] = ComandID.DETEC_DEV;
        // 设备uid，必填
        String uid;
        //连接发送默认的uid
        uid = Perfence.getPerfence(AppConstant.PERFENCE_BIND_APP_UUID);
        System.arraycopy(uid.getBytes(), 0, data, len, 32);
        //uid32位，最后一个结束标志0
        len += 32;
        data[len++] = 0x0;//
        //设备ip（网络字节序），响应数据必填
        byte[] ip = new byte[4];
        ip[0] = (byte) 0xFF;
        ip[1] = (byte) 0xFF;
        ip[2] = (byte) 0xFF;
        ip[3] = (byte) 0xFF;
        System.arraycopy(ip, 0, data, len, 4);
        len += 4;
        //设备类型 0x0：中继器  0x1：app
        data[len++] = 0x01;
        //  SmartUid 智能设备uid,可以为空33,先为空调试
        System.arraycopy(new byte[32], 0, data, len, 32);
        len += 32;
        data[len++] = 0x0;//结束符号
        //数据长度，命令内容长度 (2)debug-0x0,0x0
        data[len++] = (byte) 0x0;
        data[len++] = (byte) 0x0;
        //检验值crc，8位，占位
        //计算crc校验，data数据全部赋值后再计算(先不校验xdata数据)
     /*   CRC32 c = new CRC32();
        c.update(data);
        byte[] crc = DataExchange.longToEightByte(c.getValue());
       System.arraycopy(crc, 0, data, len, 8);
        len += 8;*/
        len += 4;
        //xdata数据
        Log.i(TAG, "组装探测报");
        return data.length;
    }

    /**
     * 绑定设备
     **/
    public int packBindData(String uid, byte cmdId, byte[] xdata) {
        int len = 0;
        data = new byte[AppConstant.BASICLEGTH + xdata.length];
        //head
        data[len++] = (byte) 0xAA;
        data[len++] = (byte) 0xBB;
        data[len++] = (byte) 0xCC;
        data[len++] = (byte) 0xDD;
        //协议主版本号
        data[len++] = 0x01;
        //协议次版本号
        data[len++] = 0x0;
        // 命令id
        data[len++] = cmdId;
        // 设备uid，必填
        System.arraycopy(uid.getBytes(), 0, data, len, 32);
        //uid32位，最后一个结束标志0
        len += 32;
        data[len++] = 0x0;
        //设备ip（网络字节序），响应数据必填
        System.arraycopy(new byte[4], 0, data, len, 4);
        len += 4;
        //设备类型 0x0：中继器  0x1：app
        data[len++] = 0x01;
        //  SmartUid 智能设备uid,可以为空33,先为空调试,现在智能设备UID没有数据都是0
        System.arraycopy(new byte[32], 0, data, len, 32);
        len += 32;
        data[len++] = 0x0;//结束符号
        //数据长度，命令内容长度 (2)debug-0x0,0x0
        byte[] temp;
        temp = DataExchange.intToTwoByte(xdata.length);
        data[len++] = temp[0];
        data[len++] = temp[1];
        //检验值crc，8位，占位
        //计算crc校验，data数据全部赋值后再计算(先不校验xdata数据)
     /*   CRC32 c = new CRC32();
        c.update(data);
        byte[] crc = DataExchange.longToEightByte(c.getValue());
       System.arraycopy(crc, 0, data, len, 8);
        len += 8;*/
        len += 4;
        //xdata数据
        //xdata数据
        System.arraycopy(xdata, 0, data, len, xdata.length);
        return data.length;
    }


    /**
     * 得到需要发送的UDP包
     *
     * @return
     */
    public DatagramPacket getUdpData() {
        return new DatagramPacket(this.data, this.data.length, ip, port);
    }

    public byte type;
    public byte ver;
    int frameLen;
    int frameCount;
    public boolean isLocal;
    int xdataLen;
    int uuid;
    //seq编号，方便返回的时候回掉到界面
    public int seq;
    public int unpackPacketWithData(byte[] data, int len) {
        if (len < BasicLen)
            return -1;
        if (data[0] != 0x55)
            return -2;
        if (data[1] != (byte) 0x66 && data[1] != (byte) 0xaa)
            return -3;
        IPV4Util ipv4Util = new IPV4Util();

     /*   if (PublicMethod.checkConnectionState(mContext) == 1) {   //如果是WiFi情况下，则判断ip地址
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
        }*/
        isLocal=true;
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
}

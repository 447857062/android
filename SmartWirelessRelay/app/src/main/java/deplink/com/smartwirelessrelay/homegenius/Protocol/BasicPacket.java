package deplink.com.smartwirelessrelay.homegenius.Protocol;

import android.content.Context;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.InetAddress;

import deplink.com.smartwirelessrelay.homegenius.util.DataExchange;
import deplink.com.smartwirelessrelay.homegenius.util.IPV4Util;
import deplink.com.smartwirelessrelay.homegenius.util.PublicMethod;
import deplink.com.smartwirelessrelay.homegenius.util.SharedPreference;


/**
 * Created by benond on 2017/2/6.
 */

public class BasicPacket {
    public static final String TAG = "BasicPacket";

    public static final int BasicLen_wireless = 84;
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
    public OnRecvListener listener;

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
    public InetAddress ip = null;
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

    public BasicPacket(Context context) {
        this.mContext = context;
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
     * 中继器
     * 基础打包函数
     **/
    public int packWirelessData(byte[] ip, boolean istcp, byte[] xdata, byte cmdId) {
        int len = 0;
        if (xdata != null)
            data = new byte[BasicLen_wireless + xdata.length];
        else {
            data = new byte[BasicLen_wireless];
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
      //  Log.e(TAG, "中继器基础打包函数 tcp/ip连接的="+istcp);
        if (istcp) {
            //tcp连接发送默认的uid
            uid = "77685180654101946200316696479445";
            System.arraycopy(uid.getBytes(), 0, data, len, 32);
        } else {

            SharedPreference sharedPreference = new SharedPreference(mContext, "uid");
            uid = sharedPreference.getString("uid");
            Log.i(TAG,"uid.getBytes().length="+uid.getBytes().length+"uid="+DataExchange.byteArrayToHexString(uid.getBytes()));
            if (!uid.equals("")) {
                //获取到的数据前面乱码，取指定位置的数据
              //  byte[]str=new byte[32];
               // System.arraycopy(uid.getBytes(), 13, str, 0, 32);

              //  String temp=new String(str);
              //  Log.i(TAG,"发送的uid="+temp);
                System.arraycopy(uid.getBytes(), 0, data, len, 32);
            } else {
                //没有获取到uid
                uid = "77685180654101946200316696479445";
                System.arraycopy(uid.getBytes(), 0, data, len, 32);
            }
        }
        //uid32位，最后一个结束标志0
        len += 32;
        data[len++] = 0x0;//
        //设备ip（网络字节序），响应数据必填
        System.arraycopy(ip, 0, data, len, 4);
        len += 4;
        //设备类型 0x0：中继器  0x1：app
        data[len++] = 0x01;
        //  SmartUid 智能设备uid,可以为空33,先为空调试
        System.arraycopy(new byte[32], 0, data, len, 32);
        len += 32;
        data[len++] = 0x0;//结束符号
        //数据长度，命令内容长度 (2)debug-0x0,0x0
        byte[] temp;
        if (xdata != null) {

            temp = DataExchange.intToTwoByte(xdata.length);
            Log.i(TAG,"temp[0]"+temp[0]);
            Log.i(TAG,"temp[1]"+temp[1]);
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
        len+=4;
        //xdata数据
        if (xdata!=null && xdata.length > 0) {
            System.arraycopy(xdata, 0, data, len, xdata.length);
        }
        Log.e(TAG, "tcp/ip连接的="+istcp+"打包数据长度data.length=" + data.length + "打包数据:" + DataExchange.byteArrayToHexString(data));
        return data.length;
    }

    private Context mContext;

    public int unpackPacketWithData(byte[] data, int len) {
        if (data[0] != 0x55)
            return -2;
        if (data[1] != (byte) 0x66 && data[1] != (byte) 0xaa)
            return -3;
        IPV4Util ipv4Util = new IPV4Util();

        if (PublicMethod.checkConnectionState(mContext) == 1) {   //如果是WiFih情况下，则判断ip地址
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
        Log.i(TAG, "解码 mac=" + mac + "uuid=" + uuid + "fun=" + fun + "ver=" + ver + "type=" + type);
        if (fun == -18) {
            Log.e(TAG, "接收wifi列表数据Data:" + DataExchange.byteArrayToHexString(data));
        } else if (fun == -16) {
            Log.e(TAG, "接收wifi配置数据Data:" + DataExchange.byteArrayToHexString(data));
        }

        return 0;
    }

    /**
     * 解析设备ip和uid数据,ip和uid是对应的.
     *
     * @param data
     * @return
     */
    public byte[] unpackPacketWithWirelessData(byte[] data) {
        byte[] ipaddress = new byte[4];
        System.arraycopy(data, 40, ipaddress, 0,4);

        //探测设备回应的IP地址

            Log.i(TAG,"解析设备ip="+DataExchange.byteArrayToHexString(ipaddress)+"trans2IpV4Str="+IPV4Util.trans2IpV4Str(ipaddress));
        return ipaddress;
    }


    /**
     * 当前包是否可以发送
     *
     * @return
     */
    public int isPacketCouldSend(long time) {
        if (sendCount > MaxWiFiDevSendCount)
            return PacketTimeout;
        if (Math.abs(time - sendTime) > WiFiDevTimeout) {
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

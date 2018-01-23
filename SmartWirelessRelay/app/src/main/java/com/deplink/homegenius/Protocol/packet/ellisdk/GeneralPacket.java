package com.deplink.homegenius.Protocol.packet.ellisdk;

import android.content.Context;

import com.deplink.homegenius.util.PublicMethod;

import java.net.InetAddress;


/**
 * Created by benond on 2017/2/6.
 */

public class GeneralPacket extends BasicPacket {


    public final static byte CmdDevCheck = (byte) 0xff;
    public final static byte CmdDevCheckBack = (byte) 0xfe;
    public final static byte CmdDevAllowJoin = (byte) 0xfa;
    private Context mContext;

    public GeneralPacket(InetAddress ip, int port, Context context) {
        super(context, ip, port);
        mContext = context;
    }

    public GeneralPacket(Context context) {
        super(context);
        mContext = context;

    }


    public int packRegCheckPacketWithDev(OneDev dev) {
        return packData(EllESDK_DEF.FunReg, PublicMethod.getUuid(mContext), PublicMethod.getSeq(), null, false, dev.mac, dev.type, dev.ver);

    }

    public int packCheckPacketWithMac(long mac, byte type, byte ver, boolean isLocal, int seq, OnRecvListener listener) {
        this.listener = listener;
        return packData(EllESDK_DEF.FunCheck, PublicMethod.getUuid(mContext), seq, null, isLocal, mac, type, ver);
    }

    public int packCheckPacketWithDev(OneDev dev, boolean isLocal, OnRecvListener listener) {
        this.listener = listener;
        return packData(EllESDK_DEF.FunCheck, PublicMethod.getUuid(mContext), PublicMethod.getSeq(), null, isLocal, dev.mac, dev.type, dev.ver);
    }

    public int packRebootWiFiConfigPacket(long mac, byte type, byte ver, boolean isLocal,OnRecvListener listener) {
        byte[] xdata = new byte[1];
        xdata[0] = 0x05;
        this.listener = listener;
        return packData(EllESDK_DEF.FunWiFiConfig, PublicMethod.getUuid(mContext), PublicMethod.getSeq(), xdata, isLocal, mac, type, ver);

    }

    public int packGetWiFiConfigPacket(long mac, byte type, byte ver, boolean isLocal, OnRecvListener listener) {
        byte[] xdata = new byte[1];
        xdata[0] = 0x01;
        this.listener = listener;
        return packData(EllESDK_DEF.FunWiFiConfig, PublicMethod.getUuid(mContext), PublicMethod.getSeq(), xdata, isLocal, mac, type, ver);

    }

    public int packWiFiConfigPacket(long mac, byte type, byte ver, boolean isLocal, String SSID, String pwd, OnRecvListener listener) {
        this.listener = listener;

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
        return packData(EllESDK_DEF.FunWiFiConfig, PublicMethod.getUuid(mContext), PublicMethod.getSeq(), xdata, isLocal, mac, type, ver);
    }

    public int packGetWiFiSsidListPacket(long mac, byte type, byte ver, boolean isLocal, OnRecvListener listener) {
        this.listener = listener;
        return packData((byte) 0xEF, PublicMethod.getUuid(mContext), PublicMethod.getSeq(), xdata, isLocal, mac, type, ver);
    }

}

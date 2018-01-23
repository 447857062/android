package com.deplink.homegenius.Protocol.packet.ellisdk;


import android.util.Log;


/**
 * Created by benond on 2017/2/6.
 */

public class UnpacketWiFiConfig implements OnRecvListener {
    public static final byte FunWiFiConfig = (byte) 0xf0;
    private static final String TAG = "UnpacketWiFiConfig";
    public String ssid;
    public String pwd;
    public int step;
    @Override
    public void OnRecvData( BasicPacket packet) {
        Log.i(TAG,"UnpacketWiFiConfig");
        int ssidLen = 0;
        int pwdLen = 0;
        if (packet != null) {
            if (packet.fun == FunWiFiConfig) {
                switch (packet.xdata[0]) {
                    case 0x02:  //返回当前数据
                        ssidLen = packet.xdata[1];
                        byte[] byteSSID = new byte[ssidLen];
                        System.arraycopy(packet.xdata, 2, byteSSID, 0, ssidLen);
                        ssid = String.valueOf(byteSSID);
                        pwdLen = packet.xdata[ssidLen + 2];
                        byte[] bytePWD = new byte[pwdLen];
                        System.arraycopy(packet.xdata, 3 + ssidLen, bytePWD, 0, pwdLen);
                        pwd = String.valueOf(bytePWD);
                        step = 2;
                        Log.e(TAG, "收到设备:%lld 的WiFi配置信息:%@  %@ " + packet.mac + ":" + ssid + ":" + pwd);
                        return;
                    case 0x04:  //配置成功
                        step = 4;
                        Log.e(TAG, "设备"+packet.mac+"WiFi配置成功");
                        return;
                    default:
                        step = -1;
                        break;
                }
            }
        }

    }
}
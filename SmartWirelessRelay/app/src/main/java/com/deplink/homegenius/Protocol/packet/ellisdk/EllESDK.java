package com.deplink.homegenius.Protocol.packet.ellisdk;

import android.content.Context;
import android.util.Log;

import java.util.List;


/**
 * Created by benond on 2017/2/7.
 */

public class EllESDK {//
    private static final String TAG = "EllESDK";
    private static EllE_Listener elleListener;

    private DevStatus devStatus;

    private UdpPacket udpPacket;

    private static EllESDK instance;

    private EllESDK() {
    }
    public static synchronized EllESDK getInstance() {
        if (instance == null) {
            instance = new EllESDK();
        }
        return instance;
    }
    //初始化SDK
    public int InitEllESDK(Context context, EllE_Listener listener) {
        //发送任务队列
        if (udpPacket == null) {
            udpPacket = new UdpPacket(context);
            udpPacket.start();
        }
        //启动状态查询任务
        if (devStatus == null) {
            devStatus = new DevStatus(context, udpPacket);
            devStatus.open();
        }
        elleListener = listener;
        if (listener == null)
            Log.e("info", "没有回调 SDK 会出现异常");
        return 0;
    }

    public void setElleListener(EllE_Listener elleListener) {
        EllESDK.elleListener = elleListener;
    }

    public int startSearchDevs() {
        Log.i(TAG, "startSearchDevs");
        //启动状态查询任务
        //if (devStatus != null) {
        devStatus.close();
        devStatus.startSearch();
        devStatus.open();
        // }
        return 0;
    }

    public int stopSearchDevs() {
        devStatus.stopSearch();
        return 0;
    }

    public List<OneDev> getNewDevs() {
        return devStatus.getNewDevs();
    }

    //设置设备的WiFi参数 -- 阻塞方式 -- 外部调用建议使用线程
    public int setDevWiFiConfigWithMac(long mac, byte type, byte ver, WIFIData wifiData) {
        return devStatus.setDevWiFiConfigWithMac(mac, type, ver, wifiData);
    }

    //设置设备的WiFi参数 -- 阻塞方式 -- 外部调用建议使用线程
    public WIFIData getDevWiFiConfigWithMac(long mac, byte type, byte ver) {
        return devStatus.getDevWiFiConfigWithMac(mac, type, ver);
    }

    //添加需要通讯的设备进入队列
    public int addDevToCommWithMac(long mac, byte type, byte ver) {
        return devStatus.addDevWithMac(mac, type, ver);
    }
    //内部用
    public static void findDeviceWithMac(long mac, byte type, byte ver) {
        elleListener.searchDevCBS(mac, type, ver);
    }

    public static void getNewPacket(BasicPacket packet) {
        elleListener.onRecvEllEPacket(packet);
    }


    public List getDevWiFiListWithMac(long mac, byte type, byte ver) {
        return devStatus.getDevWiFiListWithMac(mac, type, ver);
    }


}

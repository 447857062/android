package com.deplink.homegenius.Protocol.packet.ellisdk;

import android.content.Context;
import android.util.Log;

import com.deplink.homegenius.util.DataExchange;
import com.deplink.homegenius.util.PublicMethod;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by benond on 2017/2/6.
 */

public class DevStatus {

    private static final String TAG = "DevStatus";
    public static DevList devs;
    static Timer timer;
    static InetAddress regServerIP;
    public static List<OneDev> newDevs;
    public static List<String> ssids;
    public static int searchCount;
    int curNetWork;
    public DatagramSocket dataSocket;
    public Context mContext;
    private UdpPacket udp;

    public DevStatus(Context context, UdpPacket udpPacket) {
        mContext = context;
        udp = udpPacket;
        try {
            dataSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    class timerTimeoutTask extends TimerTask {
        @Override
        public void run() {
            Log.d(TAG, "搜索设备中，timerTimeoutTask");
            timerTimeout();
        }
    }

    public void timerTimeout() {
        //NSLog(@"设备状体定时器运行正常");
        //先判断当前的网络状态，没有网络则不执行设备的检查
        //当前如果是WiFi的话，则优先执行本地查找，确定本地没有设备后再执行远程查找
        //当前如果是流量模式的话，则只执行远程查询
        int net = PublicMethod.checkConnectionState(mContext);
        //更新下本地的网络状态和IP地址
        PublicMethod.getLocalIP(mContext);
        switch (net) {
            case -1:
                return;
            case 2:
                mobileCheckHandler();
                break;
            case 1:         //WiFi模式
            case 3:         //WiFi模式
                wifiCheckHandler();
                break;
            default:
                break;
        }
    }


    public static void dealRegBackPacket(BasicPacket packet) {
        OneDev dev = devs.getOneDev(packet.mac);
        if (dev != null) {
            try {
                byte[] byteIp = new byte[4];
                byte[] bytePort = new byte[4];
                System.arraycopy(packet.xdata, 0, byteIp, 0, 4);
                System.arraycopy(packet.xdata, 4, byteIp, 0, 2);
                dev.remoteIP = InetAddress.getByName(DataExchange.charToIp(byteIp));
                dev.remotePort = DataExchange.twoCharToInt(bytePort);

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    public static void dealDataBackPacket(final BasicPacket packet) {
            boolean find = false;
            for (int i = 0; i < newDevs.size(); i++) {
                OneDev xdev = newDevs.get(i);
                if (xdev.mac == packet.mac) {
                    find = true;
                    break;
                }
            }
            Log.i(TAG,"find="+find);
            if (!find) {
                OneDev xdev = new OneDev(packet.mac, packet.type, packet.ver);
                newDevs.add(xdev);
                devs.addDevWithMac(packet.mac, packet.type, packet.ver);
                Handler_Background.execute(new Runnable() {
                    @Override
                    public void run() {
                        //获取设备列表
                        EllESDK.findDeviceWithMac(packet.mac, packet.type, packet.ver);
                    }
                }, 150);
            }

        OneDev dev = devs.getOneDev(packet.mac);
        if (dev != null) {
            if (packet.isLocal) {
                dev.freshLocalTime(packet.createTime);
            } else {
                dev.freshRemoteTime(packet.createTime);
            }
        }
    }
    public int delDevFromCommWithMac(long mac) {
        return devs.delDevFromCommWithMac(mac);
    }
    public void wifiCheckHandler() {
        long curtiem = PublicMethod.getTimeMs();
            for (int i = 0; i < devs.devs.size(); i++) {
                OneDev dev = devs.devs.get(i);
                if (dev != null && (dev.getDevStatus(curtiem) != OneDev.ConnTypeLocal) && dev.remoteIP != null) {   //连接方式不等于本地的时候，就需要去远端查询
                    GeneralPacket packet = new GeneralPacket(dev.remoteIP, dev.remotePort, mContext);
                    packet.packCheckPacketWithDev(dev, false, null);
                    udp.writeNet(packet);
                }
            }

        GeneralPacket packet;
        try {
            //发送一个局域网查询包
            Log.i(TAG, "搜索设备中，发送一个局域网查询包");
            packet = new GeneralPacket(InetAddress.getByName("255.255.255.255"), EllESDK_DEF.LocalConPort, mContext);
            packet.packCheckPacketWithMac(0, (byte) 0, (byte) 0, true, 0, null);
            udp.writeNet(packet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void mobileCheckHandler() {
        for (int i = 0; i < devs.devs.size(); i++) {
            OneDev dev = devs.devs.get(i);
            if (dev != null && dev.remoteIP != null) {
                GeneralPacket packet = new GeneralPacket(dev.remoteIP, dev.remotePort, mContext);
                packet.packCheckPacketWithDev(dev, false, null);
                udp.writeNet(packet);
            }
        }
    }

    public void open() {
        Log.i(TAG, "搜索设备中，open");
        curNetWork = PublicMethod.checkConnectionState(mContext);
        newDevs = new ArrayList<>();
        ssids = new ArrayList<>();
        devs = new DevList();
        regServerIP = null;
        timer = new Timer();
        timer.schedule(new timerTimeoutTask(), 1000, 1000);

    }


    public void close() {
        if (timer != null)
            timer.cancel();
    }


    public void startSearch() {

        ssids.clear();
        newDevs.clear();
        searchCount = 0;

    }


    public void stopSearch() {

        ssids.clear();
        searchCount = 0;
        newDevs.clear();
        close();
    }

    public List<String> getSSIDs() {
        return ssids;
    }

    public int addDevWithMac(long mac, byte type, byte ver) {
        return devs.addDevWithMac(mac, type, ver);

    }

    public static OneDev getOneDev(long mac) {
        return devs.getOneDev(mac);
    }

    public static int statusDealPacket(BasicPacket packet) {
        switch (packet.fun) {
            case EllESDK_DEF.FunRegBack:    //返回的注册包
                dealRegBackPacket(packet);
                break;
            case EllESDK_DEF.FunCheckBack:
                Log.i(TAG, "statusDealPacket FunCheckBack mac=" + packet.mac + "type=" + packet.type + "ver=" + packet.ver);
                dealDataBackPacket(packet);
                break;
            case (byte) 0xee:  //wifi信息返回的包
                //  dealWiFiSsidListPacket(packet);
                break;
            case (byte) 0xf0:  //
                Log.i(TAG, "wifi config 返回的包");
                break;
        }
        return 0;
    }

    //得到设备的WiFi参数 -- 阻塞方式 -- 外部调用建议使用线程
    public WIFIData getDevWiFiConfigWithMac(long mac, byte type, byte ver) {
        Log.i(TAG,"得到设备的WiFi参数");
        WIFIData wifiData = null;
        OneDev dev = DevStatus.getOneDev(mac);
        int netStatus = dev.getDevStatus(PublicMethod.getTimeMs());
        UnpacketWiFiConfig unpack = new UnpacketWiFiConfig();
        //设备存在，并且设备在线，发起读取设备WiFi的任务
        GeneralPacket packet;
        try {
            packet = new GeneralPacket(InetAddress.getByName("255.255.255.255"), EllESDK_DEF.LocalConPort, mContext);
            packet.packGetWiFiConfigPacket(mac, type, ver, true, unpack);
            udp.writeNet(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean isFinish = false;
        int count = 0;
        while (!isFinish) {
            count++;
            switch (unpack.step) {
                case 2:
                    wifiData.pwd = unpack.ssid;
                    wifiData.ssid = unpack.ssid;
                    return wifiData;
                case 4:
                    isFinish = true;
                    break;
                case -1:
                    isFinish = true;
                    break;
                default:
                    break;
            }
            if (count > 800) {
                isFinish = true;
            }
        }
        return null;

    }

    //设置设备的WiFi参数 -- 阻塞方式 -- 外部调用建议使用线程
    public int setDevWiFiConfigWithMac(long mac, byte type, byte ver, WIFIData wifiData) {
        OneDev dev = DevStatus.getOneDev(mac);
        String ssid = wifiData.ssid;
        String pwd = wifiData.pwd;
        int netStatus = dev.getDevStatus(PublicMethod.getTimeMs());
        UnpacketWiFiConfig unpack = new UnpacketWiFiConfig();
        GeneralPacket rebootPacket = null;
        Log.i(TAG, "setDevWiFiConfigWithMac netStatus=" + netStatus);
        //设备存在，并且设备在线，发起读取设备WiFi的任务
        GeneralPacket packet = new GeneralPacket(mContext);
        if (netStatus == OneDev.ConnTypeLocal) {
            try {
                rebootPacket = new GeneralPacket(InetAddress.getByName("255.255.255.255"), EllESDK_DEF.LocalConPort, mContext);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            packet.packWiFiConfigPacket(mac, type, ver, true, ssid, pwd, unpack);
            rebootPacket.packRebootWiFiConfigPacket(mac, type, ver, true, unpack);
        } else if (netStatus == OneDev.ConnTypeRemote) {
            rebootPacket = new GeneralPacket(dev.remoteIP, dev.remotePort, mContext);
            packet.packWiFiConfigPacket(mac, type, ver, false, ssid, pwd, unpack);
            rebootPacket.packRebootWiFiConfigPacket(mac, type, ver, false, unpack);
        } else {
            return -1;
        }
        udp.writeNet(packet);
        boolean isFinish = false;
        int count = 0;
        boolean isSetOk = false;
        while (!isFinish) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
            switch (unpack.step) {
                case 2:
                    return 0;
                case 4:
                    isFinish = true;
                    isSetOk = true;
                    break;
                case -1:
                    isSetOk = false;
                    isFinish = true;
                    break;
                default:
                    break;
            }
            if (count > 50) {
                isSetOk = false;
                isFinish = true;
            }
        }
        if (isSetOk) {
            udp.writeNet(rebootPacket);

            return 1;
        } else {
            return 0;
        }

    }


    public List<OneDev> getNewDevs() {
        return newDevs;
    }

    public List getDevWiFiListWithMac(long mac, byte type, byte ver) {
        ssids.clear();
        OneDev dev = DevStatus.getOneDev(mac);
        GeneralPacket packet = null;
        if (dev != null) {
            int netStatus = dev.getDevStatus(PublicMethod.getTimeMs());
            if ((netStatus > 0)) {
                //设备存在，并且设备在线，发起读取设备WiFi的任务
                if (netStatus == OneDev.ConnTypeLocal) {
                    try {
                        packet = new GeneralPacket(InetAddress.getByName("255.255.255.255"), EllESDK_DEF.LocalConPort, mContext);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    packet.packGetWiFiSsidListPacket(mac, type, ver, true, null);
                } else if (netStatus == OneDev.ConnTypeRemote) {
                    packet = new GeneralPacket(dev.remoteIP, dev.remotePort, mContext);
                    packet.packGetWiFiSsidListPacket(mac, type, ver, false, null);
                } else
                    return null;
            }
        } else {
            packet.packGetWiFiSsidListPacket(mac, type, ver, true, null);
        }
        udp.writeNet(packet);
        boolean isFinish = false;
        while (!isFinish) {
            try {
                Thread.sleep(1000);
                udp.writeNet(packet);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            break;
        }
        Log.d(TAG, "得到WiFi列表长度:" + ssids.size());
        return ssids;
    }


}

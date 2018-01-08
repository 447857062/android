package com.deplink.homegenius.manager.connect.local.udp.interfaces;

/**
 * Created by Administrator on 2017/11/7.
 * udpmanager获取到ip地址，把数据回调给LocalConnectManager
 */
public interface UdpManagerGetIPLintener {
    void onGetLocalConnectIp(String ipAddress,String uid);
}

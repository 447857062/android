package com.deplink.homegenius.manager.connect.local.udp.interfaces;

/**
 * Created by Administrator on 2017/11/7.
 * udppacket获取到ip地址，把数据回调给udpmanager
 */
public interface OnGetIpListener {
    void onRecvLocalConnectIp(byte[] packet,String uid);
}

package com.deplink.homegenius.manager.connect.local.udp.interfaces;

/**
 * Created by benond on 2017/2/6.
 * 接收udp数据的udpcommon 与发送udp数据的udppacket接口，传递一个IP地址
 */

public interface OnRecvLocalConnectIpListener {
    void OnRecvIp(byte[] ip,String uid);
}

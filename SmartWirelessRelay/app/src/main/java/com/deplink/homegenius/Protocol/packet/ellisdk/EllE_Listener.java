package com.deplink.homegenius.Protocol.packet.ellisdk;


/**
 * Created by benond on 2017/2/7.
 */

public interface EllE_Listener {

    void onRecvEllEPacket(BasicPacket packet);

    void searchDevCBS(long mac, byte type, byte ver);

}

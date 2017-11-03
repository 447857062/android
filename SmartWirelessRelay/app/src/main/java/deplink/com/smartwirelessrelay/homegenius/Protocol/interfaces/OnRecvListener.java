package deplink.com.smartwirelessrelay.homegenius.Protocol.interfaces;

import deplink.com.smartwirelessrelay.homegenius.Protocol.packet.BasicPacket;

/**
 * Created by benond on 2017/2/6.
 */

public interface OnRecvListener {

    void OnRecvData(BasicPacket packet);
    void OnRecvIp(byte[] ip);
}

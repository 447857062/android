package deplink.com.smartwirelessrelay.homegenius.Devices;


import deplink.com.smartwirelessrelay.homegenius.Protocol.BasicPacket;

/**
 * Created by benond on 2017/2/7.
 */

public interface EllE_Listener {

    void onRecvEllEPacket(BasicPacket packet);
}

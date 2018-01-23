package com.deplink.homegenius.Protocol.packet.ellisdk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by benond on 2017/2/6.
 */

public class DevList {


    public static List<com.deplink.homegenius.Protocol.packet.ellisdk.OneDev> devs;

    public DevList() {
        devs = new ArrayList<>();
    }


    public int addDevWithMac(long mac, byte type, byte ver) {
        for (int i = 0; i < devs.size(); i++) {
            com.deplink.homegenius.Protocol.packet.ellisdk.OneDev dev = devs.get(i);
            if (dev != null && dev.mac == mac) {
                return devs.size();
            }
        }

        com.deplink.homegenius.Protocol.packet.ellisdk.OneDev dev = new com.deplink.homegenius.Protocol.packet.ellisdk.OneDev(mac, type, ver);
        devs.add(dev);
        return devs.size();
    }

    public com.deplink.homegenius.Protocol.packet.ellisdk.OneDev getOneDev(long mac) {
        for (int i = 0; i < devs.size(); i++) {
            com.deplink.homegenius.Protocol.packet.ellisdk.OneDev dev = devs.get(i);
            if (dev != null && dev.mac == mac) {
                return dev;
            }
        }
        return null;
    }

    public int delDevFromCommWithMac(long mac) {
        for (int i = 0; i < devs.size(); i++) {
            com.deplink.homegenius.Protocol.packet.ellisdk.OneDev dev = devs.get(i);
            if (dev != null && dev.mac == mac) {
                devs.remove(i);
                break;
            }
        }
        return devs.size();
    }


}

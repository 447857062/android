package deplink.com.smartwirelessrelay.homegenius.Protocol.json.device;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 */
public class RemoteControlDevice extends SmartDev implements Serializable{

    private String Type="IRMOTE_V2";

    @Override
    public String getType() {
        return Type;
    }

    @Override
    public void setType(String type) {
        Type = type;
    }
}

package deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.remotecontrol;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 * 按键的学习状态
 */
public class TvboxKeyCode extends DataSupport implements Serializable{
    private int groupData;
    private String keycode;
    /**
     * 当前code绑定的智能设备UID
     */
    private String mAirconditionUid;


    public String getmAirconditionUid() {
        return mAirconditionUid;
    }

    public void setmAirconditionUid(String mAirconditionUid) {
        this.mAirconditionUid = mAirconditionUid;
    }
    public String getKeycode() {
        return keycode;
    }

    public void setKeycode(String keycode) {
        this.keycode = keycode;
    }

    public int getGroupData() {
        return groupData;
    }

    public void setGroupData(int groupData) {
        this.groupData = groupData;
    }
}

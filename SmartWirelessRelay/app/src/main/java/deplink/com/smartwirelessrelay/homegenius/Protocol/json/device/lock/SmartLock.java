package deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock;

import java.util.ArrayList;
import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.SmartDev;
import deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport.Info;

/**
 * Created by Administrator on 2017/10/30.
 * 智能锁设备
 */
public class SmartLock extends SmartDev {
    private List<Info> alarmList = new ArrayList<>();

    public List<Info> getAlarmList() {
        return alarmList;
    }

    public void setAlarmList(List<Info> alarmList) {
        this.alarmList = alarmList;
    }

    @Override
    public String toString() {
        return super.toString() + "SmartLock{" +
                "alarmList=" + alarmList +
                '}';
    }
}

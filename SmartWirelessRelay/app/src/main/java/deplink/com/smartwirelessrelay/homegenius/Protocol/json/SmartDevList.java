package deplink.com.smartwirelessrelay.homegenius.Protocol.json;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/30.
 */
public class SmartDevList implements Serializable{
    private int Count;
    private List<SmartDev>SmartDev;

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public List<SmartDev> getSmartDev() {
        return SmartDev;
    }

    public void setSmartDev(List<SmartDev> smartDev) {
        SmartDev = smartDev;
    }

    @Override
    public String toString() {
        return "SmartDevList{" +
                "Count=" + Count +
                ", SmartDev=" + SmartDev +
                '}';
    }
}

package deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/30.
 * 这个解析的json不是对象，是字符串类型。
 */
public class ReportAlertRecord implements Serializable {
    private String OP="REPORT";
    private String Method="ALARM_INFO";
    private List<ALARM_INFO> ALARM_INFO;

    public String getOP() {
        return OP;
    }

    public void setOP(String OP) {
        this.OP = OP;
    }

    public String getMethod() {
        return Method;
    }

    public void setMethod(String method) {
        Method = method;
    }

    public List<deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport.ALARM_INFO> getALARM_INFO() {
        return ALARM_INFO;
    }

    public void setALARM_INFO(List<deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport.ALARM_INFO> ALARM_INFO) {
        this.ALARM_INFO = ALARM_INFO;
    }

    @Override
    public String toString() {
        return "ReportAlertRecord{" +
                "OP='" + OP + '\'' +
                ", Method='" + Method + '\'' +
                ", ALARM_INFO=" + ALARM_INFO +
                '}';
    }
}

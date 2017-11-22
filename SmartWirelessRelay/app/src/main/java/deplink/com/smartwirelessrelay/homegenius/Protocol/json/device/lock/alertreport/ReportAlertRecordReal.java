package deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/30.
 * 这个才是报警记录的json解析对象
 */
public class ReportAlertRecordReal implements Serializable {
    private String OP="REPORT";
    private String Method="ALARM_INFO";
    private List<LOCK_ALARM> LOCK_ALARM=new ArrayList<>();

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


    public List<deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport.LOCK_ALARM> getLOCK_ALARM() {
        return LOCK_ALARM;
    }

    public void setLOCK_ALARM(List<deplink.com.smartwirelessrelay.homegenius.Protocol.json.device.lock.alertreport.LOCK_ALARM> LOCK_ALARM) {
        this.LOCK_ALARM = LOCK_ALARM;
    }

    @Override
    public String toString() {
        return "ReportAlertRecordReal{" +
                "OP='" + OP + '\'' +
                ", Method='" + Method + '\'' +
                ", LOCK_ALARM=" + LOCK_ALARM +
                '}';
    }
}

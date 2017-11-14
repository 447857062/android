package deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/30.
 */
public class LockHistorys implements Serializable{
    private String OP;
    private String Method;
    private List<LockHistory> Record;

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


    public List<LockHistory> getRecord() {
        return Record;
    }

    public void setRecord(List<LockHistory> record) {
        Record = record;
    }
}

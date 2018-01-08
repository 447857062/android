package com.deplink.homegenius.Protocol.json.device.lock;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/30.
 */
public class LockHistorys implements Serializable{
    private String OP;
    private String Method;
    private List<Record> Record;

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


    public List<Record> getRecord() {
        return Record;
    }

    public void setRecord(List<Record> record) {
        Record = record;
    }
}

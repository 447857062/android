package deplink.com.smartwirelessrelay.homegenius.Protocol.json;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/22.
 */
public class ResultType implements Serializable{
    private String OP;
    private String Method;

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

    @Override
    public String toString() {
        return "resultType{" +
                "OP='" + OP + '\'' +
                ", Method='" + Method + '\'' +
                '}';
    }
}

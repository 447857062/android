package deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/30.
 * 保存管理密码
 */
public class ManagerPassword extends DataSupport implements Serializable{
    /**
     * 记住管理密码
     */
    private boolean remenbEnable=true;
    private String managerPassword;

    public boolean isRemenbEnable() {
        return remenbEnable;
    }

    public void setRemenbEnable(boolean remenbEnable) {
        this.remenbEnable = remenbEnable;
    }

    public String getManagerPassword() {
        return managerPassword;
    }

    public void setManagerPassword(String managerPassword) {
        this.managerPassword = managerPassword;
    }

    @Override
    public String toString() {
        return "ManagerPassword{" +
                "remenbEnable=" + remenbEnable +
                ", managerPassword='" + managerPassword + '\'' +
                '}';
    }
}

package deplink.com.smartwirelessrelay.homegenius.manager.device;

/**
 * Created by Administrator on 2017/11/9.
 */
public interface DeviceListener {
    /**
     *返回智能锁查询结果
     */
    void responseQueryResult(String result);
    /**
     *返回绑定结果
     */
    void responseBindDeviceResult(String result);

}

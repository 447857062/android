package deplink.com.smartwirelessrelay.homegenius.manager.device;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock.SSIDList;

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
    /**
     *返回wifi列表
     */
    void responseWifiListResult(List<SSIDList>wifiList);

}

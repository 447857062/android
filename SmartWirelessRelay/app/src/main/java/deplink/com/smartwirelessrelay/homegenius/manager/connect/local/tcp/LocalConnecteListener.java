package deplink.com.smartwirelessrelay.homegenius.manager.connect.local.tcp;

import java.util.List;

import deplink.com.smartwirelessrelay.homegenius.Protocol.json.lock.alertreport.LOCK_ALARM;

/**
 * Created by Administrator on 2017/11/7.
 */
public interface LocalConnecteListener {

    /**
     * sslsocket握手成功
     */
    void handshakeCompleted();
    /**
     * sslsocket握手失败
     */
    void createSocketFailed(String msg);
    /**
     * 获取本地网关失败
     */
    void OnFailedgetLocalGW(String msg);
    /**
     * 获取uid
     */
    void OnBindAppResult(String uid);
    /**
     * 获取查询结果
     */
    void OnGetQueryresult(String devList);
    /**
     * 获取设置结果
     */
    void OnGetSetresult(String setResult);
    /**
     * 绑定结果
     */
    void OnGetBindresult(String setResult);

    /**
     * WiFi连接不可用
     */
    void wifiConnectUnReachable();
    /**
     * 查询到wifi列表
     */
    void getWifiList(String result);
    void onSetWifiRelayResult(String result);
    /**
     * 获取报警记录
     */
    void onGetalarmRecord(List<LOCK_ALARM> alarmList);
}

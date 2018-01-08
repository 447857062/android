package com.deplink.homegenius.manager.connect.local.tcp;

import java.util.List;

import com.deplink.homegenius.Protocol.json.device.lock.alertreport.Info;

/**
 * Created by Administrator on 2017/11/7.
 */
public interface LocalConnecteListener {




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
     * 查询到wifi列表
     */
    void getWifiList(String result);
    void onSetWifiRelayResult(String result);
    /**
     * 获取报警记录
     */
    void onGetalarmRecord(List<Info> alarmList);
}

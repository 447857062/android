package com.deplink.homegenius.manager.device.smartlock;

import com.deplink.homegenius.Protocol.json.device.lock.UserIdInfo;

/**
 * Created by Administrator on 2017/11/9.
 */
public interface SmartLockListener {
    /**
     *返回智能锁查询结果
     */
    void responseQueryResult(String result);
    /**
     *返回智能锁设置结果
     */
    void responseSetResult(String result);
    /**
     *返回智能锁绑定结果
     */
    void responseBind(String result);
    void responseLockStatu(int RecondNum,int LockStatus );
    void responseUserIdInfo(UserIdInfo userIdInfo);
}

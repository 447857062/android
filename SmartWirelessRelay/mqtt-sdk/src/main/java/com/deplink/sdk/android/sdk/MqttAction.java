package com.deplink.sdk.android.sdk;

/**
 * Created by huqs on 2016/7/5.
 */
public enum MqttAction {
    /**
     * 连接
     **/
    CONNECT,
    /**
     * 恢复连接
     **/
    RECONNECT,
    /**
     * 断开连接
     **/
    DISCONNECT,
    /**
     * 订阅
     **/
    SUBSCRIBE,
    /**
     * 广播
     **/
    PUBLISH,
}

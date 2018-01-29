package com.deplink.sdk.android.sdk;

/**
 * Created by huqs on 2016/7/1.
 */
public enum SDKAction {
    CONNECTED,
    /**
     * 登录
     */
    LOGIN,
    /**
     * 注销
     */
    LOGOUT,
    /**
     * 登录后修改密码
     */
    ALERTPASSWORD,
    /**
     * 上传头像
     */
    UPLOADIMAGE,
    /**
     * 获取头像
     */
    GETIMAGE,
    /**
     * 获取用户信息
     */
    GETUSERINFO,
    /**
     * 注册
     */
    REGISTER,
    /**
     *重置密码
     */
    RESET_PASSWORD,
    /**
     * 解除绑定
     */
    UNBIND,
    /**
     * 绑定设备
     */
    BIND,
    /**
     * 获取绑定的设备
     */
    GET_BINDING,
    /**
     * 修改用户设置
     */
    UPDATE_SETTING,
    /**
     * 意见反馈
     */
    SEND_COMMENTS,
    /**
     * 查询升级信息
     */
    OP_LOAD_UPGRADEINFO,
    /**
     * 查询APP升级信息
     */
    APPUPDATE,

}

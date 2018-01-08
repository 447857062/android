package com.deplink.homegenius.constant;

/**
 * Created by Administrator on 2017/12/19.
 */
public class DeviceTypeConstant {
    public interface TYPE{
        String TYPE_SMART_GETWAY = "智能网关";
        String TYPE_ROUTER = "路由器";
        String TYPE_LOCK = "智能密码门锁";
        String TYPE_MENLING = "智能门铃";
        String TYPE_SWITCH = "智能开关";
        String TYPE_REMOTECONTROL = "智能遥控";
        String TYPE_TV_REMOTECONTROL = "电视遥控";
        String TYPE_AIR_REMOTECONTROL = "空调遥控";
        String TYPE_TVBOX_REMOTECONTROL = "机顶盒遥控";
        String TYPE_LIGHT = "智能灯泡";
    }
    public interface TYPE_SWITCH_SUBTYPE{
        String SUB_TYPE_SWITCH_ONEWAY="一路开关";
        String SUB_TYPE_SWITCH_TWOWAY="二路开关";
        String SUB_TYPE_SWITCH_THREEWAY="三路开关";
        String SUB_TYPE_SWITCH_FOURWAY="四路开关";
    }
}

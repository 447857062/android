package com.deplink.homegenius.manager.connect.remote;

import android.util.Log;

import com.deplink.homegenius.Protocol.json.QueryOptions;
import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.Protocol.json.device.getway.GatwayDevice;
import com.deplink.homegenius.Protocol.json.qrcode.QrcodeSmartDevice;
import com.deplink.homegenius.Protocol.json.wifi.AP_CLIENT;
import com.deplink.homegenius.Protocol.json.wifi.Proto;
import com.deplink.homegenius.Protocol.json.wifi.WifiRelaySet;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.mqtt.MQTTController;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huqs on 2016/7/6.
 * 路由器设备
 */
public class HomeGenius {
    public static final String TAG = "HomeGenius";
    public void queryDeviceList(String topic, String userUuid) {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("QUERY");
        queryCmd.setMethod("DevList");
        queryCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    public void setWifiRelay(String topic, String userUuid,AP_CLIENT paramas) {
        Log.i(TAG, "setWifiRelay");
        WifiRelaySet setCmd = new WifiRelaySet();
        setCmd.setTimestamp();
        Proto proto = new Proto();
        proto.setAP_CLIENT(paramas);
        setCmd.setProto(proto);
        setCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(setCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));

    }

    public void bindSmartDevList(String topic, String userUuid,QrcodeSmartDevice smartDevice) {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("DevList");
        queryCmd.setTimestamp();
        List<SmartDev> devs = new ArrayList<>();
        //设备赋值
        SmartDev dev = new SmartDev();
        dev.setUid(smartDevice.getAd());
        dev.setOrg(smartDevice.getOrg());
        Log.i(TAG, "bindSmartDevList type=" + smartDevice.getTp());
        dev.setType(smartDevice.getTp());
        dev.setVer(smartDevice.getVer());
        //设备列表添加一个设备
        devs.add(dev);
        queryCmd.setSmartDev(devs);
        Gson gson = new Gson();
        queryCmd.setSenderId(userUuid);
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    public void deleteSmartDevice(SmartDev currentSelectSmartDevice, String topic, String userUuid) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("DELETE");
        queryCmd.setMethod("DevList");
        queryCmd.setTimestamp();
        List<SmartDev> devs = new ArrayList<>();
        //设备赋值
        SmartDev dev = new SmartDev();
        dev.setUid(currentSelectSmartDevice.getUid());
        dev.setOrg(currentSelectSmartDevice.getOrg());
        dev.setType(currentSelectSmartDevice.getType());
        dev.setVer(currentSelectSmartDevice.getVer());
        devs.add(dev);
        queryCmd.setSmartDev(devs);
        queryCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    public void deleteGetwayDevice(GatwayDevice currentSelectGetwayDevice, String topic, String userUuid) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("DELETE");
        queryCmd.setMethod("DevList");
        queryCmd.setTimestamp();
        List<GatwayDevice> devs = new ArrayList<>();
        //设备赋值
        GatwayDevice dev = new GatwayDevice();
        dev.setUid(currentSelectGetwayDevice.getUid());
        devs.add(dev);
        queryCmd.setDevice(devs);
        queryCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    public void bindGetwayDevice(String topic, String userUuid,String deviceUid) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("DevList");
        queryCmd.setTimestamp();
        List<GatwayDevice> devs = new ArrayList<>();
        //设备赋值
        GatwayDevice dev = new GatwayDevice();
        dev.setUid(deviceUid);
        devs.add(dev);
        queryCmd.setDevice(devs);
        queryCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        Log.i(TAG,"绑定网关:"+queryCmd.toString());
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    /**
     * 查询开锁记录
     */
    public void queryLockHistory(SmartDev currentSelectLock, String topic, String userUuid) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("QUERY");
        queryCmd.setMethod("SmartLock");
        queryCmd.setCommand("HisRecord");
        queryCmd.setUserID("1001");
        queryCmd.setSenderId(userUuid);
        queryCmd.setSmartUid(currentSelectLock.getUid());
        Log.i(TAG, "查询开锁记录设备smartUid=" + currentSelectLock.getUid());
        queryCmd.setTimestamp();
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    /**
     * 设置SamrtLock参数
     *
     * @param cmd
     * @param userId       注册app,服务器统一分配一个userid
     * @param managePasswd 管理密码，第一次由用户自己输入
     * @param authPwd      授权密码
     * @param limitedTime  授权时限
     */
    public void setSmartLockParmars(
            SmartDev currentSelectLock,
            String topic, String userUuid,
            String cmd, String userId, String managePasswd, String authPwd, String limitedTime) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("SmartLock");
        queryCmd.setSmartUid(currentSelectLock.getUid());
        queryCmd.setCommand(cmd);
        if (authPwd != null) {
            queryCmd.setAuthPwd(authPwd);
        } else {
            queryCmd.setAuthPwd("0");
        }

        queryCmd.setUserID(userId);
        queryCmd.setManagePwd(managePasswd);
        if (limitedTime != null) {
            queryCmd.setTime(limitedTime);
        } else {
            queryCmd.setTime("0");
        }
        queryCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    /**
     *
     */
    public void setSwitchCommand(SmartDev currentSelectSmartDevice,String topic, String userUuid,String cmd) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("SmartWallSwitch");
        queryCmd.setCommand(cmd);
        queryCmd.setSmartUid(currentSelectSmartDevice.getUid());
        Log.i(TAG, "设置开关smartUid=" + currentSelectSmartDevice.getUid());
        queryCmd.setTimestamp();
        queryCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    public void querySwitchStatus(SmartDev currentSelectSmartDevice,String topic, String userUuid,String cmd) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("SmartWallSwitch");
        queryCmd.setCommand(cmd);
        queryCmd.setSmartUid(currentSelectSmartDevice.getUid());
        queryCmd.setTimestamp();
        queryCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    public void study(SmartDev mSelectRemoteControlDevice, String topic, String userUuid) {
        com.deplink.homegenius.Protocol.json.QueryOptions cmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        cmd.setOP("SET");
        cmd.setMethod("IrmoteV2");
        cmd.setTimestamp();
        cmd.setSenderId(userUuid);
        cmd.setSmartUid(mSelectRemoteControlDevice.getRemotecontrolUid());
        cmd.setCommand("Study");
        Gson gson=new Gson();
        String text = gson.toJson(cmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    public void sendData(SmartDev mSelectRemoteControlDevice,String topic, String userUuid,String data) {
        //TODO 当前选中的遥控器和当前选中的物理遥控器混乱
        Log.i(TAG,"mSelectRemoteControlDevice="+mSelectRemoteControlDevice.getRemotecontrolUid());
        String controlUid=mSelectRemoteControlDevice.getRemotecontrolUid();
        if(mSelectRemoteControlDevice.getRemotecontrolUid()==null){
            controlUid=mSelectRemoteControlDevice.getUid();
        }
        com.deplink.homegenius.Protocol.json.QueryOptions cmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        cmd.setOP("SET");
        cmd.setMethod("IrmoteV2");
        cmd.setTimestamp();
        cmd.setSmartUid(controlUid);
        cmd.setCommand("Send");
        cmd.setData(data);
        cmd.setSenderId(userUuid);
        Gson gson=new Gson();
        String text = gson.toJson(cmd);
        Log.i(TAG,"mSelectRemoteControlDevice!=null"+(mSelectRemoteControlDevice!=null));
        Log.i(TAG,""+text+" mSelectRemoteControlDevice.getRemotecontrolUid()!=null"+( mSelectRemoteControlDevice.getRemotecontrolUid()));
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    public void setSmartLightSwitch(SmartDev currentSelectLight,String topic, String userUuid,String cmd) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("YWLIGHTCONTROL");
        queryCmd.setSmartUid(currentSelectLight.getUid());
        queryCmd.setCommand(cmd);
        queryCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    public void setSmartLightParamas(SmartDev currentSelectLight,String topic, String userUuid,String cmd,int yellow,int white ) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("YWLIGHTCONTROL");
        queryCmd.setSmartUid(currentSelectLight.getUid());
        queryCmd.setCommand(cmd);
        queryCmd.setYellow(yellow);
        queryCmd.setWhite(white);
        queryCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    public void queryLightStatus(SmartDev currentSelectLight,String topic, String userUuid) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("YWLIGHTCONTROL");
        queryCmd.setCommand("query");
        queryCmd.setSmartUid(currentSelectLight.getUid());
        queryCmd.setTimestamp();
        queryCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    private void notifySuccess(String action) {
       /* if (mSDKCoordinator != null) {
            mSDKCoordinator.notifyDeviceOpSuccess(action, deviceKey);
        }*/
    }
    private void notifyFailure(String action, String error) {
       /* if (mSDKCoordinator != null) {
            mSDKCoordinator.notifyDeviceOpFailure(action, deviceKey, new Throwable(error));
        }*/
    }
    private class MqttActionHandler implements IMqttActionListener {
        private String action;
        public MqttActionHandler(String action) {
            this.action = action;
        }
        @Override
        public void onSuccess(IMqttToken iMqttToken) {
            Log.i(DeplinkSDK.SDK_TAG, "--->Mqtt onSuccess: " + iMqttToken.toString());
            notifySuccess(action);
        }
        @Override
        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
            throwable.printStackTrace();
            Log.i(DeplinkSDK.SDK_TAG, "--->Mqtt failure: " + throwable.getMessage());
            String error = "操作失败";
            notifyFailure(action, error);
        }
    }
}
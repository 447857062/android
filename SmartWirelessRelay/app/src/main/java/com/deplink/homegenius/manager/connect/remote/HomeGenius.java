package com.deplink.homegenius.manager.connect.remote;

import android.util.Log;

import com.deplink.homegenius.Protocol.json.QueryOptions;
import com.deplink.homegenius.Protocol.json.device.SmartDev;
import com.deplink.homegenius.Protocol.json.device.getway.GatwayDevice;
import com.deplink.homegenius.Protocol.json.qrcode.QrcodeSmartDevice;
import com.deplink.homegenius.Protocol.json.wifi.AP_CLIENT;
import com.deplink.homegenius.Protocol.json.wifi.Proto;
import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.bean.DeviceUpgradeInfo;
import com.deplink.sdk.android.sdk.device.router.RouterDevice;
import com.deplink.sdk.android.sdk.json.DeviceControl;
import com.deplink.sdk.android.sdk.json.DeviceImageUpgrade;
import com.deplink.sdk.android.sdk.json.Lan;
import com.deplink.sdk.android.sdk.json.PERFORMANCE;
import com.deplink.sdk.android.sdk.json.Qos;
import com.deplink.sdk.android.sdk.json.Wifi;
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

    public void setWifiRelay(String topic, String userUuid, AP_CLIENT paramas) {
        Log.i(TAG, "setWifiRelay");
        QueryOptions setCmd = new QueryOptions();
        setCmd.setOP("WAN");
        setCmd.setMethod("SET");
        setCmd.setTimestamp();
        Proto proto = new Proto();
        proto.setAP_CLIENT(paramas);
        setCmd.setProto(proto);
        setCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(setCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }

    public void bindSmartDevList(String topic, String userUuid, QrcodeSmartDevice smartDevice) {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("DevList");
        queryCmd.setTimestamp();
        List<SmartDev> devs = new ArrayList<>();
        //设备赋值
        SmartDev dev = new SmartDev();
        dev.setOrg(smartDevice.getOrg());
        Log.i(TAG, "bindSmartDevList type=" + smartDevice.getTp());
        dev.setType(smartDevice.getTp());
        dev.setVer(smartDevice.getVer());
        dev.setSmartUid(smartDevice.getAd());
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
        queryCmd.setSmartUid(currentSelectSmartDevice.getMac());
        queryCmd.setTimestamp();
        List<SmartDev> devs = new ArrayList<>();
        //设备赋值
        SmartDev dev = new SmartDev();
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

    public void bindGetwayDevice(String topic, String userUuid, String deviceUid) {
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
        Log.i(TAG, "绑定网关:" + queryCmd.toString());
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }

    /**
     * 查询开锁记录
     */
    public void queryLockHistory(SmartDev currentSelectLock, String topic, String userUuid,int queryNumber,String userId) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("QUERY");
        queryCmd.setMethod("SmartLock");
        queryCmd.setCommand("HisRecord");
        queryCmd.setUserID(userId);
        queryCmd.setQuery_Num(queryNumber);
        queryCmd.setSenderId(userUuid);
        queryCmd.setSmartUid(currentSelectLock.getMac());
        queryCmd.setTimestamp();
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    public void queryLockStatu(SmartDev currentSelectLock, String topic, String userUuid) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("SMART_LOCK");
        queryCmd.setCommand("query");
        queryCmd.setSenderId(userUuid);
        queryCmd.setSmartUid(currentSelectLock.getMac());
        queryCmd.setTimestamp();
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    public void queryRemoteControlStatu(SmartDev currentSelectLock, String topic, String userUuid) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("IrmoteV2");
        queryCmd.setCommand("query");
        queryCmd.setSenderId(userUuid);
        queryCmd.setSmartUid(currentSelectLock.getMac());
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
            String cmd, String userId,
            String managePasswd, String authPwd, String limitedTime) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("SmartLock");
        queryCmd.setSmartUid(currentSelectLock.getMac());
        queryCmd.setCommand(cmd);
        queryCmd.setTimestamp();
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
            queryCmd.setTime("30");
        }
        queryCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }


    public void setSwitchCommand(SmartDev currentSelectSmartDevice, String topic, String userUuid, String cmd) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("SmartWallSwitch");
        queryCmd.setCommand(cmd);
        queryCmd.setSmartUid(currentSelectSmartDevice.getMac());
        Log.i(TAG, "设置开关smartUid=" + currentSelectSmartDevice.getMac());
        queryCmd.setTimestamp();
        queryCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    public void queryWifiList(String topic, String userUuid) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("QUERY");
        queryCmd.setMethod("WIFIRELAY");
        queryCmd.setTimestamp();
        queryCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    public void querySwitchStatus(SmartDev currentSelectSmartDevice, String topic, String userUuid, String cmd) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("SmartWallSwitch");
        queryCmd.setCommand(cmd);
        queryCmd.setSmartUid(currentSelectSmartDevice.getMac());
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
        cmd.setSmartUid(mSelectRemoteControlDevice.getMac());
        cmd.setCommand("Study");
        Gson gson = new Gson();
        String text = gson.toJson(cmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }
    public void stopStudy(SmartDev mSelectRemoteControlDevice, String topic, String userUuid) {
        com.deplink.homegenius.Protocol.json.QueryOptions cmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        cmd.setOP("SET");
        cmd.setMethod("IrmoteV2");
        cmd.setTimestamp();
        cmd.setSenderId(userUuid);
        cmd.setSmartUid(mSelectRemoteControlDevice.getMac());
        cmd.setCommand("Quit");
        Gson gson = new Gson();
        String text = gson.toJson(cmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }

    public void sendData(SmartDev mSelectRemoteControlDevice, String topic, String userUuid, String data) {
        //TODO 当前选中的遥控器和当前选中的物理遥控器混乱
        Log.i(TAG, "mSelectRemoteControlDevice=" + mSelectRemoteControlDevice.getRemotecontrolUid());
        String controlUid = mSelectRemoteControlDevice.getMac();
        if (mSelectRemoteControlDevice.getRemotecontrolUid() == null) {
            controlUid = mSelectRemoteControlDevice.getMac();
        }
        com.deplink.homegenius.Protocol.json.QueryOptions cmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        cmd.setOP("SET");
        cmd.setMethod("IrmoteV2");
        cmd.setTimestamp();
        cmd.setSmartUid(controlUid);
        cmd.setCommand("Send");
        cmd.setData(data);
        cmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(cmd);
        Log.i(TAG, "" + text + " mSelectRemoteControlDevice.getRemotecontrolUid()!=null" + (mSelectRemoteControlDevice.getRemotecontrolUid()));
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }

    public void setSmartLightSwitch(SmartDev currentSelectLight, String topic, String userUuid, String cmd) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("YWLIGHTCONTROL");
        queryCmd.setSmartUid(currentSelectLight.getMac());
        queryCmd.setCommand(cmd);
        queryCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }

    public void setSmartLightParamas(SmartDev currentSelectLight, String topic, String userUuid, String cmd, int yellow, int white) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("YWLIGHTCONTROL");
        queryCmd.setSmartUid(currentSelectLight.getMac());
        queryCmd.setCommand(cmd);
        queryCmd.setYellow(yellow);
        queryCmd.setWhite(white);
        queryCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }

    public void queryLightStatus(SmartDev currentSelectLight, String topic, String userUuid) {
        com.deplink.homegenius.Protocol.json.QueryOptions queryCmd = new com.deplink.homegenius.Protocol.json.QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("YWLIGHTCONTROL");
        queryCmd.setCommand("query");
        queryCmd.setSmartUid(currentSelectLight.getMac());
        queryCmd.setTimestamp();
        queryCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish(topic, text, new MqttActionHandler(""));
    }

    /**
     * 查询设备
     */
    public void queryDevices(String sub) {
        PERFORMANCE textContent = new PERFORMANCE();
        textContent.setOP("QUERY");
        textContent.setMethod("DEVICES");
        textContent.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(textContent);
        MQTTController.getSingleton().publish(sub, text, new MqttActionHandler(RouterDevice.OP_QUERY_DEVICES));
    }
    /**
     * 要求设备上报
     */
    public void getReport(String sub) {
        PERFORMANCE textContent = new PERFORMANCE();
        textContent.setOP("QUERY");
        textContent.setMethod("PERFORMANCE");
        textContent.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(textContent);

        MQTTController.getSingleton().publish(sub, text, new MqttActionHandler(RouterDevice.OP_QUERY_REPORT));
    }
    /**
     * 设置黑名单列表，从列表中移除
     */
    public void setDeviceControl(DeviceControl control,String sub) {
        control.setOP("DEVICES");
        control.setMethod("CONTROL");
        control.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(control);
        MQTTController.getSingleton().publish(sub, text, new MqttActionHandler(RouterDevice.OP_SET_DEVICE_CONTROL));
    }
    /**
     * 查询LAN
     */
    public void queryLan(String sub) {
        Log.i(TAG, "queryLan");
        PERFORMANCE textContent = new PERFORMANCE();
        textContent.setTimestamp(System.currentTimeMillis() / 1000);
        textContent.setOP("QUERY");
        textContent.setMethod("LAN");
        Gson gson = new Gson();
        String text = gson.toJson(textContent);
        MQTTController.getSingleton().publish(sub, text, new MqttActionHandler(RouterDevice.OP_QUERY_LAN));
    }

    /**
     * 查询WAN
     */
    public void queryWan(String sub) {
        PERFORMANCE textContent = new PERFORMANCE();
        textContent.setOP("QUERY");
        textContent.setMethod("WAN");
        textContent.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(textContent);
        MQTTController.getSingleton().publish( sub, text, new MqttActionHandler(RouterDevice.OP_QUERY_WAN));
    }
    /**
     * 重启设备
     */
    public void reboot(String sub) {
        PERFORMANCE textContent = new PERFORMANCE();
        textContent.setOP("REBOOT");
        textContent.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(textContent);
        MQTTController.getSingleton().publish(sub, text, new MqttActionHandler(RouterDevice.OP_REBOOT));
    }
    /**
     * 设置上网方式
     */
    public void setWan(com.deplink.sdk.android.sdk.json.Proto proto,String sub) {
        PERFORMANCE textContent = new PERFORMANCE();
        textContent.setOP("WAN");
        textContent.setMethod("SET");
        textContent.setProto(proto);
        textContent.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(textContent);
        MQTTController.getSingleton().publish(sub, text, new MqttActionHandler(RouterDevice.OP_SET_WAN));
    }
    /**
     * 启动设备固件升级
     */
    public void startUpgrade(DeviceUpgradeInfo deviceUpgradeInfo,String sub) {
        if (null == deviceUpgradeInfo) {
            return;
        }
        DeviceImageUpgrade upgrade = new DeviceImageUpgrade();
        upgrade.setOP("IMAGE");
        upgrade.setMethod("UPGRADE");
        upgrade.setSoftwareVersion(deviceUpgradeInfo.getVersion());
        upgrade.setProductKey(deviceUpgradeInfo.getProduct_key());
        upgrade.setProtocol(deviceUpgradeInfo.getProtocol());
        upgrade.setImgUrl(deviceUpgradeInfo.getImg_url());
        upgrade.setBakProtocol(deviceUpgradeInfo.getBak_protocol());
        upgrade.setBakImgUrl(deviceUpgradeInfo.getBak_img_url());
        upgrade.setFileLen(deviceUpgradeInfo.getFile_len());
        upgrade.setMD5(deviceUpgradeInfo.getFile_md5());
        upgrade.setUpgradeTime("0");
        upgrade.setRandomTime(0);
        upgrade.setType(0);
        Gson gson = new Gson();
        String content = gson.toJson(upgrade);
        Log.d(DeplinkSDK.SDK_TAG, "--->write JSON: " + content);
        MQTTController.getSingleton().publish(sub, content, new MqttActionHandler(RouterDevice.OP_CHG_START_UPGRADE));
    }
    /**
     * 设置LAN
     */
    public void setLan(Lan lan,String sub) {
        lan.setOP("LAN");
        lan.setMethod("SET");
        lan.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(lan);
        MQTTController.getSingleton().publish(sub, text, new MqttActionHandler(RouterDevice.OP_SET_LAN));
    }
    /**
     * 设置Qos
     */
    public void setQos(Qos qos,String sub) {

        qos.setOP("QOS");
        qos.setMethod("SET");
        qos.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(qos);
        MQTTController.getSingleton().publish(sub, text, new MqttActionHandler(RouterDevice.OP_SET_QOS));
    }
    /**
     * 查询Qos
     */
    public void queryQos(String sub) {
        PERFORMANCE textContent = new PERFORMANCE();
        textContent.setOP("QUERY");
        textContent.setMethod("QOS");
        textContent.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(textContent);
        MQTTController.getSingleton().publish(sub, text, new MqttActionHandler(RouterDevice.OP_QUERY_QOS));
    }
    /**
     * 设置wifi
     */
    public void setWifi(Wifi wifi,String sub) {
        wifi.setOP("WIFI");
        wifi.setMethod("SET");
        wifi.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(wifi);
        MQTTController.getSingleton().publish(sub, text, new MqttActionHandler(RouterDevice.OP_SET_WIFI));
    }
    /**
     * 查询无线中继
     */
    public void queryWifiRelay(String sub) {
        PERFORMANCE textContent = new PERFORMANCE();
        textContent.setOP("QUERY");
        textContent.setMethod("WIFIRELAY");
        textContent.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(textContent);
        MQTTController.getSingleton().publish(sub, text, new MqttActionHandler(RouterDevice.OP_QUERY_WIFIRELAY));
    }
    /**
     * 查询wifi
     */
    public void queryWifi(String sub) {
        PERFORMANCE textContent = new PERFORMANCE();
        textContent.setOP("QUERY");
        textContent.setMethod("WIFI");
        textContent.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(textContent);
        MQTTController.getSingleton().publish(sub, text, new MqttActionHandler(RouterDevice.OP_QUERY_WIFI));
    }
    private class MqttActionHandler implements IMqttActionListener {
        private String action;

        public MqttActionHandler(String action) {
            this.action = action;
        }

        @Override
        public void onSuccess(IMqttToken iMqttToken) {
            Log.i(DeplinkSDK.SDK_TAG, "--->Mqtt onSuccess: " + iMqttToken.toString());
        }

        @Override
        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
            throwable.printStackTrace();
            Log.i(DeplinkSDK.SDK_TAG, "--->Mqtt failure: " + throwable.getMessage());
            String error = "操作失败";
        }
    }
}
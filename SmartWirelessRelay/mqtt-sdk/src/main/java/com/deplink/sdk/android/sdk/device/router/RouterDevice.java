package com.deplink.sdk.android.sdk.device.router;

import android.util.Log;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.bean.CommonRes;
import com.deplink.sdk.android.sdk.bean.DeviceCookieItem;
import com.deplink.sdk.android.sdk.bean.DeviceCookieRes;
import com.deplink.sdk.android.sdk.bean.DeviceProperty;
import com.deplink.sdk.android.sdk.bean.DeviceUpgradeRes;
import com.deplink.sdk.android.sdk.interfaces.SDKCoordinator;
import com.deplink.sdk.android.sdk.json.DeviceControl;
import com.deplink.sdk.android.sdk.json.DeviceImageUpgrade;
import com.deplink.sdk.android.sdk.json.DeviceUpgradeReport;
import com.deplink.sdk.android.sdk.json.DevicesOnlineRoot;
import com.deplink.sdk.android.sdk.json.Lan;
import com.deplink.sdk.android.sdk.json.PERFORMANCE;
import com.deplink.sdk.android.sdk.json.Proto;
import com.deplink.sdk.android.sdk.json.Qos;
import com.deplink.sdk.android.sdk.json.SSIDList;
import com.deplink.sdk.android.sdk.json.Wifi;
import com.deplink.sdk.android.sdk.mqtt.MQTTController;
import com.deplink.sdk.android.sdk.rest.RestfulTools;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by huqs on 2016/7/6.
 * 路由器设备
 */
public class RouterDevice extends BaseDevice {
    public static final String TAG = "RouterDevice";
    public static final String OP_REBOOT = "reboot";
    /**
     * 这个代表请求得到响应了
     */
    public static final String OP_GET_REPORT = "getReport";
    /**
     * 这个回调只代表请求的数据已发送
     */
    public static final String OP_QUERY_REPORT = "queryReport";
    public static final String OP_SUCCESS = "opSuccess";
    public static final String OP_SET_WAN = "setWan";
    public static final String OP_SET_LAN = "setLan";
    public static final String OP_GET_WIFIRELAY = "getWifiRelay";
    public static final String OP_QUERY_DEVICES = "queryDevices";
    public static final String OP_GET_DEVICES = "getDevices";
    public static final String OP_QUERY_LAN = "queryLan";
    public static final String OP_GET_LAN = "getLan";
    public static final String OP_QUERY_WAN = "queryWan";
    public static final String OP_GET_WAN = "GetWan";
    public static final String OP_QUERY_WIFIRELAY = "queryWifiRelay";
    public static final String OP_QUERY_WIFI = "queryWifi";
    public static final String OP_QUERY_QOS = "queryQos";
    public static final String OP_GET_WIFI = "getWifi";
    public static final String OP_GET_QOS = "getQos";
    public static final String OP_SET_WIFI = "setWifi";
    public static final String OP_SET_DEVICE_CONTROL = "setDeviceControl";
    public static final String OP_SET_QOS = "setQos";
    public static final String OP_CHANGE_AUTO_UPGRADE = "changeAutoUpgrade";
    public static final String OP_CHG_START_UPGRADE = "startUpgrade";
    public static final String OP_LOAD_UPGRADEINFO = "loadUpgradeInfo";
    public static final String OP_LOAD_UPGRADEINFONULL = "loadUpgradeInfoNull";
    //start:开始，download:下载，update:烧写，finish:完成，error:出错）
    public static final int MSG_REPORT_START = MSG_DUMMY + 1;
    public static final int MSG_REPORT_DOWNLOAD = MSG_DUMMY + 2;
    public static final int MSG_REPORT_WRITE = MSG_DUMMY + 3;
    public static final int MSG_REPORT_FINSH = MSG_DUMMY + 4;
    public static final int MSG_REPORT_ERROR = MSG_DUMMY + 5;
    private long currentReceivedDataTime = 0;
    private Proto proto;
    private DevicesOnlineRoot mDevicesOnlineRoot;
    private List<SSIDList> wifiRelay = new ArrayList<>();
    /**
     * 上一次设备上传的流量
     */
    private long lastUploadData = 0;
    /**
     * 上一次设备下载的流量
     */
    private long lastDownloadData = 0;
    /**
     * 设备上行速率(bps)
     */
    private float uprate;
    /**
     * 设备下行速率(bps)
     */
    private float downrate;
    private SDKCoordinator mSDKCoordinator = null;
    private Lan lan;
    private Qos qos;
    public float getUpRate() {
        return uprate;
    }
    public float getDownRate() {
        return downrate;
    }
    public RouterDevice(SDKCoordinator coordinator) {
        mSDKCoordinator = coordinator;
    }
    @Override
    public int processMqttMsg(String topic, MqttMessage message) {
        String jsonString = new String(message.getPayload());
        Log.i(DeplinkSDK.SDK_TAG, "--->mqtt msg: " + jsonString);
        return parseDeviceReport(jsonString);
    }
    private int parseDeviceReport(String xmlStr) {
        int msgFormat = MSG_DUMMY;
        String op = "";
        String method;
        Gson gson = new Gson();
        PERFORMANCE content = gson.fromJson(xmlStr, PERFORMANCE.class);
        op = content.getOP();
        method = content.getMethod();
        Log.i(TAG, "op=" + op + "method=" + method + "result=" + content.getResult() + "xmlStr=" + xmlStr);
        if(op!=null){
            isOnline = true;
        }
        if (op == null) {
            if (content.getResult().equalsIgnoreCase("OK")) {
                Log.i(TAG," mSDKCoordinator.notifyDeviceOpSuccess");
                mSDKCoordinator.notifyDeviceOpSuccess(RouterDevice.OP_SUCCESS, deviceKey);
                return msgFormat;
            }
        } else if (op.equalsIgnoreCase("REPORT")) {
            if (method.equalsIgnoreCase("PERFORMANCE")) {
                performance = gson.fromJson(xmlStr, PERFORMANCE.class);
                Log.i(TAG, "performance=" + performance.toString());
                updateDevice(performance);
                mSDKCoordinator.notifyDeviceOpSuccess(RouterDevice.OP_GET_REPORT, deviceKey);
            } else if (method.equalsIgnoreCase("WIFIRELAY")) {
                updateWifiRelay(content);
                mSDKCoordinator.notifyDeviceOpSuccess(RouterDevice.OP_GET_WIFIRELAY, deviceKey);
            } else if (method.equalsIgnoreCase("UPGRADE")) {
                DeviceUpgradeReport upgrade = gson.fromJson(xmlStr, DeviceUpgradeReport.class);
                if ("finish".equalsIgnoreCase(upgrade.getState())) {
                    upgradeInfo = null;
                    return MSG_REPORT_FINSH;
                } else if ("start".equalsIgnoreCase(upgrade.getState())) {
                    return MSG_REPORT_START;
                } else if ("download".equalsIgnoreCase(upgrade.getState())) {
                    return MSG_REPORT_DOWNLOAD;
                } else if ("update".equalsIgnoreCase(upgrade.getState())) {
                    return MSG_REPORT_WRITE;
                } else if ("error".equalsIgnoreCase(upgrade.getState())) {
                    return MSG_REPORT_ERROR;
                }
            }
        } else if (op.equalsIgnoreCase("DEVICES")) {
            if (method.equalsIgnoreCase("REPORT")) {
                mDevicesOnlineRoot = gson.fromJson(xmlStr, DevicesOnlineRoot.class);
                Log.i(TAG,"routerdevice DEVICES REPORT mDevicesOnlineRoot"+(mDevicesOnlineRoot!=null)+mDevicesOnlineRoot.toString());
                mSDKCoordinator.notifyDeviceOpSuccess(RouterDevice.OP_GET_DEVICES, deviceKey);
            }
        } else if (op.equalsIgnoreCase("LAN")) {
            if (method.equalsIgnoreCase("REPORT")) {
                lan = gson.fromJson(xmlStr, Lan.class);
                Log.i(TAG, "get lan =" + lan.toString());
                mSDKCoordinator.notifyDeviceOpSuccess(RouterDevice.OP_GET_LAN, deviceKey);
            }
        } else if (op.equalsIgnoreCase("WAN")) {
            if (method.equalsIgnoreCase("REPORT")) {
                PERFORMANCE wan = gson.fromJson(xmlStr, PERFORMANCE.class);
                updateQueryedWan(wan);
                mSDKCoordinator.notifyDeviceOpSuccess(RouterDevice.OP_GET_WAN, deviceKey);
            }
        } else if (op.equalsIgnoreCase("QOS")) {
            if (method.equalsIgnoreCase("REPORT")) {
                qos = gson.fromJson(xmlStr, Qos.class);
                mSDKCoordinator.notifyDeviceOpSuccess(RouterDevice.OP_GET_QOS, deviceKey);
            }
        } else if (op.equalsIgnoreCase("WIFI")) {
            if (method.equalsIgnoreCase("REPORT")) {
                wifi = gson.fromJson(xmlStr, Wifi.class);
                mSDKCoordinator.notifyDeviceOpSuccess(RouterDevice.OP_GET_WIFI, deviceKey);
            }
        }
        return msgFormat;
    }
    private Wifi wifi;

    public Wifi getWifi() {
        return wifi;
    }

    public List<SSIDList> getWifiRelay() {
        return wifiRelay;
    }

    private void updateWifiRelay(PERFORMANCE content) {
        wifiRelay.clear();
        Log.i(TAG, "PERFORMANCE:getSSIDList().size" + content.getSSIDList().size());
        wifiRelay.addAll(content.getSSIDList());
    }

    private void updateQueryedWan(PERFORMANCE wan) {
        proto = wan.getProto();
    }

    public Qos getQos() {
        return qos;
    }

    public Lan getLan() {
        return lan;
    }

    public Proto getProto() {
        return proto;
    }

    public DevicesOnlineRoot getDevicesOnlineRoot() {
        return mDevicesOnlineRoot;
    }


    private PERFORMANCE performance;

    public PERFORMANCE getPerformance() {
        return performance;
    }

    private void updateDevice(PERFORMANCE report) {
        //流量下载
        String downloadBytes = report.getDevice().getDataTraffic().getRX();
        if (downloadBytes.length() > 6) {//去掉数据开始的字符：bytes:
            downloadBytes = downloadBytes.substring(6);
        } else {
            downloadBytes = "0";
        }
        long downloadBytesMath = Long.parseLong(downloadBytes);
        Log.i(TAG, downloadBytes + "downloadBytesMath=" + downloadBytesMath);
        //流量上传
        String uploadBytes = report.getDevice().getDataTraffic().getTX();
        if (uploadBytes.length() > 6) {
            uploadBytes = report.getDevice().getDataTraffic().getTX().substring(6);
        } else {
            uploadBytes = "0";
        }
        long uploadBytesMath = Long.parseLong(uploadBytes);
        Log.i(TAG, uploadBytes + "uploadBytesMath=" + uploadBytesMath);
        /**
         *两次获取到的数据（PERFORMANCE）的时间差
         */
        long timeDif;
        if (currentReceivedDataTime == 0) {
            //说明是第一次收到反馈数据,不处理
            currentReceivedDataTime = System.currentTimeMillis();
        } else {
            timeDif = System.currentTimeMillis() - currentReceivedDataTime;
            currentReceivedDataTime = System.currentTimeMillis();
            Log.i(TAG, "下载总数=" + (float) (downloadBytesMath - lastDownloadData) / 1024 + "KB");
            Log.i(TAG, "上传总数=" + (float) (downloadBytesMath - lastDownloadData) / 1024 + "KB");
            downrate = (float) (Math.abs(downloadBytesMath - lastDownloadData)) / 1024 / ((float) timeDif / 1000);
            uprate = (float) (Math.abs(uploadBytesMath - lastUploadData)) / 1024 / ((float) timeDif / 1000);

        }
        lastDownloadData = downloadBytesMath;
        lastUploadData = uploadBytesMath;
    }
    /**
     * 获取升级信息
     */
    public void retrieveUpgradeInfo() {
        RestfulTools.getSingleton().getDeviceUpgradeInfo(deviceKey, new Callback<DeviceUpgradeRes>() {
            @Override
            public void onResponse(Call<DeviceUpgradeRes> call, Response<DeviceUpgradeRes> response) {
                switch (response.code()) {
                    case 200:
                        Log.i(TAG, "retrieveUpgradeInfo=" + response.body().toString() + "response.message()=" + response.message());
                        if (null != response.body().getUpgrade_info()) {
                            Log.i(TAG, "已获取版本升级信息");
                            setUpgradeInfo(response.body().getUpgrade_info());
                            notifySuccess(OP_LOAD_UPGRADEINFO);
                        } else {
                            Log.i(TAG, "版本升级信息为空");
                            notifySuccess(OP_LOAD_UPGRADEINFONULL);
                        }
                        break;
                }
            }
            @Override
            public void onFailure(Call<DeviceUpgradeRes> call, Throwable t) {
                String error = "读取设备升级信息名称失败";
                Log.i(TAG, "读取设备升级信息名称失败 " + t.getMessage());
                notifyFailure(OP_LOAD_UPGRADEINFO, error);
            }
        });
    }

    /**
     * 获取设备cookie
     */
    public void retrieveDeviceCookie() {
        RestfulTools.getSingleton().getDeviceCookie(deviceKey, null, null, new Callback<DeviceCookieRes>() {

            @Override
            public void onResponse(Call<DeviceCookieRes> call, Response<DeviceCookieRes> response) {
                switch (response.code()) {
                    case 200:
                        List<DeviceCookieItem> result = response.body().getCookies();
                        cookies.clear();
                        for (DeviceCookieItem item : result) {
                            cookies.add(item);
                        }
                        break;
                }
            }

            @Override
            public void onFailure(Call<DeviceCookieRes> call, Throwable t) {

            }
        });
    }

    /**
     * 获取设备属性
     */
    public void retrieveDeviceProperty() {
        RestfulTools.getSingleton().getDeviceProperty(deviceKey, new Callback<DeviceProperty>() {

            @Override
            public void onResponse(Call<DeviceProperty> call, Response<DeviceProperty> response) {
                switch (response.code()) {
                    case 200:
                        DeviceProperty property = response.body();
                        if (null != property && "ok".equals(property.getStatus())) {
                            setAutoUpgrade("true".equals(property.isAuto_upgrade()));
                            setName(property.getName());
                        }
                        break;
                }
            }

            @Override
            public void onFailure(Call<DeviceProperty> call, Throwable t) {

            }
        });
    }

    /**
     * 修改自动升级选项
     *
     * @param auto
     */
    public void changeAutoUpgrade(final boolean auto) {
        DeviceProperty item = new DeviceProperty();
        item.setAuto_upgrade(auto);
        RestfulTools.getSingleton().updateDeviceProperty(deviceKey, item, new Callback<CommonRes>() {
            @Override
            public void onResponse(Call<CommonRes> call, Response<CommonRes> response) {
                switch (response.code()) {
                    case 200:
                        setAutoUpgrade(auto);
                        notifySuccess(OP_CHANGE_AUTO_UPGRADE);
                        break;
                }
            }

            @Override
            public void onFailure(Call<CommonRes> call, Throwable t) {
                String error = "修改设备自动升级选项失败";
                notifyFailure(OP_CHANGE_AUTO_UPGRADE, error);
            }
        });
    }

    /**
     * 启动设备固件升级
     */
    public void startUpgrade() {
        if (null == upgradeInfo) {
            return;
        }
        DeviceImageUpgrade upgrade = new DeviceImageUpgrade();
        upgrade.setOP("IMAGE");
        upgrade.setMethod("UPGRADE");
        upgrade.setSoftwareVersion(upgradeInfo.getVersion());
        upgrade.setProductKey(upgradeInfo.getProduct_key());
        upgrade.setProtocol(upgradeInfo.getProtocol());
        upgrade.setImgUrl(upgradeInfo.getImg_url());
        upgrade.setBakProtocol(upgradeInfo.getBak_protocol());
        upgrade.setBakImgUrl(upgradeInfo.getBak_img_url());
        upgrade.setFileLen(upgradeInfo.getFile_len());
        upgrade.setMD5(upgradeInfo.getFile_md5());
        // upgrade.setUpgradeTime(String.valueOf(upgradeInfo.getUpgrade_time()));
        upgrade.setUpgradeTime("0");
        // upgrade.setRandomTime(upgradeInfo.getRandom_time());
        upgrade.setRandomTime(0);
        upgrade.setType(0);
        Gson gson = new Gson();
        String content = gson.toJson(upgrade);
        Log.d(DeplinkSDK.SDK_TAG, "--->write JSON: " + content);
        MQTTController.getSingleton().publish(exclusive.getSub(), content, new MqttActionHandler(RouterDevice.OP_CHG_START_UPGRADE));
    }

    /**
     * 要求设备上报
     */
    public void getReport() {
        PERFORMANCE textContent = new PERFORMANCE();
        textContent.setOP("QUERY");
        textContent.setMethod("PERFORMANCE");
        textContent.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(textContent);

        MQTTController.getSingleton().publish(exclusive.getSub(), text, new MqttActionHandler(RouterDevice.OP_QUERY_REPORT));
    }
    /**
     * 查询设备
     */
    public void queryDevices() {
        PERFORMANCE textContent = new PERFORMANCE();
        textContent.setOP("QUERY");
        textContent.setMethod("DEVICES");
        textContent.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(textContent);
        MQTTController.getSingleton().publish(exclusive.getSub(), text, new MqttActionHandler(RouterDevice.OP_QUERY_DEVICES));
    }

    /**
     * 重启设备
     */
    public void reboot() {
        PERFORMANCE textContent = new PERFORMANCE();
        textContent.setOP("REBOOT");
        textContent.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(textContent);
        MQTTController.getSingleton().publish(exclusive.getSub(), text, new MqttActionHandler(RouterDevice.OP_REBOOT));
    }

    /**
     * 查询LAN
     */
    public void queryLan() {
        Log.i(TAG, "queryLan");
        PERFORMANCE textContent = new PERFORMANCE();
        textContent.setTimestamp(System.currentTimeMillis() / 1000);
        textContent.setOP("QUERY");
        textContent.setMethod("LAN");
        Gson gson = new Gson();
        String text = gson.toJson(textContent);
        MQTTController.getSingleton().publish(exclusive.getSub(), text, new MqttActionHandler(RouterDevice.OP_QUERY_LAN));
    }

    /**
     * 查询WAN
     */
    public void queryWan() {
        PERFORMANCE textContent = new PERFORMANCE();
        textContent.setOP("QUERY");
        textContent.setMethod("WAN");
        textContent.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(textContent);
        MQTTController.getSingleton().publish(exclusive.getSub(), text, new MqttActionHandler(RouterDevice.OP_QUERY_WAN));
    }

    /**
     * 查询无线中继
     */
    public void queryWifiRelay() {
        PERFORMANCE textContent = new PERFORMANCE();
        textContent.setOP("QUERY");
        textContent.setMethod("WIFIRELAY");
        textContent.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(textContent);
        MQTTController.getSingleton().publish(exclusive.getSub(), text, new MqttActionHandler(RouterDevice.OP_QUERY_WIFIRELAY));
    }

    /**
     * 查询wifi
     */
    public void queryWifi() {
        PERFORMANCE textContent = new PERFORMANCE();
        textContent.setOP("QUERY");
        textContent.setMethod("WIFI");
        textContent.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(textContent);
        MQTTController.getSingleton().publish(exclusive.getSub(), text, new MqttActionHandler(RouterDevice.OP_QUERY_WIFI));
    }

    /**
     * 查询Qos
     */
    public void queryQos() {
        PERFORMANCE textContent = new PERFORMANCE();
        textContent.setOP("QUERY");
        textContent.setMethod("QOS");
        textContent.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(textContent);
        MQTTController.getSingleton().publish(exclusive.getSub(), text, new MqttActionHandler(RouterDevice.OP_QUERY_QOS));
    }

    /**
     * 设置wifi
     */
    public void setWifi(Wifi wifi) {
        wifi.setOP("WIFI");
        wifi.setMethod("SET");
        wifi.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(wifi);
        MQTTController.getSingleton().publish(exclusive.getSub(), text, new MqttActionHandler(RouterDevice.OP_SET_WIFI));
    }

    /**
     * 设置黑名单列表，从列表中移除
     */
    public void setDeviceControl(DeviceControl control) {
        control.setOP("DEVICES");
        control.setMethod("CONTROL");
        control.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(control);
        MQTTController.getSingleton().publish(exclusive.getSub(), text, new MqttActionHandler(RouterDevice.OP_SET_DEVICE_CONTROL));
    }

    /**
     * 设置上网方式
     */
    public void setWan(Proto proto) {
        PERFORMANCE textContent = new PERFORMANCE();
        textContent.setOP("WAN");
        textContent.setMethod("SET");
        textContent.setProto(proto);
        textContent.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(textContent);
        MQTTController.getSingleton().publish(exclusive.getSub(), text, new MqttActionHandler(RouterDevice.OP_SET_WAN));
    }

    /**
     * 设置LAN
     */
    public void setLan(Lan lan) {
        lan.setOP("LAN");
        lan.setMethod("SET");
        lan.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(lan);
        MQTTController.getSingleton().publish(exclusive.getSub(), text, new MqttActionHandler(RouterDevice.OP_SET_LAN));
    }

    /**
     * 设置Qos
     */
    public void setQos(Qos qos) {

        qos.setOP("QOS");
        qos.setMethod("SET");
        qos.setTimestamp(System.currentTimeMillis() / 1000);
        Gson gson = new Gson();
        String text = gson.toJson(qos);
        MQTTController.getSingleton().publish(exclusive.getSub(), text, new MqttActionHandler(RouterDevice.OP_SET_QOS));
    }


    private class MqttActionHandler implements IMqttActionListener {
        private String action;
        public MqttActionHandler(String action) {
            this.action = action;
        }
        @Override
        public void onSuccess(IMqttToken iMqttToken) {
            notifySuccess(action);
        }
        @Override
        public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
            throwable.printStackTrace();
            Log.d(DeplinkSDK.SDK_TAG, "--->Mqtt failure: " + throwable.getMessage());
            String error = "操作失败";
            notifyFailure(action, error);
        }
    }

    private void notifySuccess(String action) {
        if (mSDKCoordinator != null) {
            mSDKCoordinator.notifyDeviceOpSuccess(action, deviceKey);
        }
    }
    private void notifyFailure(String action, String error) {
        if (mSDKCoordinator != null) {
            mSDKCoordinator.notifyDeviceOpFailure(action, deviceKey, new Throwable(error));
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
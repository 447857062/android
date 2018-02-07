package com.deplink.sdk.android.sdk.manager;

import android.text.TextUtils;
import android.util.Log;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.MqttAction;
import com.deplink.sdk.android.sdk.SDKAction;
import com.deplink.sdk.android.sdk.bean.DeviceInfo;
import com.deplink.sdk.android.sdk.bean.DeviceRoot;
import com.deplink.sdk.android.sdk.bean.DeviceUpgradeInfo;
import com.deplink.sdk.android.sdk.device.router.BaseDevice;
import com.deplink.sdk.android.sdk.device.router.RouterDevice;
import com.deplink.sdk.android.sdk.interfaces.MqttListener;
import com.deplink.sdk.android.sdk.interfaces.SDKCoordinator;
import com.deplink.sdk.android.sdk.json.Content;
import com.deplink.sdk.android.sdk.json.DeviceImageUpgrade;
import com.deplink.sdk.android.sdk.mqtt.MQTTController;
import com.deplink.sdk.android.sdk.rest.RestfulTools;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by huqs on 2016/6/30.
 */
public class DeviceManager implements MqttListener {
    private static final String TAG = "DeviceManager";
    final static private String XML_FMT_NOTIFY_DEVICE = "xmlNotifyDevice";
    final static private String XML_FMT_IMAGE_UPGRADE = "xmlImageUpgrade";

    private SDKCoordinator mSDKCoordinator = null;
    LinkedHashMap<String, BaseDevice> mDeviceMap = new LinkedHashMap<>();
    LinkedHashMap<String, BaseDevice> mDeviceTopics = new LinkedHashMap<>();

    public DeviceManager(SDKCoordinator coordinator) {
        mSDKCoordinator = coordinator;
    }

    /**
     * MQTT连接建立
     */
    public void onMQTTConnection() {
        Log.d(DeplinkSDK.SDK_TAG, "---->MQTT connected");
        //订阅所有设备的sub topic
        Iterator it = mDeviceTopics.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            MQTTController.getSingleton().subscribe(key, this);
        }
        String userTopic = mSDKCoordinator.getUserSession().getTopic_sub().get(0);
        Log.i(TAG,"userTopic="+userTopic);
        MQTTController.getSingleton().subscribe(userTopic, this);
        mSDKCoordinator.notifySuccess(SDKAction.CONNECTED);
    }

    public List<BaseDevice> getDeviceList() {

        return mapToList(mDeviceMap);
    }

    private void printHashMap(HashMap attMap) {
        Iterator i = attMap.entrySet().iterator();
        while (i.hasNext()) {
            Object o = i.next();
            String key = o.toString();
            //这样就可以遍历该HashMap的key值了。
            Log.i(TAG, "hashmap key=" + key);
        }

    }

    public BaseDevice getDevice(String deviceKey) {
        printHashMap(mDeviceMap);
        return mDeviceMap.get(deviceKey);
    }

    public void cleanup() {
        mDeviceMap.clear();
        mDeviceTopics.clear();
    }

    public void getDeviceBinding() {
        if (mSDKCoordinator.getUserSession() == null || mSDKCoordinator.getUserInfo() == null) {
            return;
        }
        RestfulTools.getSingleton().getBinding(new Callback<DeviceRoot>() {
            @Override
            public void onResponse(Call<DeviceRoot> call, Response<DeviceRoot> response) {
                switch (response.code()) {
                    case 200:
                        DeviceRoot deviceRoot = response.body();
                        deviceRoot.getDevice_list();
                        Log.i(TAG, "getBinding绑定的设备 =" + deviceRoot.getDevice_list().size());
                        synchronized (mDeviceMap) {
                            mDeviceMap.clear();
                            for (DeviceInfo deviceInfo : deviceRoot.getDevice_list()) {
                                RouterDevice device = new RouterDevice(mSDKCoordinator);
                                if (TextUtils.isEmpty(deviceInfo.getDeviceName())) {
                                    device.setName(deviceInfo.getProduct());
                                } else {
                                    device.setName(deviceInfo.getDeviceName());
                                }
                                device.setDeviceSN(deviceInfo.getDeviceSn());
                                device.setAutoUpgrade(0 != deviceInfo.getAuto_upgrade());
                                if (deviceInfo.getChannels().getCommon() != null) {
                                    device.setCommonCh(deviceInfo.getChannels().getCommon());
                                }
                                if (deviceInfo.getChannels().getSecondary() != null) {
                                    device.setExclusiveCh(deviceInfo.getChannels().getSecondary());
                                }
                                if (device.getCommonCh() != null) {
                                    mDeviceTopics.put(device.getCommonCh().getPub(), device);
                                }
                                if (device.getExclusiveCh() != null) {
                                    mDeviceTopics.put(device.getExclusiveCh().getPub(), device);
                                }
                                device.retrieveDeviceCookie();
                                device.retrieveUpgradeInfo();
                                device.retrieveDeviceProperty();
                                mDeviceMap.put(device.getDeviceKey(), device);
                            }
                            Iterator it = mDeviceTopics.keySet().iterator();
                            List<String> topics = new ArrayList<>();
                            while (it.hasNext()) {
                                String key = it.next().toString();
                                BaseDevice device = mDeviceTopics.get(key);
                                if (mDeviceMap.get(device.getDeviceKey()) == null) {
                                    MQTTController.getSingleton().unsubscribe(key);
                                    topics.add(key);
                                } else {
                                    MQTTController.getSingleton().subscribe(key, DeviceManager.this);
                                }
                            }
                            for (String topic : topics) {
                                mDeviceTopics.remove(topic);
                            }
                        }
                        String uuid=  DeplinkSDK.getSDKManager().getUserInfo().getUuid();
                        Log.i(TAG,"devicemanager uuid="+uuid);
                        mSDKCoordinator.notifySuccess(SDKAction.GET_BINDING);
                        break;
                }
            }

            @Override
            public void onFailure(Call<DeviceRoot> call, Throwable t) {
                String error = "未能获取已绑定设备";
                mSDKCoordinator.notifyFailure(SDKAction.GET_BINDING, error);
            }
        });
    }

    private List<BaseDevice> mapToList(LinkedHashMap<String, BaseDevice> map) {
        List<BaseDevice> lis = new ArrayList<>();
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            lis.add(map.get(key));
        }
        Log.i(TAG, "mapToList" + lis.size());
        return lis;
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken, MqttAction cation, String clientHandle, String topic) {
        Log.i(TAG, "onSuccess");
    }

    @Override
    public void onFailure(IMqttToken token, Throwable exception, MqttAction cation, String clientHandle, String topic) {
        Log.i(TAG, "onFailure");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        Log.i(TAG, "messageArrived" + message.toString());
        if (topic.equals(mSDKCoordinator.getUserSession().getTopic_sub().get(0))) {
            String xmlStr = new String(message.getPayload());
            String msgFormat = null;
            Gson gson = new Gson();
            Content content = gson.fromJson(xmlStr, Content.class);
            String op;
            String method;
            op = content.getOP();
            method = content.getMethod();
           if ("IMAGE".equalsIgnoreCase(op) && "UPGRADE".equalsIgnoreCase(method)) {
                msgFormat = XML_FMT_IMAGE_UPGRADE;
            }
            processMqttMsg(message);
            if (msgFormat == null) {
                return;
            }
            //parse by type
            switch (msgFormat) {
                case XML_FMT_IMAGE_UPGRADE:
                    DeviceImageUpgrade upgrade = gson.fromJson(xmlStr, DeviceImageUpgrade.class);
                    synchronized (mDeviceMap) {
                        Iterator it = mDeviceMap.keySet().iterator();
                        while (it.hasNext()) {
                            String key = it.next().toString();
                            BaseDevice device = mDeviceMap.get(key);
                            if (device.getProductKey().equals(upgrade.getProductKey())) {
                                DeviceUpgradeInfo info = new DeviceUpgradeInfo();
                                info.setProduct_key(upgrade.getProductKey());
                                info.setVersion(upgrade.getSoftwareVersion());
                                info.setProtocol(upgrade.getProtocol());
                                info.setImg_url(upgrade.getImgUrl());
                                info.setBak_protocol(upgrade.getBakProtocol());
                                info.setBak_img_url(upgrade.getBakImgUrl());
                                info.setFile_len(upgrade.getFileLen());
                                info.setFile_md5(upgrade.getMD5());
                                info.setUpgrade_state(BaseDevice.UPGRADE_STATE_READY);
                                device.setUpgradeInfo(info);
                                mSDKCoordinator.notifyDeviceUpgrade(device.getDeviceKey());
                            }
                        }
                    }
                    break;
            }
            return;
        }
        processMqttMsg(message);
        //设备状态改变
        RouterDevice device = (RouterDevice) mDeviceTopics.get(topic);
        if (device == null) {
            return;
        }
        int update = device.processMqttMsg(topic, message);
        if (update != BaseDevice.MSG_DUMMY) {
            synchronized (mDeviceMap) {
                mDeviceMap.put(device.getDeviceKey(), device);
            }
            Log.i(TAG, "notifyDeviceDataUpdate update=" + update);
            mSDKCoordinator.notifyDeviceDataUpdate(device.getDeviceKey(), update);
        }
    }

    private void processMqttMsg(MqttMessage message) {
        String jsonString = new String(message.getPayload());
        Log.i(TAG,"processMqttMsg:"+jsonString);
        mSDKCoordinator.notifyHomeGeniusResult(jsonString);
    }
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.i(TAG, "deliveryComplete");
    }
}

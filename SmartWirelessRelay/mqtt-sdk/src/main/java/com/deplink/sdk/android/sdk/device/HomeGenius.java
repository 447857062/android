package com.deplink.sdk.android.sdk.device;

import android.util.Log;

import com.deplink.sdk.android.sdk.DeplinkSDK;
import com.deplink.sdk.android.sdk.bean.TopicPair;
import com.deplink.sdk.android.sdk.json.homegenius.QueryOptions;
import com.deplink.sdk.android.sdk.mqtt.MQTTController;
import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

/**
 * Created by huqs on 2016/7/6.
 * 路由器设备
 */
public class HomeGenius  {
    public static final String TAG = "HomeGenius";
    /**
     * 设备与本APP用户的专用通道
     */
    protected TopicPair exclusive;
    /**
     * 要求设备上报
     */
    public void bindApp(String uid,String userUuid) {
        QueryOptions queryCmd = new QueryOptions();
        queryCmd.setOP("SET");
        queryCmd.setMethod("BindApp");
        queryCmd.setTimestamp();
        queryCmd.setAuthId(uid);
        queryCmd.setSenderId(userUuid);
        Gson gson = new Gson();
        String text = gson.toJson(queryCmd);
        MQTTController.getSingleton().publish("device/44ebba9138a4b2b8c3f391f587148ff0/sub", text, new MqttActionHandler(""));
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
}
package com.deplink.sdk.android.sdk.interfaces;


import com.deplink.sdk.android.sdk.MqttAction;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by huqs on 2016/7/5.
 */
public interface MqttListener {
    void connectionLost(final Throwable cause);
    void onSuccess(IMqttToken asyncActionToken, MqttAction cation, String clientHandle,String topic);
    void onFailure(IMqttToken token, Throwable exception, MqttAction cation, String clientHandle,String topic) ;
    void messageArrived(String topic, MqttMessage message);
    void deliveryComplete(IMqttDeliveryToken token);
}

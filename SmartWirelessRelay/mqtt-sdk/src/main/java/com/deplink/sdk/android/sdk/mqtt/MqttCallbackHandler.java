/*
 * Licensed Materials - Property of IBM
 *
 * 5747-SM3
 *
 * (C) Copyright IBM Corp. 1999, 2012 All Rights Reserved.
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 *
 */
package com.deplink.sdk.android.sdk.mqtt;

import android.content.Context;
import android.util.Log;

import com.deplink.sdk.android.R;
import com.deplink.sdk.android.sdk.interfaces.MqttListener;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
/**
 * Handles call backs from the MQTT Client
 *
 */
public class MqttCallbackHandler implements MqttCallback {

  private static final String TAG="MqttCallbackHandler";
  /** {@link Context} for the application used to format and import external strings**/
  private Context context;
  private String clientHandle;
  private MqttListener mqttListener;
  /**
   * Creates an <code>MqttCallbackHandler</code> object
   * @param context The application's context
   * @param clientHandle The handle to a {@link Connection} object
   */
  public MqttCallbackHandler(Context context, String clientHandle, MqttListener mqttListener)
  {
    this.context = context;
    this.clientHandle = clientHandle;
    this.mqttListener=mqttListener;

  }

  /**
   * @see org.eclipse.paho.client.mqttv3.MqttCallback#connectionLost(java.lang.Throwable)
   */
  @Override
  public void connectionLost( Throwable cause) {
    if (cause != null) {
      Connection c = Connections.getInstance().getConnection(clientHandle);
      c.addAction("Connection Lost");
      c.changeConnectionStatus(Connection.ConnectionStatus.DISCONNECTED);
    }
    mqttListener.connectionLost(cause);
  }

  /**
   * @see org.eclipse.paho.client.mqttv3.MqttCallback#messageArrived(java.lang.String, org.eclipse.paho.client.mqttv3.MqttMessage)
   */
  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    this.mqttListener.messageArrived(topic,message);
    //Get connect object associated with this object
    Connection c = Connections.getInstance().getConnection(clientHandle);
    //create arguments to format message arrived notifcation string
    String[] args = new String[2];
    args[0] = new String(message.getPayload());
    args[1] = topic;
    Log.i(TAG,"message arrived ="+args[0]);
    //get the string from strings.xml and format
    String messageString = context.getString(R.string.messageRecieved, (Object[]) args);
    //update client history
    c.addAction(messageString);
  }

  /**
   * @see org.eclipse.paho.client.mqttv3.MqttCallback#deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken)
   */
  @Override
  public void deliveryComplete(IMqttDeliveryToken token) {
    // Do nothing
    this.mqttListener.deliveryComplete(token);
  }

}

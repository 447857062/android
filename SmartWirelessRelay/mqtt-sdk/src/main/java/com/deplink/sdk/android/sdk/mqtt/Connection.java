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
import android.text.Html;
import android.text.Spanned;

import com.deplink.sdk.android.R;
import com.deplink.sdk.android.sdk.mqtt.service.MqttClientAndroidService;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * 
 * Represents a {@link MqttClientAndroidService} and the actions it has performed
 *
 */
public class Connection {

  /*
   * Basic Information about the client
   */
  /** ClientHandle for this Connection Object**/
  private String clientHandle = null;
  /** The clientId of the client associated with this <code>Connection</code> object **/
  private String clientId = null;
  /** The host that the {@link MqttClientAndroidService} represented by this <code>Connection</code> is represented by **/
  private String host = null;
  /** {@link ConnectionStatus} of the {@link MqttClientAndroidService} represented by this <code>Connection</code> object. Default value is {@link ConnectionStatus#NONE} **/
  private ConnectionStatus status = ConnectionStatus.NONE;
  /** The history of the {@link MqttClientAndroidService} represented by this <code>Connection</code> object **/
  private ArrayList<String> history = null;
  /** The {@link MqttClientAndroidService} instance this class represents**/
  private MqttClientAndroidService client = null;

  private ArrayList<PropertyChangeListener> listeners = new ArrayList<>();

  /** The {@link Context} of the application this object is part of**/
  private Context context = null;

  /** The {@link MqttConnectOptions} that were used to connect this client**/
  private MqttConnectOptions conOpt;
  /**
   * Connections status for  a connect
   */
  public enum ConnectionStatus {

    /** Client is Connecting **/
    CONNECTING,
    /** Client is Connected **/
    CONNECTED,
    /** Client is Disconnecting **/
    DISCONNECTING,
    /** Client is Disconnected **/
    DISCONNECTED,
    /** Client has encountered an Error **/
    ERROR,
    /** Status is unknown **/
    NONE
  }

  /**
   * Creates a connect object with the server information and the client
   * hand which is the reference used to pass the client around activities
   * @param clientHandle The handle to this <code>Connection</code> object
   * @param clientId The Id of the client
   * @param host The server which the client is connecting to
   * @param context The application context
   * @param client The MqttClientAndroidService which communicates with the service for this connect
   */
  public Connection(String clientHandle, String clientId, String host,
                    Context context, MqttClientAndroidService client) {
    //generate the client handle from its hash code
    this.clientHandle = clientHandle;
    this.clientId = clientId;
    this.host = host;
    /* The port on the server this client is connecting to */
    this.context = context;
    this.client = client;
    history = new ArrayList<>();
    String sb = "Client: " +
            clientId +
            " created";
    addAction(sb);
  }

  /**
   * Add an action to the history of the client
   * @param action the history item to add
   */
  public void addAction(String action) {

    Object[] args = new String[1];
    SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.dateFormat));
    args[0] = sdf.format(new Date());

    String timestamp = context.getString(R.string.timestamp, args);
    history.add(action + timestamp);

    notifyListeners(new PropertyChangeEvent(this, ActivityConstants.historyProperty, null, null));
  }

  /**
   * Generate an array of Spanned items representing the history of this
   * connect.
   * 
   * @return an array of history entries
   */
  public Spanned[] history() {

    int i = 0;
    Spanned[] array = new Spanned[history.size()];

    for (String s : history) {
      if (s != null) {
        array[i] = Html.fromHtml(s);
        i++;
      }
    }

    return array;

  }

  /**
   * Gets the client handle for this connect
   * @return client Handle for this connect
   */
  public String handle() {
    return clientHandle;
  }

  /**
   * Changes the connect status of the client
   * @param connectionStatus The connect status of this connect
   */
  public void changeConnectionStatus(ConnectionStatus connectionStatus) {
    status = connectionStatus;
    notifyListeners((new PropertyChangeEvent(this, ActivityConstants.ConnectionStatusProperty, null, null)));
  }

  /**
   * A string representing the state of the client this connect
   * object represents
   * 
   * 
   * @return A string representing the state of the client
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(clientId);
    sb.append("\n ");

    switch (status) {
      case CONNECTED :
        sb.append(context.getString(R.string.connectedto));
        break;
      case DISCONNECTED :
        sb.append(context.getString(R.string.disconnected));
        break;
      case NONE :
        sb.append(context.getString(R.string.no_status));
        break;
      case CONNECTING :
        sb.append(context.getString(R.string.connecting));
        break;
      case DISCONNECTING :
        sb.append(context.getString(R.string.disconnecting));
        break;
      case ERROR :

        sb.append(context.getString(R.string.connectionError));
    }
    sb.append(" ");
    sb.append(host);

    return sb.toString();
  }

  /**
   * Compares two connect objects for equality
   * this only takes account of the client handle
   * @param o The object to compare to
   * @return true if the client handles match
   */
  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Connection)) {
      return false;
    }

    Connection c = (Connection) o;

    return clientHandle.equals(c.clientHandle);

  }

  /**
   * Get the client Id for the client this object represents
   * @return the client id for the client this object represents
   */
  public String getId() {
    return clientId;
  }

  /**
   * Get the host name of the server that this connect object is associated with
   * @return the host name of the server this connect object is associated with
   */
  public String getHostName() {

    return host;
  }

  /**
   * Determines if the client is in a state of connecting or connected.
   * @return if the client is connecting or connected
   */
  public boolean isConnectedOrConnecting() {
    return (status == ConnectionStatus.CONNECTED) || (status == ConnectionStatus.CONNECTING);
  }

  /**
   * Client is currently not in an error state
   * @return true if the client is in an error state
   */
  public boolean noError() {
    return status != ConnectionStatus.ERROR;
  }

  /**
   * Gets the client which communicates with the android service.
   * @return the client which communicates with the android service
   */
  public MqttClientAndroidService getClient() {
    return client;
  }

  /**
   * Add the connectOptions used to connect the client to the server
   * @param connectOptions the connectOptions used to connect to the server
   */
  public void addConnectionOptions(MqttConnectOptions connectOptions) {
    conOpt = connectOptions;
  }

  /**
   * Get the connectOptions used to connect this client to the server
   * @return The connectOptions used to connect the client to the server
   */
  public MqttConnectOptions getConnectionOptions()
  {
    return conOpt;
  }

  /**
   * Register a {@link PropertyChangeListener} to this object
   * @param listener the listener to register
   */
  public void registerChangeListener(PropertyChangeListener listener)
  {
    listeners.add(listener);
  }

  /**
   * Remove a registered {@link PropertyChangeListener}
   * @param listener A reference to the listener to remove
   */
  public void removeChangeListener(PropertyChangeListener listener)
  {
    if (listener != null) {
      listeners.remove(listener);
    }
  }

  /**
   * Notify {@link PropertyChangeListener} objects that the object has been updated
   * @param propertyChangeEvent 
   */
  private void notifyListeners(PropertyChangeEvent propertyChangeEvent)
  {
    for (PropertyChangeListener listener : listeners)
    {
      listener.propertyChange(propertyChangeEvent);
    }
  }
}

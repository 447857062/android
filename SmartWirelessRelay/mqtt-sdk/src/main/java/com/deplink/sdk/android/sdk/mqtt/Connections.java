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

import com.deplink.sdk.android.sdk.mqtt.service.MqttClientAndroidService;

import java.util.HashMap;
import java.util.Map;


/**
 * <code>Connections</code> is a singleton class which stores all the connect objects
 * in one central place so they can be passed between activities using a client
 * handle
 *
 */
public class Connections {

  /** Singleton instance of <code>Connections</code>**/
  private static Connections instance = null;

  /** List of {@link Connection} objects**/
  private HashMap<String, Connection> connections = null;

  private Connections()
  {
    connections = new HashMap<>();
  }

  /**
   * Returns an already initialised instance of <code>Connections</code>, if Connections has yet to be created, it will
   * create and return that instance
   * @return Connections instance
   */
  public synchronized static Connections getInstance()
  {
    if (instance == null) {
      instance = new Connections();
    }

    return instance;
  }

  /**
   * Finds and returns a connect object that the given client handle points to
   * @param handle The handle to the <code>Connection</code> to return
   * @return a connect associated with the client handle, <code>null</code> if one is not found
   */
  public Connection getConnection(String handle)
  {

    return connections.get(handle);
  }

  /**
   * Adds a <code>Connection</code> object to the collection of connections associated with this object
   * @param connection
   */
  public void addConnection(Connection connection)
  {
    connections.put(connection.handle(), connection);
  }

  /**
   * Create a fully initialised <code>MqttClientAndroidService</code> for the parameters given
   * @param context The Applications context
   * @param serverURI The ServerURI to connect to
   * @param clientId The clientId for this client
   * @return new instance of MqttClientAndroidService
   */
  public MqttClientAndroidService createClient(Context context, String serverURI, String clientId)
  {
    return new MqttClientAndroidService(context, serverURI, clientId);
  }

  /**
   * Get all the connections associated with this <code>Connections</code> object.
   * @return <code>Map</code> of connections
   */
  public Map<String, Connection> getConnections()
  {
    return connections;
  }

}

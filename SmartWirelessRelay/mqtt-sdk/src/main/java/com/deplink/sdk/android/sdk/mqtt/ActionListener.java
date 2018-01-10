package com.deplink.sdk.android.sdk.mqtt;

import com.deplink.sdk.android.sdk.MqttAction;
import com.deplink.sdk.android.sdk.interfaces.MqttListener;
import com.deplink.sdk.android.sdk.mqtt.service.MqttClientAndroidService;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;


/**
 * This Class handles receiving information from the
 * {@link MqttClientAndroidService} and updating the {@link Connection} associated with
 * the action
 */
public class ActionListener implements IMqttActionListener {

  /**
   * Actions that can be performed Asynchronously <strong>and</strong> associated with a
   * {@link ActionListener} object
   *
   */

  /**
   * The {@link MqttAction} that is associated with this instance of
   * <code>ActionListener</code>
   **/
  private MqttAction action;
  /** The arguments passed to be used for formatting strings**/
  /** Handle of the {@link Connection} this action was being executed on **/
  private String clientHandle;
  private MqttListener mqttListener;
  private String topic;


  /**
   * Creates a generic action listener for actions performed form any activity
   * 
   * @param action
   *            The action that is being performed
   * @param clientHandle
   *            The handle for the client which the action is being performed
   *            on
   * @param topic
   *            topic
   */
  public ActionListener(MqttAction action, String clientHandle, MqttListener mqttListener,String topic) {
    this.action = action;
    this.clientHandle = clientHandle;
    this.mqttListener=mqttListener;
    this.topic=topic;
  }

  /**
   * The action associated with this listener has been successful.
   * 
   * @param asyncActionToken
   *            This argument is not used
   */
  @Override
  public void onSuccess(IMqttToken asyncActionToken) {
    if(this.mqttListener != null ){
      this.mqttListener.onSuccess(asyncActionToken,action,clientHandle,topic);
    }

    switch (action) {
      case CONNECT :
        connect();
        break;
      case DISCONNECT :
        disconnect();
        break;
      case SUBSCRIBE :
        subscribe();
        break;
      case PUBLISH :
        publish();
        break;
    }

  }

  /**
   * A publish action has been successfully completed, update connect
   * object associated with the client this action belongs to, then notify the
   * user of success
   */
  private void publish() {
    Connection c = Connections.getInstance().getConnection(clientHandle);
    String actionTaken = "发布成功";
    c.addAction(actionTaken);
  }

  /**
   * A subscribe action has been successfully completed, update the connect
   * object associated with the client this action belongs to and then notify
   * the user of success
   */
  private void subscribe() {
    Connection c = Connections.getInstance().getConnection(clientHandle);
    String actionTaken = "订阅成功";
    c.addAction(actionTaken);
  }

  /**
   * A disconnection action has been successfully completed, update the
   * connect object associated with the client this action belongs to and
   * then notify the user of success.
   */
  private void disconnect() {
    Connection c = Connections.getInstance().getConnection(clientHandle);
    c.changeConnectionStatus(Connection.ConnectionStatus.DISCONNECTED);
    String actionTaken = "Disconnected";
    c.addAction(actionTaken);
  }

  /**
   * A connect action has been successfully completed, update the
   * connect object associated with the client this action belongs to and
   * then notify the user of success.
   */
  private void connect() {

    Connection c = Connections.getInstance().getConnection(clientHandle);
    c.changeConnectionStatus(Connection.ConnectionStatus.CONNECTED);
    c.addAction("Client Connected");
  }

  /**
   * The action associated with the object was a failure
   * 
   * @param token
   *            This argument is not used
   * @param exception
   *            The exception which indicates why the action failed
   */
  @Override
  public void onFailure(IMqttToken token, Throwable exception) {
	  //System.out.println("onFailure----------->"+exception.getMessage());

    if(this.mqttListener != null ){
      this.mqttListener.onFailure(token,exception,action,clientHandle,this.topic);
    }

    switch (action) {
      case CONNECT :
        connect(exception);
        break;
      case DISCONNECT :
        disconnect(exception);
        break;
      case SUBSCRIBE :
        subscribe(exception);
        break;
      case PUBLISH :
       // publish(exception);
        break;
    }

  }

  /**
   * A publish action was unsuccessful, notify user and update client history
   * 
   * @param exception
   *            This argument is not used
   */
  private void publish(Throwable exception) {
    Connection c = Connections.getInstance().getConnection(clientHandle);
    String action = "发布失败";
    c.addAction(action);
  }

  /**
   * A subscribe action was unsuccessful, notify user and update client history
   * @param exception
   */
  private void subscribe(Throwable exception) {
    Connection c = Connections.getInstance().getConnection(clientHandle);
    String action ="订阅失败";
    c.addAction(action);
  }

  /**
   * A disconnect action was unsuccessful, notify user and update client history
   * @param exception
   */
  private void disconnect(Throwable exception) {
    Connection c = Connections.getInstance().getConnection(clientHandle);
    c.changeConnectionStatus(Connection.ConnectionStatus.DISCONNECTED);
    c.addAction("Disconnect Failed - an error occured");

  }

  /**
   * A connect action was unsuccessful, notify the user and update client history
   * @param exception
   */
  private void connect(Throwable exception) {
    Connection c = Connections.getInstance().getConnection(clientHandle);
    c.changeConnectionStatus(Connection.ConnectionStatus.ERROR);
    c.addAction("Client failed to connect");
  }

}
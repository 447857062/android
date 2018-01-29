/*
============================================================================ 
Licensed Materials - Property of IBM

5747-SM3
 
(C) Copyright IBM Corp. 1999, 2012 All Rights Reserved.
 
US Government Users Restricted Rights - Use, duplication or
disclosure restricted by GSA ADP Schedule Contract with
IBM Corp.
============================================================================
 */
package com.deplink.sdk.android.sdk.mqtt.service;

/**
 * Various strings used to identify operations or data in the Android MQTT
 * service, mainly used in Intents passed between Activities and the Service.
 */
public interface MqttServiceConstants {

    /*
     * Attibutes of messages <p> Used for the column names in the database
     */
    String DUPLICATE = "duplicate";
    String RETAINED = "retained";
    String QOS = "qos";
    String PAYLOAD = "payload";
    String DESTINATION_NAME = "destinationName";
    String CLIENT_HANDLE = "clientHandle";
    String MESSAGE_ID = "messageId";
    /* Tags for actions passed between the Activity and the Service */
    String SEND_ACTION = "send";
    String UNSUBSCRIBE_ACTION = "unsubscribe";
    String SUBSCRIBE_ACTION = "subscribe";
    String DISCONNECT_ACTION = "disconnect";
    String CONNECT_ACTION = "connect";
    String MESSAGE_ARRIVED_ACTION = "messageArrived";
    String MESSAGE_DELIVERED_ACTION = "messageDelivered";
    String ON_CONNECTION_LOST_ACTION = "onConnectionLost";
    String TRACE_ACTION = "trace";
    /* Identifies an Intent which calls back to the Activity */
    String CALLBACK_TO_ACTIVITY = MqttService.TAG + ".callbackToActivity";
    /* Identifiers for extra data on Intents broadcast to the Activity */
    String CALLBACK_ACTION = MqttService.TAG + ".callbackAction";
    String CALLBACK_STATUS = MqttService.TAG + ".callbackStatus";
    String CALLBACK_CLIENT_HANDLE = MqttService.TAG + "." + CLIENT_HANDLE;
    String CALLBACK_ERROR_MESSAGE = MqttService.TAG + ".errorMessage";
    String CALLBACK_EXCEPTION_STACK = MqttService.TAG + ".exceptionStack";
    String CALLBACK_INVOCATION_CONTEXT = MqttService.TAG + "." + "invocationContext";
    String CALLBACK_ACTIVITY_TOKEN = MqttService.TAG + "." + "activityToken";
    String CALLBACK_DESTINATION_NAME = MqttService.TAG + '.' + DESTINATION_NAME;
    String CALLBACK_MESSAGE_ID = MqttService.TAG + '.' + MESSAGE_ID;
    String CALLBACK_MESSAGE_PARCEL = MqttService.TAG + ".PARCEL";
    String CALLBACK_TRACE_SEVERITY = MqttService.TAG + ".traceSeverity";
    String CALLBACK_ERROR_NUMBER = MqttService.TAG + ".ERROR_NUMBER";
    // Prefices for actions used in intents for registering callback listeners
    String ALARM_INTENT_PREFIX = MqttService.TAG + ".ALARM_";
    String PING_TOPIC_PREFIX = MqttService.TAG + ".PING_";
}
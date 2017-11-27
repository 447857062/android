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
 * Interface for simple trace handling
 *
 */
interface MqttTraceHandler {

	void traceDebug(String source, String message);

	void traceError(String source, String message);
	
	void traceException(String source, String message, Exception e);

}
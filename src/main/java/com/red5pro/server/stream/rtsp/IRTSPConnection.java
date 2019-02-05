package com.red5pro.server.stream.rtsp;

import org.apache.mina.core.session.IoSession;

import com.red5pro.server.stream.IoSessionAware;

/**
 * Base interface for RTSP connection.
 * 
 * @author Andy Shaules
 * @author Paul Gregoire (paul@infrared5.com)
 */
public interface IRTSPConnection extends IoSessionAware {

	/**
	 * Use IoSessionAware.getIoSession().
	 * 
	 * @return session
	 */
	@Deprecated
	IoSession getIOSession();

	String getSessionId();

	/**
	 * Apply a QoS value to the connection.
	 * 
	 * @param qos
	 *            quality of service.
	 */
	void applyQoS(int qos);

}

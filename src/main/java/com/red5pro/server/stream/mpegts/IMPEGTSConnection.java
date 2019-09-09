package com.red5pro.server.stream.mpegts;

import com.red5pro.server.stream.IoSessionAware;

/**
 * Base interface for an MPEG-TS connection.
 * 
 * @author Paul Gregoire (paul@infrared5.com)
 */
public interface IMPEGTSConnection extends IoSessionAware {

	public final static int fourCC = 'h' | ('l' << 8) | ('s' << 16) | (' ' << 24);

	/**
	 * From RTMPConnection / IConnection
	 */
	public void close();

	/**
	 * Returns whether or not the connection is considered idle.
	 * 
	 * @return true if idle and false otherwise
	 */
	boolean isIdle();

	/**
	 * Return session identifier.
	 * 
	 * @return connection session id
	 */
	String getSessionId();

	/**
	 * Check whether connection is alive
	 * 
	 * @return true if not idle nor in a state of disconnected / disconnecting,
	 *         false otherwise
	 */
	boolean isConnected();

	/**
	 * Apply a QoS value to the connection.
	 * 
	 * @param qos
	 *            quality of service
	 */
	void applyQoS(int qos);

}

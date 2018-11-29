package com.red5pro.util;

import org.red5.server.api.IConnection;

/**
 * Utility methods to assist with various connection types.
 * 
 * @author Rajdeep Rath
 * @author Paul Gregoire
 */
public class ConnectionUtils {

	public enum ConnectionType {
		// fall-through for unknown or unimplemented connection type
		UNKNOWN("null"),
		// RTMP connection
		RTMP("org.red5.server.net.rtmp.RTMPMinaConnection"),
		// RTMPT connection
		RTMPT("org.red5.server.net.rtmpt.RTMPTConnection"),
		// Red5 Pro RTSP connection
		RTSP("com.red5pro.server.stream.rtsp.RTSPMinaConnection"),
		// Red5 Pro SecondScreen connection
		SECOND_SCREEN_MULTI("com.red5pro.server.secondscreen.net.MultiscreenMinaConnection"),
		// Red5 Pro SecondScreen via WebSocket connection
		SECOND_SCREEN_WS("com.red5pro.server.html5.websockets.net.WebsocketsMinaConnection"),
		// Red5 Pro WebRTC connection
		RTC("com.red5pro.webrtc.RTCConnection");

		final String className;

		ConnectionType(String className) {
			this.className = className;
		}

		public String getClassName() {
			return className;
		}

		public static Enum<ConnectionType> findByClassName(String canonicalName) {
			for (ConnectionType type : ConnectionType.values()) {
				if (type.getClassName().equals(canonicalName)) {
					return type;
				}
			}
			return UNKNOWN;
		}

	}

	/**
	 * Returns human readable string for a given instance implementing IConnection
	 * type.
	 * 
	 * @param connection
	 *            instance of IConnection
	 * @return connection type as a string in lower case
	 */
	public final static String getConnectionType(IConnection connection) {
		return ConnectionType.findByClassName(connection.getClass().getCanonicalName()).name().toLowerCase();
	}

	/**
	 * Returns human readable string for a given instance implementing IConnection
	 * type.
	 * 
	 * @param connection
	 *            instance of IConnection
	 * @return connection type enum
	 */
	public final static ConnectionType getConnectionTypeEnum(IConnection connection) {
		return (ConnectionType) ConnectionType.findByClassName(connection.getClass().getCanonicalName());
	}

	/**
	 * Returns boolean true if connection is a RTMPMinaConnection object, false
	 * otherwise
	 * 
	 * @param connection
	 *            IConnection
	 * @return true if RTMP and false otherwise
	 */
	public static boolean isRTMP(IConnection connection) {
		return ConnectionType.RTMP.equals(getConnectionTypeEnum(connection));
	}

	/**
	 * Returns boolean true if connection is a RTSPMinaConnection object, false
	 * otherwise
	 * 
	 * @param connection
	 *            IConnection
	 * @return true if RTSP and false otherwise
	 */
	public static boolean isRTSP(IConnection connection) {
		return ConnectionType.RTSP.equals(getConnectionTypeEnum(connection));
	}

	/**
	 * Returns boolean true if connection is a RTCConnection object, false otherwise
	 * 
	 * @param connection
	 *            IConnection
	 * @return true if RTC and false otherwise
	 */
	public static boolean isRTC(IConnection connection) {
		return ConnectionType.RTC.equals(getConnectionTypeEnum(connection));
	}

	/*
	 * public static void main(String[] args) { // NPE expected, and working as it
	 * should //System.out.println("Null? " + ConnectionUtils.isRTMP(null) +
	 * " return type: " + ConnectionUtils.getConnectionType(null));
	 * RTMPMinaConnection rtmp = new RTMPMinaConnection();
	 * System.out.println("RTMP? " + ConnectionUtils.isRTMP(rtmp) + " return type: "
	 * + ConnectionUtils.getConnectionType(rtmp)); }
	 */
}

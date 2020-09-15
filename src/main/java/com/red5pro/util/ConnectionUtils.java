//
// Copyright © 2020 Infrared5, Inc. All rights reserved.
//
// The accompanying code comprising examples for use solely in conjunction with Red5 Pro (the "Example Code")
// is  licensed  to  you  by  Infrared5  Inc.  in  consideration  of  your  agreement  to  the  following
// license terms  and  conditions.  Access,  use,  modification,  or  redistribution  of  the  accompanying
// code  constitutes your acceptance of the following license terms and conditions.
//
// Permission is hereby granted, free of charge, to you to use the Example Code and associated documentation
// files (collectively, the "Software") without restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the following conditions:
//
// The Software shall be used solely in conjunction with Red5 Pro. Red5 Pro is licensed under a separate end
// user  license  agreement  (the  "EULA"),  which  must  be  executed  with  Infrared5,  Inc.
// An  example  of  the EULA can be found on our website at: https://account.red5pro.com/assets/LICENSE.txt.
//
// The above copyright notice and this license shall be included in all copies or portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,  INCLUDING  BUT
// NOT  LIMITED  TO  THE  WARRANTIES  OF  MERCHANTABILITY, FITNESS  FOR  A  PARTICULAR  PURPOSE  AND
// NONINFRINGEMENT.   IN  NO  EVENT  SHALL INFRARED5, INC. BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN  AN  ACTION  OF  CONTRACT,  TORT  OR  OTHERWISE,  ARISING  FROM,  OUT  OF  OR  IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
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
		// RTSP connection
		RTSP("com.red5pro.server.stream.rtsp.RTSPMinaConnection"),
		// SecondScreen connection
		SECOND_SCREEN_MULTI("com.red5pro.server.secondscreen.net.MultiscreenMinaConnection"),
		// SecondScreen via WebSocket connection
		SECOND_SCREEN_WS("com.red5pro.server.html5.websockets.net.WebsocketsMinaConnection"),
		// WebRTC connection
		RTC("com.red5pro.webrtc.RTCConnection"),
		// MPEG-TS connection
		MPEGTS("com.red5pro.mpegts.MPEGTSConnection");

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

	/**
	 * Returns boolean true if connection is a MPEGTSConnection object, false
	 * otherwise
	 * 
	 * @param connection
	 *            IConnection
	 * @return true if MPEGTS and false otherwise
	 */
	public static boolean isMPEGTS(IConnection connection) {
		return ConnectionType.MPEGTS.equals(getConnectionTypeEnum(connection));
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

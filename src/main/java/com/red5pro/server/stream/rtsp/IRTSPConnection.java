package com.red5pro.server.stream.rtsp;

import org.apache.mina.core.session.IoSession;

/**
 * Base interface for RTSP connection.
 * 
 * @author Andy Shaules
 * @author Paul Gregoire (paul@infrared5.com)
 */
public interface IRTSPConnection {

    IoSession getIOSession();

    String getSessionId();

}

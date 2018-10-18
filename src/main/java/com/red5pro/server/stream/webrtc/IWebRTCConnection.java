package com.red5pro.server.stream.webrtc;

import org.red5.net.websocket.WebSocketConnection;

/**
 * Base interface for WebRTC connection.
 * 
 * @author Paul Gregoire (paul@infrared5.com)
 */
public interface IWebRTCConnection {

    /**
     * Returns the signal channel for the connection.
     * 
     * @return signal channel or null if its not set
     */
    WebSocketConnection getSignalChannel();

    /** 
     * Returns whether or not this connection instance has a signal channel.
     * 
     * @return true if signal channel exists and false otherwise
     */
    boolean hasSignalChannel();

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
     * @return true if not idle nor in a state of disconnected / disconnecting, false otherwise
     */
    boolean isConnected();

    /**
     * Apply a QoS value to the connection.
     * 
     * @param qos
     */
    void applyQoS(int qos);

}

package com.red5pro.server.stream.mpegts;

/**
 * Base interface for an MPEG-TS connection.
 * 
 * @author Paul Gregoire (paul@infrared5.com)
 */
public interface IMPEGTSConnection {

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

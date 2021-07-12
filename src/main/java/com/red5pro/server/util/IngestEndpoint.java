package com.red5pro.server.util;

import org.red5.server.net.rtmp.RTMPConnection;

import com.red5pro.media.SourceType;

/**
 * Common interface for ingest end-point implementations.
 * 
 * @author Paul Gregoire
 */
public interface IngestEndpoint<T> {

    /**
     * Returns the source type for this ingest end-point.
     * 
     * @return SourceType
     */
    SourceType getType();

    /**
     * Returns the unique identifier (server local).
     * 
     * @return id
     */
    String getId();

    /**
     * Returns unicast or not.
     * 
     * @return true if unicast and false otherwise
     */
    boolean isUnicast();

    /**
     * Returns multicast or not.
     * 
     * @return true if multicast and false otherwise
     */
    boolean isMulticast();

    /**
     * Returns connected or not.
     * 
     * @return true if connected and false otherwise
     */
    boolean isConnected();

    /**
     * Returns internal connection.
     * 
     * @return implementation RTMPConnection
     */
    RTMPConnection getConnection();

}

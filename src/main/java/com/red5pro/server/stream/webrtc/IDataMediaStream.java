package com.red5pro.server.stream.webrtc;

/**
 * Interface for DataChannel implementations.
 * 
 * @author Paul Gregoire
 *
 */
public interface IDataMediaStream {

    /**
     * Close the data channel.
     */
    void close();

    /**
     * Whether or not we're associated.
     * 
     * @return true if associated and false otherwise
     */
    boolean isAssociated();

    /**
     * Returns the DataChannel label.
     * 
     * @return label
     */
    String getLabel();

}

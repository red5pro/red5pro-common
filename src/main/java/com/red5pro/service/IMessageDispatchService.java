package com.red5pro.service;

/**
 * Base interface for message dispatching services.
 * 
 * @author Paul Gregoire
 *
 */
public interface IMessageDispatchService {

    public static final String NAME = "messageService";

    /**
     * Send a message with the given identifier to the destination path.
     * 
     * @param destinationPath
     * @param identifier
     * @param originator
     * @param message
     */
    void sendMessage(String destinationPath, String identifier, String originator, String message);

    /**
     * Send a message with the given identifier to the destination path.
     * 
     * @param destinationPath
     * @param identifier
     * @param originator
     * @param expirationTime time at which this expires
     * @param message
     */
    void sendMessage(String destinationPath, String identifier, String originator, long expirationTime, String message);

    /**
     * Send a binary message with the given identifier to the destination path.
     * 
     * @param destinationPath
     * @param identifier
     * @param originator
     * @param message
     */
    void sendBinaryMessage(String destinationPath, String identifier, String originator, byte[] message);

    /**
     * Send a binary message with the given identifier to the destination path.
     * 
     * @param destinationPath
     * @param identifier
     * @param originator
     * @param expirationTime time at which this expires
     * @param message
     */
    void sendBinaryMessage(String destinationPath, String identifier, String originator, long expirationTime, byte[] message);

}

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
     * @param sender
     * @param destination where we're sending to
     * @param message
     */
    void sendMessage(String sender, String destination, String message);

    /**
     * Send a message with the given identifier to the destination path.
     *
     * @param sender
     * @param destination where we're sending to
     * @param expirationTime time at which this expires
     * @param message
     */
    void sendMessage(String sender, String destination, long expirationTime, String message);

    /**
     * Send a binary message with the given identifier to the destination path.
     *
     * @param sender
     * @param destination where we're sending to
     * @param message
     */
    void sendBinaryMessage(String sender, String destination, byte[] message);

    /**
     * Send a binary message with the given identifier to the destination path.
     *
     * @param sender
     * @param destination where we're sending to
     * @param expirationTime time at which this expires
     * @param message
     */
    void sendBinaryMessage(String sender, String destination, long expirationTime, byte[] message);

    /**
     * Returns the configured expiration time for a message in milliseconds.
     *
     * @return messageExpirationMs
     */
    long getMessageExpirationMs();

}

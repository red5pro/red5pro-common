package com.red5pro.server.datachannel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.red5.server.api.ICastingAttributeStore;

/**
 * Base interface for a DataChannel connection.
 *
 * @author Paul Gregoire
 *
 */
public interface IDataChannelConnection extends ICastingAttributeStore {

    /**
     * Sends text to the client.
     *
     * @param data
     *            string / text data
     * @throws UnsupportedEncodingException
     */
    void send(String data) throws UnsupportedEncodingException;

    /**
     * Sends binary data to the client.
     *
     * @param buf
     */
    void send(byte[] buf);

    /**
     * Sends a ping to the client.
     *
     * @param buf
     * @throws IOException
     * @throws IllegalArgumentException
     */
    void sendPing(byte[] buf) throws IllegalArgumentException, IOException;

    /**
     * Sends a pong to the client.
     *
     * @param buf
     * @throws IOException
     * @throws IllegalArgumentException
     */
    void sendPong(byte[] buf) throws IllegalArgumentException, IOException;

    /**
     * close Connection
     */
    void close();

    /**
     * Returns bytes read.
     *
     * @return bytes read
     */
    long getReadBytes();

    /**
     * Updates the bytes read counter.
     *
     * @param read
     */
    void updateReadBytes(long read);

    /**
     * Returns bytes written.
     *
     * @return bytes written
     */
    long getWrittenBytes();

    /**
     * Returns whether or not the connection is connected.
     *
     * @return connected state
     */
    boolean isConnected();

    /**
     * Sets connected state flag to connected.
     */
    void setConnected();

}

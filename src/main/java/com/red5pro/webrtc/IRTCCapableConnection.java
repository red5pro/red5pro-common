package com.red5pro.webrtc;

import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;

import com.red5pro.server.ConnectionAttributeKey;
import com.red5pro.server.SignalingChannel;
import com.red5pro.server.stream.webrtc.IRTCStreamSession;
import com.red5pro.webrtc.session.ISessionSourceTable;

/**
 * Interface for connections that are WebRTC capable; meaning those that can interact with
 * the MediaStream implementations.
 * 
 * @author Paul Gregoire
 *
 */
public interface IRTCCapableConnection extends IConnection {

    /**
     * Allow a message to accompany a close call.
     *
     * @param closeMessage
     * @param close perform close immediately if true and later if false
     */
    void close(String closeMessage, boolean close);

    IRTCStreamSession getSession();

    String getSessionId();

    SignalingChannel getSignalChannel();

    String getStreamPath();

    ISessionSourceTable getRemotePartyTable();

    boolean isClosed();

    boolean isConnected();

    /**
     * Update the received audio data counter.
     * @deprecated
     * This method is no longer available in Irish Setter 10.0.0 (version TbD) or beyond.
     */
    @Deprecated(since = "10.0.0", forRemoval = true)
    void updateAudioRecvCounter();

    /**
     * Update the received video data counter.
     * @deprecated
     * This method is no longer available in Irish Setter 10.0.0 (version TbD) or beyond.
     */
    @Deprecated(since = "10.0.0", forRemoval = true)
    void updateVideoRecvCounter();

    /**
     * Update the received non-a/v data counter.
     * @deprecated
     * This method is no longer available in Irish Setter 10.0.0 (version TbD) or beyond.
     */
    @Deprecated(since = "10.0.0", forRemoval = true)
    void updateOtherRecvCounter();

    /**
     * Write a string message to the connection output.
     * 
     * @param message
     */
    void writeMessage(String message);

    /**
     * Set an attribute on the connection.
     * 
     * @param key
     * @param value
     */
    void setAttribute(ConnectionAttributeKey key, Object value);

    /**
     * Update the last time at which a packet was received.
     * 
     * @param packetTimeMs
     */
    void updateReceivePacketTime(long packetTimeMs);

    /**
     * Update the last time at which a packet was sent.
     * 
     * @param packetTimeMs
     */
    void updateSendPacketTime(long packetTimeMs);

    int getId();

    IScope getScope();

    /**
     * Returns the user-agents version string.
     *
     * @return userAgentVersion
     */
    String getUAVersion();

    boolean isChrome();

    boolean isEdge();

    boolean isFirefox();

    boolean isSafari();

}

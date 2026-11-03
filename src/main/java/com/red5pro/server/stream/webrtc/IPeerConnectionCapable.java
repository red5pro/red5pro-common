package com.red5pro.server.stream.webrtc;

import org.red5.server.api.IClient;
import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;

import com.red5pro.media.sdp.SDPUserAgent;
import com.red5pro.server.ConnectionAttributeKey;
import com.red5pro.server.SignalingChannel;

import net.minidev.json.JSONObject;

/**
 * Interface to support connections representing PeerConnection features.
 *
 * @author Paul Gregoire
 *
 */
public interface IPeerConnectionCapable extends IConnection {

    /**
     * Returns the scope of this connection.
     *
     * @return scope
     */
    IScope getScope();

    /**
     * Returns the RTC stream session.
     *
     * @return session
     */
    IRTCStreamSession getSession();

    /**
     * Sets the RTC stream session.
     *
     * @param session session
     */
    void setSession(IRTCStreamSession session);

    /**
     * Returns the user agent string of the remote peer.
     *
     * @return user agent
     */
    String getUserAgent();

    /**
     * Returns the user agent as an enum.
     *
     * @return user agent enum
     */
    SDPUserAgent getUserAgentEnum();

    /**
     * Returns the signaling channel.
     *
     * @return signaling channel
     */
    SignalingChannel getSignalChannel();

    /**
     * Sets the signaling channel.
     *
     * @param signalChannel signaling channel
     */
    void setSignalChannel(Object signalChannel);

    /**
     * Returns the client for this connection.
     *
     * @return client
     */
    IClient getClient();

    /**
     * Tests whether the connection has the given attribute.
     *
     * @param dataChannel attribute key
     * @return true if present
     */
    boolean hasAttribute(ConnectionAttributeKey dataChannel);

    /**
     * Returns the value of the given attribute.
     *
     * @param key attribute key
     * @return value or null
     */
    Object getAttribute(ConnectionAttributeKey key);

    /**
     * Sets the value of the given attribute.
     *
     * @param key attribute key
     * @param value attribute value
     */
    void setAttribute(ConnectionAttributeKey key, Object value);

    /**
     * Returns the given attribute as a string.
     *
     * @param rtcStyle attribute key
     * @return string value or null
     */
    String getStringAttribute(ConnectionAttributeKey rtcStyle);

    /**
     * Whether the stream is being recorded.
     *
     * @return true if recording
     */
    boolean isRecord();

    /**
     * Applies a mute request to the connection.
     *
     * @param jsonObject mute request details
     */
    void mute(JSONObject jsonObject);

    /**
     * Sets whether to notify when the stream is unpublished.
     *
     * @param notify true to notify
     */
    void notifyUnpublished(boolean notify);

    /**
     * Writes a message to the remote peer.
     *
     * @param format message text
     */
    void writeMessage(String format);

}

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

    IScope getScope();

    IRTCStreamSession getSession();

    void setSession(IRTCStreamSession session);

    String getUserAgent();

    SDPUserAgent getUserAgentEnum();

    SignalingChannel getSignalChannel();

    void setSignalChannel(Object signalChannel);

    IClient getClient();

    boolean hasAttribute(ConnectionAttributeKey dataChannel);

    Object getAttribute(ConnectionAttributeKey key);

    void setAttribute(ConnectionAttributeKey key, Object value);

    String getStringAttribute(ConnectionAttributeKey rtcStyle);

    boolean isRecord();

    void mute(JSONObject jsonObject);

    void notifyUnpublished(boolean notify);

    void writeMessage(String format);

    void setLocalSDP(String sdp);

    String getLocalSDP();

    void sendRemb(int ssrc, int bitrate);

    @Deprecated
    boolean isEdge(); // remove traces of this once we stop supporting Edge

}

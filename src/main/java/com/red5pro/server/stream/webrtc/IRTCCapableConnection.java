//
// Copyright © 2023 Infrared5, Inc. All rights reserved.
//
// The accompanying code comprising examples for use solely in conjunction with Red5 Pro (the "Example Code")
// is  licensed  to  you  by  Infrared5  Inc.  in  consideration  of  your  agreement  to  the  following
// license terms  and  conditions.  Access,  use,  modification,  or  redistribution  of  the  accompanying
// code  constitutes your acceptance of the following license terms and conditions.
//
// Permission is hereby granted, free of charge, to you to use the Example Code and associated documentation
// files (collectively, the "Software") without restriction, including without limitation the rights to use,
// copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the following conditions:
//
// The Software shall be used solely in conjunction with Red5 Pro. Red5 Pro is licensed under a separate end
// user  license  agreement  (the  "EULA"),  which  must  be  executed  with  Infrared5,  Inc.
// An  example  of  the EULA can be found on our website at: https://account.red5pro.com/assets/LICENSE.txt.
//
// The above copyright notice and this license shall be included in all copies or portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,  INCLUDING  BUT
// NOT  LIMITED  TO  THE  WARRANTIES  OF  MERCHANTABILITY, FITNESS  FOR  A  PARTICULAR  PURPOSE  AND
// NONINFRINGEMENT.   IN  NO  EVENT  SHALL INFRARED5, INC. BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN  AN  ACTION  OF  CONTRACT,  TORT  OR  OTHERWISE,  ARISING  FROM,  OUT  OF  OR  IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package com.red5pro.server.stream.webrtc;

import org.red5.server.api.IConnection;
import org.red5.server.api.scope.IScope;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.red5pro.io.WireMessage;
import com.red5pro.media.sdp.SDPUserAgent;
import com.red5pro.server.ConnectionAttributeKey;
import com.red5pro.server.SignalingChannel;

import net.minidev.json.JSONObject;

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

    IScope getScope();

    IRTCStreamSession getSession();

    String getSessionId();

    SignalingChannel getSignalChannel();

    String getStreamPath();

    ISessionSourceTable getRemotePartyTable();

    /**
     * Allows tapping the session table.
     *
     * @param ssrc
     */
    void tapSession(int ssrc);

    boolean isClosed();

    boolean isConnected();

    /**
     * Write a string message to the connection output.
     *
     * @param message
     */
    void writeMessage(String message);

    /**
     * Write a wire message to the connection output.
     *
     * @param message
     */
    default void writeMessage(WireMessage message) {
        if (!message.isBinary()) {
            writeMessage(message.getStringMessage());
        }
    }

    /**
     * Set an attribute on the connection.
     *
     * @param key
     * @param value
     */
    void setAttribute(ConnectionAttributeKey key, Object value);

    /**
     * Returns an attribute from the connection as a string.
     *
     * @param key
     * @return attribute value
     */
    String getStringAttribute(ConnectionAttributeKey key);

    boolean isRecord();

    void mute(JSONObject jsonObject);

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

    /**
     * Returns the user-agent string.
     *
     * @return userAgent
     */
    String getUserAgent();

    /**
     * Returns the user-agent enum.
     *
     * @return userAgentEnum
     */
    SDPUserAgent getUserAgentEnum();

    void setLocalSDP(String sdp);

    String getLocalSDP();

    void sendRemb(int ssrc, int bitrate);

    // TODO(paul) add this to the interface next release
    //boolean isLocalNetwork();

}

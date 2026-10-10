package com.red5pro.server;

/**
 * Enum of connection attribute keys to prevent fat-finger errors.
 */
public enum ConnectionAttributeKey {

    /** Connection attribute key: CONNECTION_TAG. */
    CONNECTION_TAG("connection"),
    /** Connection attribute key: SIGNAL_CHANNEL. */
    SIGNAL_CHANNEL("signal.channel"),
    /** Connection attribute key: DATA_CHANNEL. */
    DATA_CHANNEL("data.channel"),
    /** Connection attribute key: SO_ADAPTER. */
    SO_ADAPTER("sharedobject.adapter"),
    /** Connection attribute key: STREAM_NAME. */
    STREAM_NAME("streamName"),
    /** Connection attribute key: LOCATION. */
    LOCATION("location"),
    /** Connection attribute key: LOCAL_ID. */
    LOCAL_ID("localId"),
    /** Connection attribute key: TMSG. */
    TMSG("tmsg"),
    /** Connection attribute key: BROADCAST_ID. */
    BROADCAST_ID("broadcastId"),
    /** Connection attribute key: STREAM_ID. */
    STREAM_ID("streamId"),
    /** Connection attribute key: SDP. */
    SDP("sdp"),
    /** Connection attribute key: URI. */
    URI("uri"),
    /** Connection attribute key: RECORDING_TYPE. */
    RECORDING_TYPE("record.type"),
    /** Connection attribute key: OFFER. */
    OFFER("offer"),
    /** Connection attribute key: TRICKLE_COMPLETE. */
    TRICKLE_COMPLETE("trickleComplete"),
    /** Connection attribute key: P2P_SESSION. */
    P2P_SESSION("p2p-session"),
    /** Connection attribute key: PEER_ID. */
    PEER_ID("peerId"),
    /** Connection attribute key: RTC_STYLE. */
    RTC_STYLE("rtc.style"),
    /** Connection attribute key: SCOPE. */
    SCOPE("scope"),
    /** Connection attribute key: MULTICAST. */
    MULTICAST("multicast"),
    /** Connection attribute key: GROUP_NAME. */
    GROUP_NAME("groupName"),
    /** Connection attribute key: GROUP_PORT. */
    GROUP_PORT("groupPort"),
    /** Connection attribute key: AUDIO_ENCODING. */
    AUDIO_ENCODING("audioEncoding"),
    /** Connection attribute key: VIDEO_ENCODING. */
    VIDEO_ENCODING("videoEncoding"),
    /** Connection attribute key: AMF_AUDIO_ENCODING. */
    AMF_AUDIO_ENCODING("amf-audio"),
    /** Connection attribute key: AMF_VIDEO_ENCODING. */
    AMF_VIDEO_ENCODING("amf-video"),
    /** Connection attribute key: CONFERENCE. */
    CONFERENCE("conference"),
    /** Connection attribute key: GROUP. */
    GROUP("group"),
    /** Connection attribute key: RESTREAMER. */
    RESTREAMER("restreamer"),
    /** Connection attribute key: PERMISSIONS. */
    PERMISSIONS("permissions");

    /** String key stored in the connection attribute map. */
    public final String value;

    ConnectionAttributeKey(String value) {
        this.value = value;
    }

    /** Finds the key with the given stored value.
     * @param value stored attribute value
     * @return matching key, or null if none matches
     */
    public static Enum<ConnectionAttributeKey> findByValue(String value) {
        for (ConnectionAttributeKey key : ConnectionAttributeKey.values()) {
            if (key.value.contentEquals(value)) {
                return key;
            }
        }
        return null;
    }

}

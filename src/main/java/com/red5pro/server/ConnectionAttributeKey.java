package com.red5pro.server;

/**
 * Enum of connection attribute keys to prevent fat-finger errors.
 */
public enum ConnectionAttributeKey {

    CONNECTION_TAG("connection"), // connection instance
    SIGNAL_CHANNEL("signal.channel"), // signal channel instance
    DATA_CHANNEL("data.channel"), // data channel instance
    SO_ADAPTER("sharedobject.adapter"), STREAM_NAME("streamName"), // associated stream name
    LOCATION("location"), // location, uri identifier
    LOCAL_ID("localId"), // local id
    TMSG("tmsg"), // tmsg
    BROADCAST_ID("broadcastId"), // broadcast id
    STREAM_ID("streamId"), // stream id
    SDP("sdp"), // generic sdp key
    URI("uri"), // generic uri
    RECORDING_TYPE("record.type"), // recording type
    OFFER("offer"), // offer sdp
    TRICKLE_COMPLETE("trickleComplete"), // flag to indicate if trickle is complete or not
    P2P_SESSION("p2p-session"), // p2p session
    PEER_ID("peerId"), // peer id
    RTC_STYLE("rtc.style"), // rtc style (no longer used)
    SCOPE("scope"), // scope
    MULTICAST("multicast"), GROUP_NAME("groupName"), // multicast group name
    GROUP_PORT("groupPort"), // multicast group port
    AUDIO_ENCODING("audioEncoding"), VIDEO_ENCODING("videoEncoding"), AMF_AUDIO_ENCODING("amf-audio"), AMF_VIDEO_ENCODING("amf-video"), CONFERENCE("conference"), // conference
    GROUP("group"), // group, non-multicast
    RESTEAMER("restreamer"), // restreamer
    PERMISSIONS("permissions"); // permissions (currently used in publishing)

    public final String value;

    ConnectionAttributeKey(String value) {
        this.value = value;
    }

    public static Enum<ConnectionAttributeKey> findByValue(String value) {
        for (ConnectionAttributeKey key : ConnectionAttributeKey.values()) {
            if (key.value.contentEquals(value)) {
                return key;
            }
        }
        return null;
    }

}

package com.red5pro.server;

/**
 * Enum of connection attribute keys to prevent fat-finger errors.
 */
public enum ConnectionAttributeKey {

    CONNECTION_TAG("connection"), SIGNAL_CHANNEL("signal.channel"), SO_ADAPTER("sharedobject.adapter"), STREAM_NAME("streamName"), RECORDING_TYPE("record.type"), OFFER("offer"), TRICKLE_COMPLETE("trickleComplete"), P2P_SESSION("p2p-session"), PEER_ID("peerId"), RTC_STYLE("rtc.style"), SCOPE("scope"), DATA_CHANNEL("data.channel"), MULTICAST("multicast"), GROUP_NAME("groupName"), GROUP_PORT(
            "groupPort"), AUDIO_ENCODING("audioEncoding"), VIDEO_ENCODING("videoEncoding"), AMF_AUDIO_ENCODING("amf-audio"), AMF_VIDEO_ENCODING("amf-video");

    public final String value;

    ConnectionAttributeKey(String value) {
        this.value = value;
    }

}
package com.red5pro.media;

/**
 * Identifies a source type.
 *
 * @author Paul Gregoire
 */
public enum SourceType {

    /** Real Time Messaging Protocol source */
    RTMP,
    /** Real Time Streaming Protocol source */
    RTSP,
    /** Real-time Transport Protocol source */
    RTP,
    /** WebRTC (real-time communication) source */
    RTC,
    /** HTTP Live Streaming source */
    HLS,
    /** MPEG transport stream source */
    MPEGTS,
    /** Secure Reliable Transport source */
    SRT,
    /** Media over QUIC source */
    MOQ;

    /** Name of the attribute used to store the source type */
    public final static String ATTRIBUTE = "sourceType";

    /** Cached copy of {@link #values()} to avoid repeated array allocation */
    public static SourceType[] cachedValues = SourceType.values();

}

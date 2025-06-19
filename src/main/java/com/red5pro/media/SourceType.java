package com.red5pro.media;

/**
 * Identifies a source type.
 *
 * @author Paul Gregoire
 */
public enum SourceType {

    RTMP, RTSP, RTP, RTC, HLS, MPEGTS, SRT;

    public final static String ATTRIBUTE = "sourceType";

    public static SourceType[] cachedValues = SourceType.values();

}

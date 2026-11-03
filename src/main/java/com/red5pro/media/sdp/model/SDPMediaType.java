package com.red5pro.media.sdp.model;

import java.util.EnumSet;

/** Media types that may appear in an SDP media description. */
public enum SDPMediaType {

    /** Audio media. */
    audio,
    /** Video media. */
    video,
    /** Text media. */
    text,
    /** Application data media. */
    application,
    /** Message media. */
    message;

    /** Set containing the audio and video media types. */
    public static EnumSet<SDPMediaType> audioVideo = EnumSet.of(audio, video);

    /**
     * Finds the media type matching the given name, ignoring case.
     * @param type the media type name, may be null
     * @return the matching type, or null if none matches
     */
    public static SDPMediaType fromString(String type) {
        if (type != null) {
            for (SDPMediaType t : SDPMediaType.values()) {
                if (type.equalsIgnoreCase(t.toString())) {
                    return t;
                }
            }
        }
        return null;
    }

}

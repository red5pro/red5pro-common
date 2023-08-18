package com.red5pro.media.sdp.model;

import java.util.EnumSet;

public enum SDPMediaType {

    audio, video, text, application, message;

    public static EnumSet<SDPMediaType> audioVideo = EnumSet.of(audio, video);

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

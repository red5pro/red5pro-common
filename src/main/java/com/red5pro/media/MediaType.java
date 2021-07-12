package com.red5pro.media;

/**
 * The <pre>MediaType</pre> enumeration contains a list of media types currently
 * known to and handled by the <pre>MediaService</pre>.
 *
 * @author Emil Ivov
 */
public enum MediaType {

    /**
     * Represents an AUDIO media type.
     */
    AUDIO("audio"),

    /**
     * Represents a VIDEO media type.
     */
    VIDEO("video"),

    /**
     * Represents a DATA media type.
     */
    DATA("data"),

    /**
     * Undefined type.
     */
    UNDEFINED("undefined");

    /**
     * The name of this <pre>MediaType</pre>.
     */
    private final String mediaTypeName;

    /**
     * Creates a <pre>MediaType</pre> instance with the specified name.
     *
     * @param mediaTypeName
     *            the name of the <pre>MediaType</pre> we'd like to create.
     */
    private MediaType(String mediaTypeName) {
        this.mediaTypeName = mediaTypeName;
    }

    /**
     * Returns the name of this MediaType (e.g. "audio" or "video"). The name
     * returned by this method is meant for use by session description mechanisms
     * such as SIP/SDP or XMPP/Jingle.
     *
     * @return the name of this MediaType (e.g. "audio" or "video").
     */
    @Override
    public String toString() {
        return mediaTypeName;
    }

}

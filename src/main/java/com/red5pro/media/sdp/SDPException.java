package com.red5pro.media.sdp;

/**
 * Thrown when an SDP cannot be parsed or created.
 */
public class SDPException extends Exception {

    private static final long serialVersionUID = -4569031041675667702L;

    /** Creates an exception with no message. */
    public SDPException() {
        super();
    }

    /**
     * Creates an exception with a message.
     *
     * @param message detail message
     */
    public SDPException(String message) {
        super(message);
    }

}

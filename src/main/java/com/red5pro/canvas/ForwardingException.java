package com.red5pro.canvas;

/**
 * Exception thrown when stream forwarding fails.
 *
 * @author Nate Roe
 */
public class ForwardingException extends Exception {
    private static final long serialVersionUID = 1L;

    public ForwardingException() {
        super();
    }

    public ForwardingException(String message) {
        super(message);
    }

    public ForwardingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ForwardingException(Throwable cause) {
        super(cause);
    }

    public ForwardingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

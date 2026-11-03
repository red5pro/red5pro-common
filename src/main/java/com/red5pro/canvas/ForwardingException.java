package com.red5pro.canvas;

/**
 * Exception thrown when stream forwarding fails.
 *
 * @author Nate Roe
 */
public class ForwardingException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with no message or cause.
     */
    public ForwardingException() {
        super();
    }

    /**
     * Creates an exception with the given detail message.
     *
     * @param message the detail message
     */
    public ForwardingException(String message) {
        super(message);
    }

    /**
     * Creates an exception with the given detail message and cause.
     *
     * @param message the detail message
     * @param cause the underlying cause
     */
    public ForwardingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an exception wrapping the given cause.
     *
     * @param cause the underlying cause
     */
    public ForwardingException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates an exception with full control over suppression and stack trace behavior.
     *
     * @param message the detail message
     * @param cause the underlying cause
     * @param enableSuppression whether suppression is enabled
     * @param writableStackTrace whether the stack trace is writable
     */
    public ForwardingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

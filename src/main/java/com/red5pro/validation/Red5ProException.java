package com.red5pro.validation;

/**
 * A general exception that came from this Conference API somewhere.
 * The root class of all other Conference API exceptions.
 *
 * @author Nate Roe
 */
public abstract class Red5ProException extends Exception {
    private static final long serialVersionUID = 1L;

    /** Creates an exception without a message or cause. */
    public Red5ProException() {
        super();
    }

    /**
     * Creates an exception with full control over suppression and stack trace behavior.
     *
     * @param message detail message
     * @param cause underlying cause
     * @param enableSuppression whether suppression is enabled
     * @param writableStackTrace whether the stack trace should be writable
     */
    public Red5ProException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Creates an exception with a message and cause.
     *
     * @param message detail message
     * @param cause underlying cause
     */
    public Red5ProException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an exception with a message.
     *
     * @param message detail message
     */
    public Red5ProException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a cause.
     *
     * @param cause underlying cause
     */
    public Red5ProException(Throwable cause) {
        super(cause);
    }
}

package com.red5pro.validation;

/**
 * A general exception that came from this Conference API somewhere.
 * The root class of all other Conference API exceptions.
 *
 * @author Nate Roe
 */
public abstract class Red5ProException extends Exception {
    private static final long serialVersionUID = 1L;

    public Red5ProException() {
        super();
    }

    public Red5ProException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public Red5ProException(String message, Throwable cause) {
        super(message, cause);
    }

    public Red5ProException(String message) {
        super(message);
    }

    public Red5ProException(Throwable cause) {
        super(cause);
    }
}

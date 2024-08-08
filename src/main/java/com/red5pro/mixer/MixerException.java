package com.red5pro.mixer;

/**
 * A classified Exception for server errors.
 *
 * @author Nate Roe
 */
public class MixerException extends Exception {
    private static final long serialVersionUID = 1L;

    public MixerException() {
        super();
    }

    public MixerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MixerException(String message, Throwable cause) {
        super(message, cause);
    }

    public MixerException(String message) {
        super(message);
    }

    public MixerException(Throwable cause) {
        super(cause);
    }
}

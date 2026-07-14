package com.red5pro.validation;

/**
 * @author Nate Roe
 */
public class ValidationException extends Red5ProException {
    private static final long serialVersionUID = 1L;

    public ValidationException() {
        super();
    }

    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }
}

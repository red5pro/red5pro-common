package com.red5pro.validation;

/** Reports a failed validation check.
 *
 * @author Nate Roe
 */
public class ValidationException extends Red5ProException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with no message or cause.
     */
    public ValidationException() {
        super();
    }

    /**
     * Creates an exception with full control over suppression and stack trace.
     *
     * @param message
     *            error message
     * @param cause
     *            underlying cause
     * @param enableSuppression
     *            whether suppression is enabled
     * @param writableStackTrace
     *            whether the stack trace is writable
     */
    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Creates an exception with a message and cause.
     *
     * @param message
     *            error message
     * @param cause
     *            underlying cause
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates an exception with a message.
     *
     * @param message
     *            error message
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a cause.
     *
     * @param cause
     *            underlying cause
     */
    public ValidationException(Throwable cause) {
        super(cause);
    }
}

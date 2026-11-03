package com.red5pro.validation;

/**
 * Simple validation utility to assert and throw ValidationException
 *
 * @author Nate Roe
 */
public class Validator {

    /** Creates a new Validator; all functionality is static. */
    public Validator() {
    }

    /**
     * Assert IS null: Throw ValidationException with message if notNullObject is not null.
     *
     * @param notNullObject value that must be null
     * @param message exception message
     * @throws ValidationException if the assertion fails
     */
    public static void isNull(Object notNullObject, String message) throws ValidationException {
        if (notNullObject != null) {
            throw new ValidationException(message);
        }
    }

    /**
     * Assert IS null: Throw ValidationException with String.format(format, arguments) if notNullObject is not null.
     *
     * @param notNullObject object to check
     * @param format message format
     * @param arguments format arguments
     * @throws ValidationException if the assertion fails
     */
    public static void isNull(Object notNullObject, String format, Object... arguments) throws ValidationException {
        if (notNullObject != null) {
            throw new ValidationException(String.format(format, arguments));
        }
    }

    /**
     * Assert NOT null: Throw ValidationException with message if notNullObject is null.
     *
     * @param notNullObject value that must not be null
     * @param message exception message
     * @throws ValidationException if the assertion fails
     */
    public static void notNull(Object notNullObject, String message) throws ValidationException {
        if (notNullObject == null) {
            throw new ValidationException(message);
        }
    }

    /**
     * Assert NOT null: Throw ValidationException with String.format(format, arguments) if notNullObject is null.
     *
     * @param notNullObject object to check
     * @param format message format
     * @param arguments format arguments
     * @throws ValidationException if the assertion fails
     */
    public static void notNull(Object notNullObject, String format, Object... arguments) throws ValidationException {
        if (notNullObject == null) {
            throw new ValidationException(String.format(format, arguments));
        }
    }

    /**
     * Assert is true: Throw ValidationException with message if not condition.
     *
     * @param condition condition that must be true
     * @param message exception message
     * @throws ValidationException if the assertion fails
     */
    public static void isTrue(boolean condition, String message) throws ValidationException {
        if (!condition) {
            throw new ValidationException(message);
        }
    }

    /**
     * Assert is true: Throw ValidationException with String.format(format, arguments) if not condition.
     *
     * @param condition condition that must be true
     * @param format message format
     * @param arguments format arguments
     * @throws ValidationException if the assertion fails
     */
    public static void isTrue(boolean condition, String format, Object... arguments) throws ValidationException {
        if (!condition) {
            throw new ValidationException(String.format(format, arguments));
        }
    }
}

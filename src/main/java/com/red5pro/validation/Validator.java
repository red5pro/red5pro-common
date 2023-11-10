package com.red5pro.validation;

/**
 * Simple validation utility to assert and throw ValidationException
 *
 * @author Nate Roe
 */
public class Validator {
    /**
     * Assert IS null: Throw ValidationException with message if notNullObject is not null.
     *
     * @param notNullObject
     * @param message
     * @throws ValidationException
     */
    public static void isNull(Object notNullObject, String message) throws ValidationException {
        if (notNullObject != null) {
            throw new ValidationException(message);
        }
    }

    /**
     * Assert IS null: Throw ValidationException with String.format(format, arguments) if notNullObject is not null.
     *
     * @param notNullObject
     * @param format
     * @param arguments
     * @throws ValidationException
     */
    public static void isNull(Object notNullObject, String format, Object... arguments) throws ValidationException {
        if (notNullObject != null) {
            throw new ValidationException(String.format(format, arguments));
        }
    }

    /**
     * Assert NOT null: Throw ValidationException with message if notNullObject is null.
     *
     * @param notNullObject
     * @param message
     * @throws ValidationException
     */
    public static void notNull(Object notNullObject, String message) throws ValidationException {
        if (notNullObject == null) {
            throw new ValidationException(message);
        }
    }

    /**
     * Assert NOT null: Throw ValidationException with String.format(format, arguments) if notNullObject is null.
     *
     * @param notNullObject
     * @param format
     * @param arguments
     * @throws ValidationException
     */
    public static void notNull(Object notNullObject, String format, Object... arguments) throws ValidationException {
        if (notNullObject == null) {
            throw new ValidationException(String.format(format, arguments));
        }
    }

    /**
     * Assert is true: Throw ValidationException with message if not condition.
     *
     * @param notNullObject
     * @param message
     * @throws ValidationException
     */
    public static void isTrue(boolean condition, String message) throws ValidationException {
        if (!condition) {
            throw new ValidationException(message);
        }
    }

    /**
     * Assert is true: Throw ValidationException with String.format(format, arguments) if not condition.
     *
     * @param notNullObject
     * @param format
     * @param arguments
     * @throws ValidationException
     */
    public static void isTrue(boolean condition, String format, Object... arguments) throws ValidationException {
        if (!condition) {
            throw new ValidationException(String.format(format, arguments));
        }
    }
}

package com.tech.test.exception;

/**
 * Exception thrown when an operation is not allowed or invalid state is detected. HTTP Status: 409
 * Conflict / 400 Bad Request
 */
public class BusinessLogicException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BusinessLogicException(String message) {
        super(message);
    }

    public BusinessLogicException(String message, Throwable cause) {
        super(message, cause);
    }
}

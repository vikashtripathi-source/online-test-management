package com.tech.test.exception;

/** Exception thrown when invalid data is provided in request. HTTP Status: 400 Bad Request */
public class InvalidDataException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}

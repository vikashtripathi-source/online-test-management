package com.tech.test.exception;

/**
 * Exception thrown when an address operation fails. HTTP Status: 400 Bad Request / 404 Not Found
 */
public class AddressException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AddressException(String message) {
        super(message);
    }

    public AddressException(String message, Throwable cause) {
        super(message, cause);
    }
}

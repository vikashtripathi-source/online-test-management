package com.tech.test.exception;

/**
 * Exception thrown when an order operation fails.
 * HTTP Status: 400 Bad Request / 404 Not Found
 */
public class OrderException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public OrderException(String message) {
        super(message);
    }
    
    public OrderException(String message, Throwable cause) {
        super(message, cause);
    }
}


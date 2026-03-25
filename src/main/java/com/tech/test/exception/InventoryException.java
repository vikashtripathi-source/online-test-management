package com.tech.test.exception;

/**
 * Exception thrown when an inventory operation fails.
 * HTTP Status: 400 Bad Request / 404 Not Found
 */
public class InventoryException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public InventoryException(String message) {
        super(message);
    }
    
    public InventoryException(String message, Throwable cause) {
        super(message, cause);
    }
}


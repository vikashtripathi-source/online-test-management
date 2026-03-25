package com.tech.test.exception;

/**
 * Exception thrown when student exam record operation fails.
 * HTTP Status: 400 Bad Request / 404 Not Found
 */
public class StudentRecordException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public StudentRecordException(String message) {
        super(message);
    }
    
    public StudentRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}


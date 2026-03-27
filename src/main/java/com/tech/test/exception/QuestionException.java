package com.tech.test.exception;

/**
 * Exception thrown when a question operation fails. HTTP Status: 400 Bad Request / 404 Not Found
 */
public class QuestionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public QuestionException(String message) {
        super(message);
    }

    public QuestionException(String message, Throwable cause) {
        super(message, cause);
    }
}

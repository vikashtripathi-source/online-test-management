package com.tech.test.exception;

/**
 * Exception thrown when Kafka message processing fails.
 * HTTP Status: 500 Internal Server Error
 */
public class KafkaException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public KafkaException(String message) {
        super(message);
    }
    
    public KafkaException(String message, Throwable cause) {
        super(message, cause);
    }
}


package com.tech.test.exception;

public class KafkaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public KafkaException(String message) {
        super(message);
    }

    public KafkaException(String message, Throwable cause) {
        super(message, cause);
    }
}

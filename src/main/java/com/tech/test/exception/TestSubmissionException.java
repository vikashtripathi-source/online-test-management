package com.tech.test.exception;

public class TestSubmissionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TestSubmissionException(String message) {
        super(message);
    }

    public TestSubmissionException(String message, Throwable cause) {
        super(message, cause);
    }
}

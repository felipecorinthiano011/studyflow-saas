package com.studyflow.backend.shared.exception;

/**
 * Thrown when a requested resource does not exist in the database.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}


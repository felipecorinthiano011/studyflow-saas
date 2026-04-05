package com.studyflow.backend.shared.exception;

/**
 * Thrown when a user attempts to perform an operation on a resource they do not own.
 * Named to avoid collision with Spring Security's AccessDeniedException.
 */
public class DomainAccessDeniedException extends RuntimeException {

    public DomainAccessDeniedException(String message) {
        super(message);
    }
}


package com.studyflow.backend.domain.user.entity;

/**
 * Application roles used for method-level security (@PreAuthorize).
 * Maps to Spring Security's "ROLE_" prefix convention.
 */
public enum Role {
    USER,
    ADMIN
}

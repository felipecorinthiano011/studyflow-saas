package com.studyflow.backend.common.helper;

import com.studyflow.backend.domain.user.entity.User;
import com.studyflow.backend.domain.user.repository.UserRepository;
import com.studyflow.backend.shared.constant.ErrorMessages;
import com.studyflow.backend.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Shared helper that resolves the authenticated {@link User} entity from a Spring Security
 * {@link Authentication} object.
 * <p>
 * Extracted from controller classes where the same three-line pattern was repeated.
 */
@Component
@RequiredArgsConstructor
public class AuthenticationHelper {

    private final UserRepository userRepository;

    public User getAuthenticatedUser(Authentication authentication) {
        String email = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));
    }
}


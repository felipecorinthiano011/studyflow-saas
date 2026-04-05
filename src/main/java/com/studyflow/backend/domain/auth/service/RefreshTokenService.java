package com.studyflow.backend.domain.auth.service;

import com.studyflow.backend.domain.auth.entity.RefreshToken;
import com.studyflow.backend.domain.auth.repository.RefreshTokenRepository;
import com.studyflow.backend.shared.constant.ErrorMessages;
import com.studyflow.backend.shared.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${refresh.token.expiration-days:7}")
    private int expirationDays;

    /**
     * Creates a new refresh token for the given user, revoking any existing ones
     * (single-session model).
     */
    public RefreshToken createRefreshToken(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
        return buildAndSave(userId);
    }

    /**
     * Validates the given token string, deletes it and issues a new one (rotation).
     * Throws {@link AuthenticationException} if the token is not found or expired.
     */
    public RefreshToken verifyAndRotate(String tokenStr) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new AuthenticationException(ErrorMessages.TOKEN_INVALID));

        if (token.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.deleteById(token.getId());
            throw new AuthenticationException(ErrorMessages.TOKEN_EXPIRED);
        }

        Long userId = token.getUserId();
        refreshTokenRepository.deleteById(token.getId());
        return buildAndSave(userId);
    }

    /**
     * Revokes all refresh tokens belonging to the user (logout).
     * If no tokens exist the call is a no-op.
     */
    public void revokeByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    /**
     * Revokes a specific refresh token by its value.
     * If the token does not exist the call is a no-op (idempotent logout).
     */
    public void revokeByToken(String tokenStr) {
        refreshTokenRepository.findByToken(tokenStr)
                .ifPresent(t -> refreshTokenRepository.deleteById(t.getId()));
    }

    private RefreshToken buildAndSave(Long userId) {
        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plus(expirationDays, ChronoUnit.DAYS))
                .createdAt(Instant.now())
                .build();
        return refreshTokenRepository.save(token);
    }
}

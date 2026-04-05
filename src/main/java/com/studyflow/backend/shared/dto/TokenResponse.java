package com.studyflow.backend.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    /** Access token (short-lived JWT). Kept as "token" for backward compatibility. */
    private String token;

    /** Refresh token (long-lived opaque token). Use to obtain a new access token. */
    private String refreshToken;

    @Builder.Default
    private String tokenType = "Bearer";
}

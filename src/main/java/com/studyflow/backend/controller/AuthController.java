package com.studyflow.backend.controller;

import com.studyflow.backend.domain.auth.entity.RefreshToken;
import com.studyflow.backend.domain.auth.service.RefreshTokenService;
import com.studyflow.backend.shared.dto.LoginRequest;
import com.studyflow.backend.shared.dto.RefreshTokenRequest;
import com.studyflow.backend.shared.dto.TokenResponse;
import com.studyflow.backend.domain.user.entity.User;
import com.studyflow.backend.shared.exception.AuthenticationException;
import com.studyflow.backend.domain.user.repository.UserRepository;
import com.studyflow.backend.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Autenticação e geração de token JWT")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    /** Authenticates the user and returns a JWT access token plus a refresh token. */
    @Operation(
        summary = "Login",
        description = "Autentica o usuário e retorna um access token JWT (1h) e um refresh token (7d).",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
                {
                  "email": "usuario@email.com",
                  "password": "suasenha"
                }
                """))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                content = @Content(schema = @Schema(example = """
                    {
                      "token": "eyJhbGciOiJIUzI1NiJ9...",
                      "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
                      "tokenType": "Bearer"
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
        }
    )
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {

        // Use the same generic message for both "user not found" and "wrong password"
        // to prevent user-enumeration attacks (OWASP A07:2021).
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Email ou senha incorretos"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Email ou senha incorretos");
        }

        String accessToken = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return TokenResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    /** Rotates the refresh token and issues a new access token. */
    @Operation(
        summary = "Renovar access token",
        description = "Valida o refresh token, o rotaciona e emite um novo access token.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
                {
                  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
                }
                """))
        ),
        responses = {
            @ApiResponse(responseCode = "200", description = "Tokens renovados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado")
        }
    )
    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshToken newRefreshToken = refreshTokenService.verifyAndRotate(request.getRefreshToken());

        User user = userRepository.findById(newRefreshToken.getUserId())
                .orElseThrow(() -> new AuthenticationException("Usuário não encontrado"));

        String accessToken = jwtService.generateToken(user.getEmail());

        return TokenResponse.builder()
                .token(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .build();
    }

    /** Revokes the given refresh token, effectively logging the user out. */
    @Operation(
        summary = "Logout",
        description = "Invalida o refresh token do usuário.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
                {
                  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
                }
                """))
        ),
        responses = {
            @ApiResponse(responseCode = "204", description = "Logout realizado com sucesso")
        }
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenService.revokeByToken(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}

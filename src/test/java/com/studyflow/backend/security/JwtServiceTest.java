package com.studyflow.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private String testEmail;
    private User testUser;

    @BeforeEach
    void setUp() {
        testEmail = "test@email.com";
        testUser = new User(testEmail, "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void shouldGenerateValidToken() {
        String token = jwtService.generateToken(testEmail);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void shouldExtractEmailFromToken() {
        String token = jwtService.generateToken(testEmail);
        String extractedEmail = jwtService.extractEmail(token);
        assertEquals(testEmail, extractedEmail);
    }

    @Test
    void shouldValidateTokenCorrectly() {
        String token = jwtService.generateToken(testEmail);
        boolean isValid = jwtService.isTokenValid(token, testUser);
        assertTrue(isValid);
    }

    @Test
    void shouldRejectTokenWithWrongEmail() {
        String token = jwtService.generateToken(testEmail);
        User wrongUser = new User("wrong@email.com", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));
        boolean isValid = jwtService.isTokenValid(token, wrongUser);
        assertFalse(isValid);
    }
}


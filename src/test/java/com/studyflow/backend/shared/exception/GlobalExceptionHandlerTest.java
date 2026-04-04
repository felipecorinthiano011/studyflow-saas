package com.studyflow.backend.shared.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void shouldReturnBadRequestForValidationErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("userRequestDTO", "email", "Email inválido");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertNotNull(response.getBody().get("timestamp"));

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertNotNull(errors);
        assertEquals("Email inválido", errors.get("email"));
    }

    @Test
    void shouldReturnBadRequestWithAllFieldErrorsForMultipleValidationErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError emailError = new FieldError("userRequestDTO", "email", "Email inválido");
        FieldError nameError = new FieldError("userRequestDTO", "name", "Nome é obrigatório");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(emailError, nameError));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertEquals(2, errors.size());
        assertEquals("Email inválido", errors.get("email"));
        assertEquals("Nome é obrigatório", errors.get("name"));
    }

    @Test
    void shouldReturnConflictForDataIntegrityViolation() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Duplicate entry");

        ResponseEntity<Map<String, Object>> response = handler.handleDataIntegrity(ex);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().get("status"));
        assertEquals("Registro já existente (possível email duplicado)", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void shouldReturnUnauthorizedForAuthenticationException() {
        AuthenticationException ex = new AuthenticationException("Email ou senha incorretos");

        ResponseEntity<Map<String, Object>> response = handler.handleAuthentication(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(401, response.getBody().get("status"));
        assertEquals("Email ou senha incorretos", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    void shouldReturnUnauthorizedWithCustomMessageForAuthenticationException() {
        AuthenticationException ex = new AuthenticationException("Token inválido");

        ResponseEntity<Map<String, Object>> response = handler.handleAuthentication(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Token inválido", response.getBody().get("message"));
    }

    @Test
    void shouldReturnInternalServerErrorForGenericException() {
        Exception ex = new Exception("Unexpected error");

        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Erro interno no servidor", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }
}

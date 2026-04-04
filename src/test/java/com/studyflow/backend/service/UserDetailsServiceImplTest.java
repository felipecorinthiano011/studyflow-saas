package com.studyflow.backend.service;

import com.studyflow.backend.domain.user.entity.User;
import com.studyflow.backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldLoadUserByEmailSuccessfully() {
        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .password("encodedPassword")
                .build();

        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(user));

        UserDetails result = userDetailsServiceImpl.loadUserByUsername("test@email.com");

        assertNotNull(result);
        assertEquals("test@email.com", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        verify(userRepository).findByEmail("test@email.com");
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("notfound@email.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsServiceImpl.loadUserByUsername("notfound@email.com"));

        verify(userRepository).findByEmail("notfound@email.com");
    }
}

package com.studyflow.backend.service;

import com.studyflow.backend.domain.user.service.UserService;

import com.studyflow.backend.shared.dto.UserRequestDTO;
import com.studyflow.backend.shared.dto.UserResponseDTO;
import com.studyflow.backend.domain.user.entity.User;
import com.studyflow.backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateUserSuccessfully() {
        // Arrange
        UserRequestDTO dto = UserRequestDTO.builder()
                .name("João Silva")
                .email("joao@email.com")
                .password("senha123")
                .build();

        User savedUser = User.builder()
                .id(1L)
                .name("João Silva")
                .email("joao@email.com")
                .password("encodedPassword")
                .build();

        when(passwordEncoder.encode("senha123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponseDTO result = userService.create(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("João Silva", result.getName());
        assertEquals("joao@email.com", result.getEmail());
        verify(passwordEncoder).encode("senha123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldReturnAllUsers() {
        // Arrange
        User user1 = User.builder()
                .id(1L)
                .name("User 1")
                .email("user1@email.com")
                .build();

        User user2 = User.builder()
                .id(2L)
                .name("User 2")
                .email("user2@email.com")
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<UserResponseDTO> result = userService.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("User 1", result.get(0).getName());
        assertEquals("User 2", result.get(1).getName());
        verify(userRepository).findAll();
    }
}

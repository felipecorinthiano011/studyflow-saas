package com.studyflow.backend.service;
import com.studyflow.backend.domain.organization.repository.OrganizationRepository;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private OrganizationRepository organizationRepository;
    @InjectMocks
    private UserService userService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(organizationRepository.findByName(anyString())).thenReturn(Optional.empty());
    }
    @Test
    void shouldCreateUserSuccessfully() {
        UserRequestDTO dto = UserRequestDTO.builder()
                .name("Joao Silva")
                .email("joao@email.com")
                .password("senha123")
                .build();
        User savedUser = User.builder()
                .id(1L)
                .name("Joao Silva")
                .email("joao@email.com")
                .password("encodedPassword")
                .build();
        when(passwordEncoder.encode("senha123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        UserResponseDTO result = userService.create(dto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Joao Silva", result.getName());
        assertEquals("joao@email.com", result.getEmail());
        verify(passwordEncoder).encode("senha123");
        verify(userRepository).save(any(User.class));
    }
    @Test
    void shouldReturnAllUsers() {
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
        List<UserResponseDTO> result = userService.findAll();
        assertEquals(2, result.size());
        assertEquals("User 1", result.get(0).getName());
        assertEquals("User 2", result.get(1).getName());
        verify(userRepository).findAll();
    }
    @Test
    void shouldFindUserByEmailSuccessfully() {
        User user = User.builder()
                .id(1L)
                .name("Joao Silva")
                .email("joao@email.com")
                .password("encodedPassword")
                .build();
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));
        UserResponseDTO result = userService.findByEmail("joao@email.com");
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Joao Silva", result.getName());
        assertEquals("joao@email.com", result.getEmail());
        verify(userRepository).findByEmail("joao@email.com");
    }
    @Test
    void shouldThrowWhenUserNotFoundByEmail() {
        when(userRepository.findByEmail("notfound@email.com")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.findByEmail("notfound@email.com"));
        assertEquals("User not found", ex.getMessage());
        verify(userRepository).findByEmail("notfound@email.com");
    }
}
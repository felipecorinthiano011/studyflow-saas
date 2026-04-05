package com.studyflow.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyflow.backend.shared.dto.LoginRequest;
import com.studyflow.backend.shared.dto.UserRequestDTO;
import com.studyflow.backend.domain.user.entity.Role;
import com.studyflow.backend.domain.user.entity.User;
import com.studyflow.backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();

        // Cria um usuário de teste
        UserRequestDTO userRequest = UserRequestDTO.builder()
                .name("Test User")
                .email("test@email.com")
                .password("senha123")
                .build();

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk());

        // Faz login para obter o token
        LoginRequest loginRequest = new LoginRequest("test@email.com", "senha123");

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        authToken = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        UserRequestDTO request = UserRequestDTO.builder()
                .name("New User")
                .email("newuser@email.com")
                .password("senha123")
                .build();

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.email").value("newuser@email.com"));
    }

    @Test
    void shouldFailCreateUserWithInvalidEmail() throws Exception {
        UserRequestDTO request = UserRequestDTO.builder()
                .name("User")
                .email("invalidemail")
                .password("senha123")
                .build();

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldListAllUsers_asUser_returns403() throws Exception {
        mockMvc.perform(get("/users")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldListAllUsers_asAdmin_returns200() throws Exception {
        // Create an admin user directly via repository (bypasses the public API)
        User adminUser = User.builder()
                .name("Admin User")
                .email("admin@email.com")
                .password(passwordEncoder.encode("adminpass1"))
                .role(Role.ADMIN)
                .build();
        userRepository.save(adminUser);

        LoginRequest adminLogin = new LoginRequest("admin@email.com", "adminpass1");
        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn();

        String adminToken = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();

        mockMvc.perform(get("/users")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetUserProfile() throws Exception {
        mockMvc.perform(get("/users/me")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void shouldFailGetProfileWithoutToken() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isForbidden());
    }
}


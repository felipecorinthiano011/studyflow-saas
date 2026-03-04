package com.studyflow.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyflow.backend.dto.LoginRequest;
import com.studyflow.backend.dto.StudyItemRequestDTO;
import com.studyflow.backend.dto.UserRequestDTO;
import com.studyflow.backend.repository.StudyItemRepository;
import com.studyflow.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class StudyItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @Autowired
    private StudyItemRepository studyItemRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() throws Exception {

        studyItemRepository.deleteAll();
        userRepository.deleteAll();

        // 1️⃣ Criar usuário
        UserRequestDTO user = UserRequestDTO.builder()
                .name("Teste User")
                .email("teste@study.com")
                .password("123456")
                .build();

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        // 2️⃣ Fazer login
        LoginRequest login = new LoginRequest("teste@study.com", "123456");

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        this.token = response.replace("\"", "");
    }

    @Test
    void shouldCreateStudyItem() throws Exception {

        StudyItemRequestDTO dto = StudyItemRequestDTO.builder()
                .title("Estudar Spring")
                .description("Revisar segurança")
                .build();

        mockMvc.perform(post("/study-items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Estudar Spring"));
    }

    @Test
    void shouldFailWhenTitleIsBlank() throws Exception {

        StudyItemRequestDTO dto = StudyItemRequestDTO.builder()
                .title("")
                .description("Erro esperado")
                .build();

        mockMvc.perform(post("/study-items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnUnauthorizedWithoutToken() throws Exception {

        mockMvc.perform(get("/study-items"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldListStudyItems() throws Exception {

        StudyItemRequestDTO dto = StudyItemRequestDTO.builder()
                .title("Item Teste")
                .description("Descrição")
                .build();

        mockMvc.perform(post("/study-items")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)));

        mockMvc.perform(get("/study-items")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Item Teste"));
    }
}
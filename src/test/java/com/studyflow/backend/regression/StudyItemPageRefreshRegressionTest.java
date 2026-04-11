package com.studyflow.backend.regression;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyflow.backend.domain.study.repository.StudyItemRepository;
import com.studyflow.backend.domain.user.repository.UserRepository;
import com.studyflow.backend.shared.dto.LoginRequest;
import com.studyflow.backend.shared.dto.StudyItemRequestDTO;
import com.studyflow.backend.shared.dto.UserRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * HTTP-level regression tests that guard against the "items disappear on page refresh" bug.
 *
 * <p>The original defect: {@code GET /study-items} returned HTTP 500 on the <em>second</em>
 * request because {@code Page<StudyItemResponseDTO>} was stored in Redis and could not be
 * deserialized back.  These tests simulate the exact browser scenario — a user loads the
 * page, refreshes it multiple times — and assert that:
 * <ul>
 *   <li>Every consecutive {@code GET /study-items} call returns HTTP 200 (never 500).</li>
 *   <li>The response JSON conforms to the {@code PageResponseDTO} contract
 *       ({@code content}, {@code page}, {@code size}, {@code totalElements},
 *       {@code totalPages}, {@code last}).</li>
 *   <li>Items created before a refresh are still present after the refresh.</li>
 *   <li>Items from other users are never visible in the response.</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class StudyItemPageRefreshRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudyItemRepository studyItemRepository;

    @Autowired
    private UserRepository userRepository;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        studyItemRepository.deleteAll();
        userRepository.deleteAll();

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        UserRequestDTO.builder()
                                .name("Refresh Tester")
                                .email("refresh.regression@test.com")
                                .password("senha123")
                                .build())));

        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("refresh.regression@test.com", "senha123"))))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        token = objectMapper.readTree(loginResponse).get("token").asText();
    }

    // ── Scenario 1: no 500 on any consecutive GET (the original bug) ─────────

    @Test
    void getStudyItems_shouldReturn200OnFiveConsecutiveCalls() throws Exception {
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/study-items")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());
        }
    }

    // ── Scenario 2: response must match PageResponseDTO JSON contract ─────────

    @Test
    void getStudyItems_responseMustContainAllPageResponseDTOFields() throws Exception {
        mockMvc.perform(get("/study-items")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists())
                .andExpect(jsonPath("$.page").exists())
                .andExpect(jsonPath("$.size").exists())
                .andExpect(jsonPath("$.totalElements").exists())
                .andExpect(jsonPath("$.totalPages").exists())
                .andExpect(jsonPath("$.last").exists());
    }

    // ── Scenario 3: items created before a refresh must still be present ──────

    @Test
    void itemsCreatedBeforeRefreshMustPersistAcrossMultipleRefreshes() throws Exception {
        mockMvc.perform(post("/study-items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                StudyItemRequestDTO.builder()
                                        .title("Regression Item")
                                        .description("Must survive every refresh")
                                        .build())))
                .andExpect(status().isOk());

        for (int refresh = 0; refresh < 3; refresh++) {
            mockMvc.perform(get("/study-items")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.content[0].title").value("Regression Item"));
        }
    }

    // ── Scenario 4: default pagination parameters are applied ─────────────────

    @Test
    void getStudyItems_defaultPaginationParametersAreApplied() throws Exception {
        mockMvc.perform(get("/study-items")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20));
    }

    // ── Scenario 5: explicit pagination parameters are respected ──────────────

    @Test
    void getStudyItems_explicitPaginationParametersAreReflectedInResponse() throws Exception {
        mockMvc.perform(get("/study-items")
                        .param("page", "0")
                        .param("size", "5")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(5));
    }

    // ── Scenario 6: items from other users must not leak into response ─────────

    @Test
    void getStudyItems_mustOnlyReturnItemsBelongingToAuthenticatedUser() throws Exception {
        // Register a second user and have them create an item
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        UserRequestDTO.builder()
                                .name("Other User")
                                .email("other.user.regression@test.com")
                                .password("senha123")
                                .build())));

        String otherLoginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LoginRequest("other.user.regression@test.com", "senha123"))))
                .andReturn().getResponse().getContentAsString();

        String otherToken = objectMapper.readTree(otherLoginResponse).get("token").asText();

        mockMvc.perform(post("/study-items")
                .header("Authorization", "Bearer " + otherToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        StudyItemRequestDTO.builder()
                                .title("Other User's Item")
                                .build())));

        // The first user must still see an empty list
        mockMvc.perform(get("/study-items")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.content", hasSize(0)));
    }

    // ── Scenario 7: each item in content has required DTO fields ──────────────

    @Test
    void getStudyItems_eachItemInContentMustExposeRequiredDTOFields() throws Exception {
        mockMvc.perform(post("/study-items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                StudyItemRequestDTO.builder()
                                        .title("Fields Check")
                                        .description("All fields present")
                                        .build())))
                .andExpect(status().isOk());

        mockMvc.perform(get("/study-items")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").exists())
                .andExpect(jsonPath("$.content[0].title").value("Fields Check"))
                .andExpect(jsonPath("$.content[0].description").value("All fields present"))
                .andExpect(jsonPath("$.content[0].createdAt").exists());
    }
}

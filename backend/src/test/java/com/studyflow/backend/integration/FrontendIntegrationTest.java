package com.studyflow.backend.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Testes E2E (End-to-End) que simulam interações de um front-end com a API.
 * Estes testes verificam fluxos completos de usuário, como login, criação de items, etc.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Testes E2E - Fluxos Completos de Usuário")
class FrontendIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";
    }

    @Test
    @DisplayName("Cenário 1: Usuário novo se registra e cria primeiro estudo")
    void testNewUserRegistrationAndFirstStudy() {
        // Front-end enviaria: POST /users
        String createUserPayload = """
                {
                  "name": "Maria Silva",
                  "email": "maria.silva@test.com",
                  "password": "SenhaForte123!"
                }
                """;

        Response userResponse = given()
                .contentType("application/json")
                .body(createUserPayload)
                .post("/users");

        userResponse.then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("email", equalTo("maria.silva@test.com"));

        // Front-end enviaria: POST /auth/login
        String loginPayload = """
                {
                  "email": "maria.silva@test.com",
                  "password": "SenhaForte123!"
                }
                """;

        Response loginResponse = given()
                .contentType("application/json")
                .body(loginPayload)
                .post("/auth/login");

        loginResponse.then()
                .statusCode(200)
                .body("token", notNullValue());

        String token = loginResponse.jsonPath().getString("token");

        // Front-end enviaria: POST /study-items
        String createStudyPayload = """
                {
                  "title": "Aprender JavaScript",
                  "description": "Estudar conceitos avançados de JS para desenvolvimento web"
                }
                """;

        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .body(createStudyPayload)
                .post("/study-items")
                .then()
                .statusCode(200)
                .body("title", equalTo("Aprender JavaScript"));
    }

    @Test
    @DisplayName("Cenário 2: Usuário existente faz login e visualiza seus estudos")
    void testExistingUserLoginAndViewStudies() {
        // Setup: Criar usuário e adicionar alguns estudos
        String createUserPayload = """
                {
                  "name": "Carlos Costa",
                  "email": "carlos.costa@test.com",
                  "password": "SenhaForte123!"
                }
                """;

        given()
                .contentType("application/json")
                .body(createUserPayload)
                .post("/users");

        // Login
        String loginPayload = """
                {
                  "email": "carlos.costa@test.com",
                  "password": "SenhaForte123!"
                }
                """;

        Response loginResponse = given()
                .contentType("application/json")
                .body(loginPayload)
                .post("/auth/login");

        String token = loginResponse.jsonPath().getString("token");

        // Criar múltiplos estudos
        for (int i = 1; i <= 3; i++) {
            String createStudyPayload = """
                    {
                      "title": "Estudo %d",
                      "description": "Descrição do estudo %d"
                    }
                    """.formatted(i, i);

            given()
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                    .body(createStudyPayload)
                    .post("/study-items");
        }

        // Visualizar todos os estudos - GET /study-items
        given()
                .header("Authorization", "Bearer " + token)
                .get("/study-items")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(3));

        // Visualizar perfil do usuário - GET /users/me
        given()
                .header("Authorization", "Bearer " + token)
                .get("/users/me")
                .then()
                .statusCode(200)
                .body("email", equalTo("carlos.costa@test.com"))
                .body("name", equalTo("Carlos Costa"));
    }

    @Test
    @DisplayName("Cenário 3: Validação de erros - Tentativa de acesso sem autenticação")
    void testUnauthorizedAccess() {
        // Tentar acessar /study-items sem token
        given()
                .get("/study-items")
                .then()
                .statusCode(403);

        // Tentar acessar /users/me sem token
        given()
                .get("/users/me")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Cenário 4: Validação de erros - Email inválido no registro")
    void testInvalidEmailValidation() {
        String invalidEmailPayload = """
                {
                  "name": "Invalid Email User",
                  "email": "nao-e-email",
                  "password": "SenhaForte123!"
                }
                """;

        given()
                .contentType("application/json")
                .body(invalidEmailPayload)
                .post("/users")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Cenário 5: Validação de erros - Credenciais incorretas no login")
    void testInvalidLoginCredentials() {
        // Criar um usuário
        String createUserPayload = """
                {
                  "name": "Test User",
                  "email": "test.user@email.com",
                  "password": "SenhaCorreta123!"
                }
                """;

        given()
                .contentType("application/json")
                .body(createUserPayload)
                .post("/users");

        // Tentar login com senha incorreta
        String incorrectLoginPayload = """
                {
                  "email": "test.user@email.com",
                  "password": "SenhaIncorreta123!"
                }
                """;

        given()
                .contentType("application/json")
                .body(incorrectLoginPayload)
                .post("/auth/login")
                .then()
                .statusCode(401);

        // Tentar login com email inexistente
        String nonExistentLoginPayload = """
                {
                  "email": "inexistente@email.com",
                  "password": "SenhaForte123!"
                }
                """;

        given()
                .contentType("application/json")
                .body(nonExistentLoginPayload)
                .post("/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Cenário 6: Token JWT inválido ou expirado")
    void testInvalidJwtToken() {
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.invalid";

        given()
                .header("Authorization", "Bearer " + invalidToken)
                .get("/study-items")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Cenário 7: Usuário tenta registrar com email duplicado")
    void testDuplicateEmailRegistration() {
        String userPayload = """
                {
                  "name": "First User",
                  "email": "duplicate@email.com",
                  "password": "SenhaForte123!"
                }
                """;

        // Primeiro registro
        given()
                .contentType("application/json")
                .body(userPayload)
                .post("/users")
                .then()
                .statusCode(200);

        // Segundo registro com mesmo email
        String duplicatePayload = """
                {
                  "name": "Second User",
                  "email": "duplicate@email.com",
                  "password": "OutraSenha123!"
                }
                """;

        given()
                .contentType("application/json")
                .body(duplicatePayload)
                .post("/users")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Cenário 8: Validação de campos obrigatórios")
    void testMissingRequiredFields() {
        // Faltando name
        String missingNamePayload = """
                {
                  "email": "test@email.com",
                  "password": "SenhaForte123!"
                }
                """;

        given()
                .contentType("application/json")
                .body(missingNamePayload)
                .post("/users")
                .then()
                .statusCode(400);

        // Faltando email
        String missingEmailPayload = """
                {
                  "name": "Test User",
                  "password": "SenhaForte123!"
                }
                """;

        given()
                .contentType("application/json")
                .body(missingEmailPayload)
                .post("/users")
                .then()
                .statusCode(400);

        // Faltando password
        String missingPasswordPayload = """
                {
                  "name": "Test User",
                  "email": "test@email.com"
                }
                """;

        given()
                .contentType("application/json")
                .body(missingPasswordPayload)
                .post("/users")
                .then()
                .statusCode(400);
    }

}


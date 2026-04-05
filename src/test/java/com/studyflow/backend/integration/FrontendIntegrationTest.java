package com.studyflow.backend.integration;

import com.studyflow.backend.support.AbstractRestAssuredTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * End-to-End tests simulating front-end interactions with the API.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Testes E2E - Fluxos Completos de UsuÃ¡rio")
class FrontendIntegrationTest extends AbstractRestAssuredTest {

    @Test
    @DisplayName("CenÃ¡rio 1: UsuÃ¡rio novo se registra e cria primeiro estudo")
    void testNewUserRegistrationAndFirstStudy() {
        String token = createUserAndGetToken("Maria Silva", "maria.silva@test.com", "SenhaForte123!");

        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .body("""
                        {"title": "Aprender JavaScript",
                         "description": "Estudar conceitos avanÃ§ados de JS"}
                        """)
                .post("/study-items")
                .then()
                .statusCode(200)
                .body("title", equalTo("Aprender JavaScript"));
    }

    @Test
    @DisplayName("CenÃ¡rio 2: UsuÃ¡rio existente faz login e visualiza seus estudos")
    void testExistingUserLoginAndViewStudies() {
        String token = createUserAndGetToken("Carlos Costa", "carlos.costa@test.com", "SenhaForte123!");

        for (int i = 1; i <= 3; i++) {
            given()
                    .contentType("application/json")
                    .header("Authorization", "Bearer " + token)
                    .body("""
                            {"title": "Estudo %d", "description": "DescriÃ§Ã£o do estudo %d"}
                            """.formatted(i, i))
                    .post("/study-items");
        }

        given().header("Authorization", "Bearer " + token).get("/study-items")
                .then().statusCode(200).body("size()", greaterThanOrEqualTo(3));

        given().header("Authorization", "Bearer " + token).get("/users/me")
                .then().statusCode(200)
                .body("email", equalTo("carlos.costa@test.com"))
                .body("name", equalTo("Carlos Costa"));
    }

    @Test
    @DisplayName("CenÃ¡rio 3: Acesso sem autenticaÃ§Ã£o retorna 403")
    void testUnauthorizedAccess() {
        given().get("/study-items").then().statusCode(403);
        given().get("/users/me").then().statusCode(403);
    }

    @Test
    @DisplayName("CenÃ¡rio 4: Email invÃ¡lido no registro retorna 400")
    void testInvalidEmailValidation() {
        given()
                .contentType("application/json")
                .body("""
                        {"name": "Invalid Email User", "email": "nao-e-email",
                         "password": "SenhaForte123!"}
                        """)
                .post("/users")
                .then().statusCode(400);
    }

    @Test
    @DisplayName("CenÃ¡rio 5: Credenciais incorretas no login retornam 401")
    void testInvalidLoginCredentials() {
        createUserAndGetToken("Test User", "test.user@email.com", "SenhaCorreta123!");

        given()
                .contentType("application/json")
                .body("""
                        {"email": "test.user@email.com", "password": "SenhaIncorreta123!"}
                        """)
                .post("/auth/login")
                .then().statusCode(401);

        given()
                .contentType("application/json")
                .body("""
                        {"email": "inexistente@email.com", "password": "SenhaForte123!"}
                        """)
                .post("/auth/login")
                .then().statusCode(401);
    }

    @Test
    @DisplayName("CenÃ¡rio 6: Token JWT invÃ¡lido retorna 403")
    void testInvalidJwtToken() {
        given()
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.invalid")
                .get("/study-items")
                .then().statusCode(403);
    }

    @Test
    @DisplayName("CenÃ¡rio 7: Email duplicado retorna 400 ou 409")
    void testDuplicateEmailRegistration() {
        String body = """
                {"name": "First User", "email": "duplicate@email.com", "password": "SenhaForte123!"}
                """;

        given().contentType("application/json").body(body).post("/users")
                .then().statusCode(200);

        given().contentType("application/json")
                .body("""
                        {"name": "Second User", "email": "duplicate@email.com",
                         "password": "OutraSenha123!"}
                        """)
                .post("/users")
                .then().statusCode(anyOf(is(400), is(409)));
    }

    @Test
    @DisplayName("CenÃ¡rio 8: Campos obrigatÃ³rios ausentes retornam 400")
    void testMissingRequiredFields() {
        given().contentType("application/json")
                .body("""
                        {"email": "test@email.com", "password": "SenhaForte123!"}
                        """)
                .post("/users").then().statusCode(400);

        given().contentType("application/json")
                .body("""
                        {"name": "Test User", "password": "SenhaForte123!"}
                        """)
                .post("/users").then().statusCode(400);

        given().contentType("application/json")
                .body("""
                        {"name": "Test User", "email": "test@email.com"}
                        """)
                .post("/users").then().statusCode(400);
    }
}

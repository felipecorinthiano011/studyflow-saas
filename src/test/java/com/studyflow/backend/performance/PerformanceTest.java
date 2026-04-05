package com.studyflow.backend.performance;

import com.studyflow.backend.support.AbstractRestAssuredTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Additional performance and stress tests.
 * Common REST Assured setup is inherited from {@link AbstractRestAssuredTest}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Testes de Performance - API StudyFlow")
class PerformanceTest extends AbstractRestAssuredTest {

    @Test
    @DisplayName("Carga: Criação em massa de usuários (50)")
    void testMassUserCreation() {
        long start = System.currentTimeMillis();
        int created = 0;

        for (int i = 0; i < 50; i++) {
            try {
                given().contentType("application/json")
                        .body("""
                                {"name":"Load Test User %d",
                                 "email":"load.test.%d@studyflow.com",
                                 "password":"SenhaForte123!"}
                                """.formatted(i, i))
                        .post("/users")
                        .then().statusCode(200);
                created++;
            } catch (Exception e) {
                System.err.println("Erro ao criar usuário " + i + ": " + e.getMessage());
            }
        }

        long total = System.currentTimeMillis() - start;
        System.out.printf("Criados: %d/50 em %d ms (%.2f users/sec)%n",
                created, total, created * 1000.0 / total);

        assertTrue(created >= 40, "Deveria criar pelo menos 40/50 usuários, criou: " + created);
    }

    @Test
    @DisplayName("SLA: Todos os endpoints respondem em menos de 1 segundo")
    void testResponseTimeSLA() {
        final long sla = 1000L;
        String token = createUserAndGetToken("SLA Test User", "sla.test@studyflow.com", "SenhaForte123!");

        long userCreationTime = measurePost("/users", """
                {"name":"SLA Measure","email":"sla.measure@studyflow.com","password":"SenhaForte123!"}
                """, null);

        long loginTime = measurePost("/auth/login", """
                {"email":"sla.test@studyflow.com","password":"SenhaForte123!"}
                """, null);

        long listTime = measureGet("/study-items", token);

        System.out.printf("SLA — criação: %dms | login: %dms | listagem: %dms%n",
                userCreationTime, loginTime, listTime);

        assertTrue(userCreationTime < sla, "Criação excedeu SLA: " + userCreationTime + "ms");
        assertTrue(loginTime < sla, "Login excedeu SLA: " + loginTime + "ms");
        assertTrue(listTime < sla, "Listagem excedeu SLA: " + listTime + "ms");
    }

    @Test
    @DisplayName("Estresse: 100 requisições sequenciais a GET /study-items")
    void testStressMultipleRequests() {
        String token = createUserAndGetToken("Stress Test User", "stress.test@studyflow.com", "SenhaForte123!");
        int total = 100, ok = 0, failed = 0;

        for (int i = 0; i < total; i++) {
            try {
                given().header("Authorization", "Bearer " + token)
                        .get("/study-items")
                        .then().statusCode(200);
                ok++;
            } catch (Exception e) {
                failed++;
            }
        }

        System.out.printf("Estresse — OK: %d | Falhas: %d | Taxa: %.1f%%%n",
                ok, failed, ok * 100.0 / total);
        assertTrue(ok >= 90, "Taxa de sucesso abaixo de 90%: " + ok + "/" + total);
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private long measurePost(String path, String body, String token) {
        var spec = given().contentType("application/json").body(body);
        if (token != null) spec = spec.header("Authorization", "Bearer " + token);
        long start = System.currentTimeMillis();
        spec.post(path);
        return System.currentTimeMillis() - start;
    }

    private long measureGet(String path, String token) {
        long start = System.currentTimeMillis();
        given().header("Authorization", "Bearer " + token).get(path);
        return System.currentTimeMillis() - start;
    }
}


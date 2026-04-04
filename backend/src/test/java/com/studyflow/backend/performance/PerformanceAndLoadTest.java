package com.studyflow.backend.performance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Testes de Performance e Carga")
class PerformanceAndLoadTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";
    }

    @Test
    @DisplayName("Teste de Carga: Múltiplas requisições de login simultâneas")
    void testConcurrentLoginRequests() throws InterruptedException {
        int numberOfThreads = 10;
        int requestsPerThread = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        // Criar usuário de teste
        String createUserBody = """
                {
                  "name": "Performance Test User",
                  "email": "perf.test@email.com",
                  "password": "senha123456"
                }
                """;

        given()
                .contentType("application/json")
                .body(createUserBody)
                .when()
                .post("/users");

        // Executar requisições em paralelo
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    for (int j = 0; j < requestsPerThread; j++) {
                        String loginBody = """
                                {
                                  "email": "perf.test@email.com",
                                  "password": "senha123456"
                                }
                                """;

                        long startTime = System.currentTimeMillis();
                        Response response = given()
                                .contentType("application/json")
                                .body(loginBody)
                                .when()
                                .post("/auth/login");
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;

                        if (response.statusCode() == 200 && duration < 1000) {
                            successCount.incrementAndGet();
                        } else {
                            failureCount.incrementAndGet();
                        }

                        System.out.println("Login request completed in " + duration + "ms");
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        System.out.println("Total requests: " + (numberOfThreads * requestsPerThread));
        System.out.println("Successful requests: " + successCount.get());
        System.out.println("Failed requests: " + failureCount.get());

        // Verificar que pelo menos 80% das requisições foram bem-sucedidas
        int expectedSuccess = (int) ((numberOfThreads * requestsPerThread) * 0.8);
        assertEquals(true, successCount.get() >= expectedSuccess,
            "Sucesso em carga: " + successCount.get() + " >= " + expectedSuccess);
    }

    @Test
    @DisplayName("Teste de Carga: Múltiplas criações de Study Items")
    void testConcurrentCreateStudyItems() throws InterruptedException {
        int numberOfThreads = 5;
        int itemsPerThread = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        // Criar usuário de teste
        String createUserBody = """
                {
                  "name": "Items Test User",
                  "email": "items.perf@email.com",
                  "password": "senha123456"
                }
                """;

        given()
                .contentType("application/json")
                .body(createUserBody)
                .when()
                .post("/users");

        // Fazer login
        String loginBody = """
                {
                  "email": "items.perf@email.com",
                  "password": "senha123456"
                }
                """;

        Response loginResponse = given()
                .contentType("application/json")
                .body(loginBody)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String token = loginResponse.jsonPath().getString("token");

        // Criar items em paralelo
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadIndex = i;
            executorService.execute(() -> {
                try {
                    for (int j = 0; j < itemsPerThread; j++) {
                        String createItemBody = """
                                {
                                  "title": "Study Item T%d-I%d",
                                  "description": "Performance test item from thread %d"
                                }
                                """.formatted(threadIndex, j, threadIndex);

                        long startTime = System.currentTimeMillis();
                        Response response = given()
                                .contentType("application/json")
                                .header("Authorization", "Bearer " + token)
                                .body(createItemBody)
                                .when()
                                .post("/study-items");
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;

                        if (response.statusCode() == 200 && duration < 500) {
                            successCount.incrementAndGet();
                        }

                        System.out.println("Create item request completed in " + duration + "ms");
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        System.out.println("Total item creation requests: " + (numberOfThreads * itemsPerThread));
        System.out.println("Successful item creations: " + successCount.get());

        // Verificar que pelo menos 80% das requisições foram bem-sucedidas
        int expectedSuccess = (int) ((numberOfThreads * itemsPerThread) * 0.8);
        assertEquals(true, successCount.get() >= expectedSuccess,
            "Sucesso na criação de items: " + successCount.get() + " >= " + expectedSuccess);
    }

    @Test
    @DisplayName("Teste de Desempenho: Tempo de resposta de GET /users")
    void testGetUsersResponseTime() {
        // Criar usuário
        String createUserBody = """
                {
                  "name": "Response Test User",
                  "email": "response.test@email.com",
                  "password": "senha123456"
                }
                """;

        given()
                .contentType("application/json")
                .body(createUserBody)
                .when()
                .post("/users");

        // Fazer login
        String loginBody = """
                {
                  "email": "response.test@email.com",
                  "password": "senha123456"
                }
                """;

        Response loginResponse = given()
                .contentType("application/json")
                .body(loginBody)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String token = loginResponse.jsonPath().getString("token");

        // Testar tempo de resposta
        long startTime = System.currentTimeMillis();
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("size()", notNullValue());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("GET /users response time: " + duration + "ms");
        assertEquals(true, duration < 1000, "Response time should be less than 1 second, but was " + duration + "ms");
    }

    @Test
    @DisplayName("Teste de Desempenho: Tempo de resposta de POST /study-items")
    void testCreateStudyItemResponseTime() {
        // Criar usuário
        String createUserBody = """
                {
                  "name": "Item Response Test User",
                  "email": "item.response@email.com",
                  "password": "senha123456"
                }
                """;

        given()
                .contentType("application/json")
                .body(createUserBody)
                .when()
                .post("/users");

        // Fazer login
        String loginBody = """
                {
                  "email": "item.response@email.com",
                  "password": "senha123456"
                }
                """;

        Response loginResponse = given()
                .contentType("application/json")
                .body(loginBody)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String token = loginResponse.jsonPath().getString("token");

        // Testar tempo de resposta
        String createItemBody = """
                {
                  "title": "Performance Item",
                  "description": "Testing response time"
                }
                """;

        long startTime = System.currentTimeMillis();
        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .body(createItemBody)
                .when()
                .post("/study-items")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("POST /study-items response time: " + duration + "ms");
        assertEquals(true, duration < 1000, "Response time should be less than 1 second, but was " + duration + "ms");
    }

    @Test
    @DisplayName("Teste de Desempenho: Autenticação JWT")
    void testJwtAuthenticationPerformance() {
        // Criar usuário
        String createUserBody = """
                {
                  "name": "JWT Perf Test",
                  "email": "jwt.perf@email.com",
                  "password": "senha123456"
                }
                """;

        given()
                .contentType("application/json")
                .body(createUserBody)
                .when()
                .post("/users");

        String loginBody = """
                {
                  "email": "jwt.perf@email.com",
                  "password": "senha123456"
                }
                """;

        // Testar tempo de geração de token
        long startTime = System.currentTimeMillis();
        Response loginResponse = given()
                .contentType("application/json")
                .body(loginBody)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .response();
        long endTime = System.currentTimeMillis();
        long tokenGenerationTime = endTime - startTime;

        String token = loginResponse.jsonPath().getString("token");

        System.out.println("JWT token generation time: " + tokenGenerationTime + "ms");
        assertEquals(true, tokenGenerationTime < 500,
            "Token generation should be less than 500ms, but was " + tokenGenerationTime + "ms");

        // Testar tempo de validação do token
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 50; i++) {
            given()
                    .header("Authorization", "Bearer " + token)
                    .when()
                    .get("/users/me");
        }
        endTime = System.currentTimeMillis();
        long validationTime = (endTime - startTime) / 50;

        System.out.println("Average JWT validation time: " + validationTime + "ms");
        assertEquals(true, validationTime < 100,
            "Average validation should be less than 100ms, but was " + validationTime + "ms");
    }

}


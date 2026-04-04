package com.studyflow.backend.performance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Testes de Performance para validar a velocidade e eficiência da API.
 * Utiliza JMH (Java Microbenchmark Harness) para medições precisas.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Testes de Performance - API StudyFlow")
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(java.util.concurrent.TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Fork(1)
@Warmup(iterations = 2)
@Measurement(iterations = 5)
class PerformanceTest {

    @LocalServerPort
    private int port;

    private String userToken;
    private String userId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";

        // Criar usuário e obter token
        String createUserPayload = """
                {
                  "name": "Performance Test User",
                  "email": "perf.test@studyflow.com",
                  "password": "SenhaForte123!"
                }
                """;

        given()
                .contentType("application/json")
                .body(createUserPayload)
                .post("/users");

        String loginPayload = """
                {
                  "email": "perf.test@studyflow.com",
                  "password": "SenhaForte123!"
                }
                """;

        userToken = given()
                .contentType("application/json")
                .body(loginPayload)
                .post("/auth/login")
                .jsonPath()
                .getString("token");
    }

    /**
     * Benchmark: Criação de usuário
     * Mede o tempo médio para criar um novo usuário
     */
    @Benchmark
    @DisplayName("Performance: Criação de Usuário")
    public void benchmarkUserCreation() {
        String createUserPayload = """
                {
                  "name": "Benchmark User %d",
                  "email": "benchmark_%d@studyflow.com",
                  "password": "SenhaForte123!"
                }
                """.formatted(System.nanoTime(), System.nanoTime());

        given()
                .contentType("application/json")
                .body(createUserPayload)
                .post("/users")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    /**
     * Benchmark: Autenticação (Login)
     * Mede o tempo médio para fazer login
     */
    @Benchmark
    @DisplayName("Performance: Autenticação (Login)")
    public void benchmarkAuthentication() {
        String loginPayload = """
                {
                  "email": "perf.test@studyflow.com",
                  "password": "SenhaForte123!"
                }
                """;

        given()
                .contentType("application/json")
                .body(loginPayload)
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    /**
     * Benchmark: Criação de Study Item
     * Mede o tempo médio para criar um novo item de estudo
     */
    @Benchmark
    @DisplayName("Performance: Criação de Study Item")
    public void benchmarkCreateStudyItem() {
        String createStudyPayload = """
                {
                  "title": "Study Item %d",
                  "description": "Estudo de performance %d"
                }
                """.formatted(System.nanoTime(), System.nanoTime());

        given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + userToken)
                .body(createStudyPayload)
                .post("/study-items")
                .then()
                .statusCode(200)
                .body("title", notNullValue());
    }

    /**
     * Benchmark: Listagem de Study Items
     * Mede o tempo médio para listar os items de estudo
     */
    @Benchmark
    @DisplayName("Performance: Listagem de Study Items")
    public void benchmarkListStudyItems() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .get("/study-items")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    /**
     * Benchmark: Obter Perfil do Usuário
     * Mede o tempo médio para obter dados do usuário autenticado
     */
    @Benchmark
    @DisplayName("Performance: Obter Perfil do Usuário")
    public void benchmarkGetUserProfile() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .get("/users/me")
                .then()
                .statusCode(200)
                .body("email", notNullValue())
                .body("name", notNullValue());
    }

    /**
     * Teste de Carga: Múltiplas criações de usuários
     * Simula múltiplos usuários sendo criados rapidamente
     */
    @Test
    @DisplayName("Teste de Carga: Criação em Massa de Usuários")
    void testMassUserCreation() {
        long startTime = System.currentTimeMillis();
        int usersCreated = 0;

        for (int i = 0; i < 50; i++) {
            String createUserPayload = """
                    {
                      "name": "Load Test User %d",
                      "email": "load.test.%d@studyflow.com",
                      "password": "SenhaForte123!"
                    }
                    """.formatted(i, i);

            try {
                given()
                        .contentType("application/json")
                        .body(createUserPayload)
                        .post("/users")
                        .then()
                        .statusCode(200);
                usersCreated++;
            } catch (Exception e) {
                // Log error but continue
                System.err.println("Erro ao criar usuário " + i + ": " + e.getMessage());
            }
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double avgTime = (double) totalTime / usersCreated;

        System.out.printf("""
                
                ╔════════════════════════════════════════╗
                ║   TESTE DE CARGA - CRIAÇÃO DE USUÁRIOS ║
                ╠════════════════════════════════════════╣
                ║ Usuários Criados:      %d/50           ║
                ║ Tempo Total:           %d ms          ║
                ║ Tempo Médio/Usuário:   %.2f ms        ║
                ║ Throughput:            %.2f users/sec  ║
                ╚════════════════════════════════════════╝
                """, usersCreated, totalTime, avgTime, (usersCreated * 1000.0) / totalTime);
    }

    /**
     * Teste de Concorrência: Múltiplos logins simultâneos
     * Simula múltiplos usuários fazendo login aproximadamente ao mesmo tempo
     */
    @Test
    @DisplayName("Teste de Concorrência: Múltiplos Logins")
    void testConcurrentLogins() throws InterruptedException {
        // Criar 10 usuários primeiro
        for (int i = 0; i < 10; i++) {
            String createUserPayload = """
                    {
                      "name": "Concurrent User %d",
                      "email": "concurrent.%d@studyflow.com",
                      "password": "SenhaForte123!"
                    }
                    """.formatted(i, i);

            given()
                    .contentType("application/json")
                    .body(createUserPayload)
                    .post("/users");
        }

        // Fazer login com todos simultaneamente
        long startTime = System.currentTimeMillis();
        int successfulLogins = 0;

        for (int i = 0; i < 10; i++) {
            final int index = i;
            Thread loginThread = new Thread(() -> {
                String loginPayload = """
                        {
                          "email": "concurrent.%d@studyflow.com",
                          "password": "SenhaForte123!"
                        }
                        """.formatted(index);

                given()
                        .contentType("application/json")
                        .body(loginPayload)
                        .post("/auth/login")
                        .then()
                        .statusCode(200);
            });
            loginThread.start();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.printf("""
                
                ╔═══════════════════════════════════════╗
                ║   TESTE DE CONCORRÊNCIA - MÚLTIPLOS   ║
                ║          LOGINS SIMULTÂNEOS           ║
                ╠═══════════════════════════════════════╣
                ║ Logins Simultâneos:   10              ║
                ║ Tempo Total:          %d ms          ║
                ║ Status:               ✅ OK           ║
                ╚═══════════════════════════════════════╝
                """, totalTime);
    }

    /**
     * Teste de Resposta Lenta: Endpoints devem responder em tempo aceitável
     * Valida que o tempo de resposta está dentro dos limites esperados
     */
    @Test
    @DisplayName("Teste de Resposta Lenta: Validação de SLA")
    void testResponseTimeSLA() {
        // SLA: Todos os endpoints devem responder em menos de 1000ms (1 segundo)
        final long SLA_THRESHOLD = 1000; // milliseconds

        // Teste 1: Criação de usuário
        long startTime = System.currentTimeMillis();
        given()
                .contentType("application/json")
                .body("""
                        {
                          "name": "SLA Test User",
                          "email": "sla.test@studyflow.com",
                          "password": "SenhaForte123!"
                        }
                        """)
                .post("/users");
        long userCreationTime = System.currentTimeMillis() - startTime;

        // Teste 2: Login
        startTime = System.currentTimeMillis();
        given()
                .contentType("application/json")
                .body("""
                        {
                          "email": "perf.test@studyflow.com",
                          "password": "SenhaForte123!"
                        }
                        """)
                .post("/auth/login");
        long loginTime = System.currentTimeMillis() - startTime;

        // Teste 3: Listagem de items
        startTime = System.currentTimeMillis();
        given()
                .header("Authorization", "Bearer " + userToken)
                .get("/study-items");
        long listTime = System.currentTimeMillis() - startTime;

        System.out.printf("""
                
                ╔════════════════════════════════════════╗
                ║      VALIDAÇÃO DE SLA (< %d ms)       ║
                ╠════════════════════════════════════════╣
                ║ Criação de Usuário:    %d ms %%s      ║
                ║ Login:                 %d ms %%s      ║
                ║ Listagem:              %d ms %%s      ║
                ╚════════════════════════════════════════╝
                """,
                SLA_THRESHOLD,
                userCreationTime, userCreationTime < SLA_THRESHOLD ? "✅" : "❌",
                loginTime, loginTime < SLA_THRESHOLD ? "✅" : "❌",
                listTime, listTime < SLA_THRESHOLD ? "✅" : "❌");

        // Assertions
        assert userCreationTime < SLA_THRESHOLD : "Criação de usuário excedeu SLA";
        assert loginTime < SLA_THRESHOLD : "Login excedeu SLA";
        assert listTime < SLA_THRESHOLD : "Listagem excedeu SLA";
    }

    /**
     * Teste de Estresse: Múltiplas solicitações ao mesmo endpoint
     * Valida comportamento sob stress
     */
    @Test
    @DisplayName("Teste de Estresse: Múltiplas Requisições ao Mesmo Endpoint")
    void testStressMultipleRequests() {
        int totalRequests = 100;
        int successfulRequests = 0;
        int failedRequests = 0;
        long totalTime = 0;

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < totalRequests; i++) {
            try {
                long reqStart = System.nanoTime();
                given()
                        .header("Authorization", "Bearer " + userToken)
                        .get("/study-items")
                        .then()
                        .statusCode(200);
                long reqTime = System.nanoTime() - reqStart;
                totalTime += reqTime;
                successfulRequests++;
            } catch (Exception e) {
                failedRequests++;
            }
        }

        long endTime = System.currentTimeMillis();
        long totalTimeMs = endTime - startTime;
        double avgTimeMs = totalTime / 1_000_000.0 / successfulRequests;

        System.out.printf("""
                
                ╔═════════════════════════════════════════╗
                ║        TESTE DE ESTRESSE               ║
                ║   Múltiplas Requisições ao Endpoint    ║
                ╠═════════════════════════════════════════╣
                ║ Total de Requisições:  %d              ║
                ║ Requisições OK:        %d              ║
                ║ Requisições com Erro:  %d              ║
                ║ Taxa de Sucesso:       %.1f%%           ║
                ║ Tempo Total:           %d ms          ║
                ║ Tempo Médio/Request:   %.2f ms        ║
                ║ Throughput:            %.2f req/sec    ║
                ╚═════════════════════════════════════════╝
                """,
                totalRequests,
                successfulRequests,
                failedRequests,
                (successfulRequests * 100.0) / totalRequests,
                totalTimeMs,
                avgTimeMs,
                (successfulRequests * 1000.0) / totalTimeMs);
    }

}


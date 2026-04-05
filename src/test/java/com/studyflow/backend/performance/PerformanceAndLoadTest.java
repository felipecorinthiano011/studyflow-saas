package com.studyflow.backend.performance;
import com.studyflow.backend.support.AbstractRestAssuredTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Testes de Performance e Carga")
class PerformanceAndLoadTest extends AbstractRestAssuredTest {
    @Test
    @DisplayName("Carga: Multiplos logins simultaneos")
    void testConcurrentLoginRequests() throws InterruptedException {
        int threads = 10, requestsPerThread = 5;
        createUserAndGetToken("Performance Test User", "perf.test@email.com", "senha123456");
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        AtomicInteger success = new AtomicInteger(0);
        for (int i = 0; i < threads; i++) {
            executor.execute(() -> {
                try {
                    for (int j = 0; j < requestsPerThread; j++) {
                        long start = System.currentTimeMillis();
                        Response r = given().contentType("application/json")
                                .body("{\"email\":\"perf.test@email.com\",\"password\":\"senha123456\"}")
                                .post("/auth/login");
                        if (r.statusCode() == 200 && (System.currentTimeMillis() - start) < 1000) {
                            success.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();
        int expected = (int) (threads * requestsPerThread * 0.8);
        assertTrue(success.get() >= expected,
                "Sucesso em carga: " + success.get() + " >= " + expected);
    }
    @Test
    @DisplayName("Carga: Multiplas criações de Study Items")
    void testConcurrentCreateStudyItems() throws InterruptedException {
        int threads = 5, itemsPerThread = 10;
        String token = createUserAndGetToken("Items Test User", "items.perf@email.com", "senha123456");
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        AtomicInteger success = new AtomicInteger(0);
        for (int i = 0; i < threads; i++) {
            final int ti = i;
            executor.execute(() -> {
                try {
                    for (int j = 0; j < itemsPerThread; j++) {
                        long start = System.currentTimeMillis();
                        Response r = given().contentType("application/json")
                                .header("Authorization", "Bearer " + token)
                                .body("{\"title\":\"Item T" + ti + "-I" + j + "\",\"description\":\"Perf item thread " + ti + "\"}")
                                .post("/study-items");
                        if (r.statusCode() == 200 && (System.currentTimeMillis() - start) < 500) {
                            success.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();
        int expected = (int) (threads * itemsPerThread * 0.8);
        assertTrue(success.get() >= expected,
                "Sucesso na criacao de items: " + success.get() + " >= " + expected);
    }
    @Test
    @DisplayName("Desempenho: Tempo de resposta de GET /users/me")
    void testGetUsersResponseTime() {
        String token = createUserAndGetToken("Response Test User", "response.test@email.com", "senha123456");
        long start = System.currentTimeMillis();
        given().header("Authorization", "Bearer " + token)
                .get("/users/me")
                .then().statusCode(200).body("email", notNullValue());
        long duration = System.currentTimeMillis() - start;
        assertTrue(duration < 1000,
                "Response time should be <1s, but was " + duration + "ms");
    }
    @Test
    @DisplayName("Desempenho: Tempo de resposta de POST /study-items")
    void testCreateStudyItemResponseTime() {
        String token = createUserAndGetToken("Item Response User", "item.response@email.com", "senha123456");
        long start = System.currentTimeMillis();
        given().contentType("application/json")
                .header("Authorization", "Bearer " + token)
                .body("{\"title\":\"Performance Item\",\"description\":\"Testing response time\"}")
                .post("/study-items")
                .then().statusCode(200).body("id", notNullValue());
        long duration = System.currentTimeMillis() - start;
        assertTrue(duration < 1000,
                "Response time should be <1s, but was " + duration + "ms");
    }
    @Test
    @DisplayName("Desempenho: Autenticacao JWT")
    void testJwtAuthenticationPerformance() {
        String token = createUserAndGetToken("JWT Perf Test", "jwt.perf@email.com", "senha123456");
        long start = System.currentTimeMillis();
        for (int i = 0; i < 50; i++) {
            given().header("Authorization", "Bearer " + token).get("/users/me");
        }
        long avgValidation = (System.currentTimeMillis() - start) / 50;
        assertTrue(avgValidation < 100,
                "Avg validation should be <100ms, but was " + avgValidation + "ms");
    }
}
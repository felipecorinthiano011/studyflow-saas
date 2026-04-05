package com.studyflow.backend.support;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;

/**
 * Base class for RestAssured-based integration and performance tests.
 * Provides shared setup and helper methods, eliminating the duplicated
 * "create user + login + extract token" pattern that appeared in every test class.
 */
public abstract class AbstractRestAssuredTest {

    @LocalServerPort
    protected int port;

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
        RestAssured.basePath = "";
    }

    /**
     * Creates a user via POST /users and returns the JWT token from POST /auth/login.
     */
    protected String createUserAndGetToken(String name, String email, String password) {
        String createBody = """
                {"name": "%s", "email": "%s", "password": "%s"}
                """.formatted(name, email, password);

        given().contentType("application/json").body(createBody).post("/users");

        return loginAndGetToken(email, password);
    }

    /**
     * Logs in an existing user and returns the JWT token.
     */
    protected String loginAndGetToken(String email, String password) {
        String loginBody = """
                {"email": "%s", "password": "%s"}
                """.formatted(email, password);

        Response loginResponse = given()
                .contentType("application/json")
                .body(loginBody)
                .post("/auth/login");

        return loginResponse.jsonPath().getString("token");
    }
}


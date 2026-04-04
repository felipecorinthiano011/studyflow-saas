# StudyFlow Backend — Architecture

## Overview

REST API built with **Java 21 + Spring Boot 3** following a domain-driven package structure.  
Authentication via **JWT**. Database: **PostgreSQL** (prod) / **H2** (tests).

---

## Package Structure

```
com.studyflow.backend/
├── domain/
│   ├── user/
│   │   ├── controller/   UserController.java
│   │   ├── entity/       User.java
│   │   ├── repository/   UserRepository.java
│   │   └── service/      UserService.java
│   └── study/
│       ├── controller/   StudyItemController.java
│       ├── entity/       StudyItem.java
│       ├── repository/   StudyItemRepository.java
│       └── service/      StudyItemService.java
├── shared/
│   ├── dto/              Request/Response DTOs
│   └── exception/        GlobalExceptionHandler, custom exceptions
├── security/             JwtService, JwtAuthenticationFilter, SecurityConfig
├── service/              UserDetailsServiceImpl
└── BackendApplication.java
```

---

## API Endpoints

| Method | Path            | Auth | Description              |
|--------|-----------------|------|--------------------------|
| POST   | /users          | ✗    | Register new user        |
| POST   | /auth/login     | ✗    | Login → returns JWT      |
| GET    | /users          | ✓    | List all users           |
| GET    | /users/me       | ✓    | Get current user profile |
| POST   | /study-items    | ✓    | Create study item        |
| GET    | /study-items    | ✓    | List user's study items  |

---

## Authentication Flow

```
Client → POST /auth/login (email + password)
       ← 200 { token: "Bearer eyJ..." }

Client → GET /study-items
         Header: Authorization: Bearer <token>
       ← 200 [ ...items ]
         (403 if no token / invalid token)
```

---

## Technology Stack

| Layer        | Technology              |
|--------------|-------------------------|
| Language     | Java 21                 |
| Framework    | Spring Boot 3.3         |
| Security     | Spring Security 6 + JWT |
| Persistence  | Spring Data JPA         |
| Database     | PostgreSQL (prod)       |
| Test DB      | H2 in-memory            |
| Build        | Maven (mvnw wrapper)    |
| Container    | Docker                  |
| CI/CD        | GitHub Actions          |

---

## Test Suite (35 tests — all passing)

| Class                    | Tests | Type              |
|--------------------------|-------|-------------------|
| AuthControllerTest       | 3     | Controller / API  |
| UserControllerTest       | 5     | Controller / API  |
| StudyItemControllerTest  | 4     | Controller / API  |
| UserServiceTest          | 3     | Unit              |
| JwtServiceTest           | 3     | Unit              |
| FrontendIntegrationTest  | 8     | E2E Integration   |
| PerformanceTest          | 9     | Load / Benchmark  |

**Run tests:**
```bash
./mvnw test                                   # all 35 tests
./mvnw test -Dtest=FrontendIntegrationTest    # E2E only
./mvnw clean test jacoco:report               # with coverage
```

---

## Configuration Profiles

| Profile | Database          | Usage               |
|---------|-------------------|---------------------|
| `dev`   | PostgreSQL local  | Local development   |
| `prod`  | PostgreSQL (env)  | Production / Docker |
| `test`  | H2 in-memory      | CI/CD tests         |

All sensitive values are injected via **environment variables** — see `.env.example`.

---

## CI/CD Pipeline (GitHub Actions)

```
push/PR → main or develop
    ↓
build-and-test   → compile + 35 tests (H2)
    ↓
security-scan    → Trivy vulnerability scanner
    ↓
build-docker     → push to Docker Hub (main only)
    ↓
notify           → pipeline result summary
```

Required secrets in GitHub → Settings → Secrets:
- `DOCKER_USERNAME` / `DOCKER_PASSWORD` — Docker Hub credentials

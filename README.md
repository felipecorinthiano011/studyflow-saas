# StudyFlow Backend

> SaaS platform for study management — Java Spring Boot REST API with JWT authentication.

[![CI/CD](https://github.com/felipecorinthiano011/studyflow-saas/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/felipecorinthiano011/studyflow-saas/actions/workflows/ci-cd.yml)

| Repo | Description |
|------|-------------|
| [studyflow-saas](https://github.com/felipecorinthiano011/studyflow-saas) | Backend (this repo) |
| [studyflow-saas-frontend](https://github.com/felipecorinthiano011/studyflow-saas-frontend) | Frontend (Angular + Tailwind) |

---

## Stack

- **Java 21** · Spring Boot 3.3 · Spring Security 6
- **PostgreSQL** (production) · H2 (tests)
- **JWT** authentication · BCrypt password hashing
- **Docker** · GitHub Actions CI/CD
- **JUnit 5** · RestAssured · JMH (performance tests)

---

## Getting Started

### Prerequisites

- Java 21+
- Docker & Docker Compose
- Maven (or use the included `./mvnw` wrapper)

### 1. Clone

```bash
git clone https://github.com/felipecorinthiano011/studyflow-saas.git
cd studyflow-saas/backend
```

### 2. Configure environment

```bash
cp .env.example .env
# Edit .env and set DB_PASSWORD and JWT_SECRET
```

### 3. Run with Docker

```bash
# From the backend directory
docker-compose up -d
```

The API will be available at `http://localhost:8080`.


### 4. Run locally (without Docker)

Start PostgreSQL first, then:

```bash
export DB_PASSWORD=your_password
export JWT_SECRET=your-jwt-secret-32-chars-minimum
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## API Endpoints

| Method | Path          | Auth | Description              |
|--------|---------------|------|--------------------------|
| POST   | /users        | ✗    | Register new user        |
| POST   | /auth/login   | ✗    | Login → returns JWT      |
| GET    | /users        | ✓    | List all users           |
| GET    | /users/me     | ✓    | Get current user profile |
| POST   | /study-items  | ✓    | Create study item        |
| GET    | /study-items  | ✓    | List user's study items  |

**Authentication:** include `Authorization: Bearer <token>` header for protected endpoints.

---

## Running Tests

```bash
# All 35 tests (uses H2 in-memory — no DB required)
./mvnw test

# Specific suites
./mvnw test -Dtest=FrontendIntegrationTest   # 8 E2E tests
./mvnw test -Dtest=PerformanceTest           # 9 load/benchmark tests

# With coverage report
./mvnw clean test jacoco:report
# Open: target/site/jacoco/index.html

# Script (Windows)
.\run-tests.bat

# Script (Linux/Mac)
chmod +x run-tests.sh && ./run-tests.sh
```

### Test Suite (35 tests — all passing ✅)

| Class                   | Tests | Type              |
|-------------------------|-------|-------------------|
| AuthControllerTest      | 3     | Controller / API  |
| UserControllerTest      | 5     | Controller / API  |
| StudyItemControllerTest | 4     | Controller / API  |
| UserServiceTest         | 3     | Unit              |
| JwtServiceTest          | 3     | Unit              |
| FrontendIntegrationTest | 8     | E2E Integration   |
| PerformanceTest         | 9     | Load / Benchmark  |

---

## CI/CD Pipeline

On every push/PR to `main` or `develop`:

1. **Build & Test** — compile + run all 35 tests against H2
2. **Security Scan** — Trivy vulnerability scanner
3. **Docker Build & Push** — push image to Docker Hub *(main branch only)*
4. **Notify** — print pipeline summary

**Required GitHub Secrets:**

| Secret            | Description                  |
|-------------------|------------------------------|
| `DOCKER_USERNAME` | Docker Hub username          |
| `DOCKER_PASSWORD` | Docker Hub access token      |
| `SONAR_TOKEN`     | SonarCloud (optional)        |
| `SLACK_WEBHOOK`   | Slack notifications (optional)|

---

## Project Structure

```
backend/
├── src/
│   ├── main/java/com/studyflow/backend/
│   │   ├── domain/user/         # User entity, repo, service, controller
│   │   ├── domain/study/        # StudyItem entity, repo, service, controller
│   │   ├── shared/dto/          # Request/Response DTOs
│   │   ├── shared/exception/    # GlobalExceptionHandler
│   │   └── security/            # JWT filter, service, config
│   └── test/java/com/studyflow/backend/
│       ├── controller/          # Controller unit tests
│       ├── service/             # Service unit tests
│       ├── security/            # JWT unit tests
│       ├── integration/         # E2E integration tests
│       └── performance/         # Load & benchmark tests
├── .env.example                 # Environment variables template
├── docker-compose.yml           # Local dev environment
├── Dockerfile                   # Multi-stage production build
├── ARCHITECTURE.md              # Detailed architecture docs
└── pom.xml
```

---

## Security

- Passwords hashed with **BCrypt**
- JWT tokens validated on every protected request
- All credentials loaded from **environment variables** — no secrets in source code
- Copy `.env.example` → `.env` and set values before running
- Production profile uses only `${ENV_VAR}` references with no defaults

---

## License

MIT

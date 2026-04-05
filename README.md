# StudyFlow SaaS

> Full-stack SaaS platform for study management — Java Spring Boot REST API + Angular frontend with JWT authentication, Redis cache, rate limiting and audit logging.

[![CI/CD](https://github.com/felipecorinthiano011/studyflow-saas/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/felipecorinthiano011/studyflow-saas/actions/workflows/ci-cd.yml)

| Part | Repository | URL |
|------|-----------|-----|
| **Backend** | [studyflow-saas](https://github.com/felipecorinthiano011/studyflow-saas) | https://studyflow-saas-production.up.railway.app |
| **Frontend** | [studyflow-saas-frontend](https://github.com/felipecorinthiano011/studyflow-saas-frontend) | https://studyflow-saas-frontend.vercel.app |

---

## Tech Stack

### Backend
| Technology | Purpose |
|---|---|
| Java 21 + Spring Boot 3.3 | Core framework |
| Spring Security 6 + JWT | Authentication & authorisation |
| PostgreSQL | Production database |
| H2 | In-memory database for tests |
| Redis | Distributed cache (`@Cacheable`) |
| Bucket4j | Rate limiting — 60 req/min per IP |
| Spring Events + Audit Log | Decoupled event-driven architecture |
| Spring Actuator | Health check endpoint (`/actuator/health`) |
| Sentry | Error monitoring & tracing |
| SonarCloud + Qodana | Code quality analysis |

### Frontend
| Technology | Purpose |
|---|---|
| Angular 21 (standalone) | SPA framework |
| NgRx Signal Store | Reactive state management |
| Tailwind CSS v4 | Utility-first styling |
| @sentry/angular | Frontend error monitoring |
| Cypress | E2E testing |

### DevOps
| Technology | Purpose |
|---|---|
| GitHub Actions | CI/CD pipelines |
| Railway | Backend + Redis hosting |
| Vercel | Frontend hosting |
| Docker | Containerisation |
| Terraform | Infrastructure as Code |

---

## Getting Started

### Prerequisites
- Java 21+
- Docker & Docker Compose
- Maven (or use the included `./mvnw` wrapper)

### 1. Clone & configure

```bash
git clone https://github.com/felipecorinthiano011/studyflow-saas.git
cd studyflow-saas
cp .env.example .env
# Edit .env — set JWT_SECRET (minimum 32 characters)
```

### 2. Run the backend with Docker

```bash
docker-compose up -d
# Backend → http://localhost:8080
# Redis   → localhost:6379
```

### 3. Run the frontend

```bash
cd frontend
npm install
npx ng serve
# → http://localhost:4200
```

### 4. Run locally without Docker

Start PostgreSQL first, then from the `backend/` directory:

```bash
export JWT_SECRET=your-secret-minimum-32-chars
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## API Reference

All protected endpoints require the header:
```
Authorization: Bearer <jwt-token>
```

| Method | Path | Auth | Description |
|--------|------|:----:|-------------|
| `POST` | `/users` | ✗ | Register new user |
| `POST` | `/auth/login` | ✗ | Login → returns JWT |
| `GET` | `/users` | ✔ | List all users |
| `GET` | `/users/me` | ✔ | Current user profile |
| `POST` | `/study-items` | ✔ | Create study item |
| `GET` | `/study-items?page=0&size=20` | ✔ | List study items (paginated) |
| `PUT` | `/study-items/{id}` | ✔ | Update study item |
| `DELETE` | `/study-items/{id}` | ✔ | Delete study item |
| `GET` | `/actuator/health` | ✗ | Health check |

**Paginated response format:**
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 42,
  "totalPages": 3,
  "last": false
}
```

---

## Running Tests

From the `backend/` directory:

```bash
# All tests (H2 in-memory — no database required)
./mvnw test

# With JaCoCo coverage report
./mvnw clean test jacoco:report
# → target/site/jacoco/index.html

# Specific suites
./mvnw test -Dtest=FrontendIntegrationTest   # integration only
./mvnw test -Dtest=PerformanceAndLoadTest    # performance only

# Script helpers (Windows)
.\run-tests.bat

# Script helpers (Linux / macOS)
chmod +x run-tests.sh && ./run-tests.sh
```

### Test Suite (67 tests — all passing ✅)

| Class | Tests | Type |
|---|:---:|---|
| `AuthControllerTest` | 3 | Controller / API |
| `UserControllerTest` | 5 | Controller / API |
| `StudyItemControllerTest` | 5 | Controller / API |
| `UserServiceTest` | 4 | Unit |
| `StudyItemServiceTest` | 7 | Unit |
| `UserRepositoryTest` | 4 | Repository |
| `StudyItemRepositoryTest` | 5 | Repository |
| `JwtServiceTest` | 4 | Unit |
| `JwtAuthenticationFilterTest` | 5 | Unit |
| `UserDetailsServiceImplTest` | 2 | Unit |
| `GlobalExceptionHandlerTest` | 5 | Unit |
| `FrontendIntegrationTest` | 8 | E2E Integration |
| `PerformanceTest` + `PerformanceAndLoadTest` | 10 | Load / Benchmark |

---

## CI/CD Pipeline

| Workflow | Trigger | What it does |
|---|---|---|
| `ci-cd.yml` | push / PR to `main` or `develop` | Compile → 67 tests → Trivy security scan → Docker build & push |
| `deploy.yml` | push to `main` | Deploy to Railway production |

### Required GitHub Secrets

| Secret | Description |
|--------|-------------|
| `DOCKER_USERNAME` | Docker Hub username |
| `DOCKER_PASSWORD` | Docker Hub access token |
| `RAILWAY_TOKEN` | Railway API token |
| `SONAR_TOKEN` | SonarCloud token (optional) |

---

## Key Features

### Backend
- ✅ **JWT Authentication** — stateless, validated on every protected request
- ✅ **Pagination** — `PageResponseDTO<T>` on `GET /study-items`
- ✅ **Redis Cache** — `@Cacheable` on list queries, `@CacheEvict` on mutations
- ✅ **Rate Limiting** — Bucket4j, 60 requests/min per IP
- ✅ **Audit Logging** — `AuditLog` entity persisted via Spring Events (decoupled)
- ✅ **Multi-tenancy** — `Organization` entity with nullable FK on User & StudyItem
- ✅ **Error Monitoring** — Sentry auto-captures exceptions per environment
- ✅ **Code Quality** — SonarCloud + Qodana gates on every push

### Frontend
- ✅ **NgRx Signal Store** — reactive state with optimistic updates + pagination
- ✅ **Global Error Handling** — `GlobalErrorHandler` + `HttpErrorInterceptor` → toast messages
- ✅ **Logged-in user display** — username always visible in the navbar
- ✅ **Cypress E2E** — register, login, create-item and delete-item specs
- ✅ **Sentry Angular** — browserTracing + session replay

---

## Infrastructure as Code (Terraform)

```bash
cd terraform
cp terraform.tfvars.example terraform.tfvars
# Fill in values for Railway and Vercel tokens
terraform init
terraform plan
terraform apply
```

Provisions: Railway project (production environment), PostgreSQL, Redis, backend service, and Vercel frontend project.

---

## Security

- Passwords hashed with **BCrypt**
- JWT tokens validated on every protected request via `JwtAuthenticationFilter`
- All secrets injected via **environment variables** — no credentials in source code
- `.env` and `terraform.tfvars` excluded from git via `.gitignore`
- Copy `.env.example` → `.env` before running locally

---

## Project Structure

```
studyflow-saas/
├── backend/                     # Spring Boot application
│   ├── src/main/java/com/studyflow/backend/
│   │   ├── domain/
│   │   │   ├── audit/           # AuditLog entity, repo, service
│   │   │   ├── organization/    # Organization entity (multi-tenancy)
│   │   │   ├── study/           # StudyItem entity, repo, service, controller, listener
│   │   │   └── user/            # User entity, repo, service, controller
│   │   ├── security/            # JwtService, JwtAuthenticationFilter, SecurityConfig
│   │   ├── shared/
│   │   │   ├── constant/        # ErrorMessages, ValidationPatterns
│   │   │   ├── dto/             # Request / Response DTOs
│   │   │   ├── event/           # Spring application events
│   │   │   ├── exception/       # GlobalExceptionHandler, custom exceptions
│   │   │   └── util/            # Utility classes
│   │   ├── common/              # Annotations, helpers, mappers
│   │   ├── config/              # OpenAPI, Redis, Sentry configuration
│   │   └── service/             # UserDetailsServiceImpl
│   ├── .env.example             # Environment variables template
│   ├── docker-compose.yml       # Local dev stack (API + PostgreSQL + Redis)
│   ├── Dockerfile               # Multi-stage production build
│   └── pom.xml
├── frontend/                    # Angular 21 SPA
├── terraform/                   # IaC — Railway + Vercel
├── .github/workflows/           # CI/CD pipelines
├── run-tests.bat                # Test runner (Windows)
└── run-tests.sh                 # Test runner (Linux / macOS)
```

---

## License

MIT

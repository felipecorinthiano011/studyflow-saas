# StudyFlow — Architecture

## Overview

REST API built with **Java 21 + Spring Boot 3.3** following a Domain-Driven Design (DDD) package structure.
Authentication via **stateless JWT**. Database: **PostgreSQL** (production) / **H2** (tests).
Cache layer: **Redis** (`@Cacheable`). Rate limiting via **Bucket4j**.

---

## Package Structure

```
com.studyflow.backend/
├── domain/
│   ├── audit/
│   │   ├── entity/       AuditLog.java
│   │   ├── repository/   AuditLogRepository.java
│   │   └── service/      AuditLogService.java
│   ├── organization/
│   │   ├── entity/       Organization.java
│   │   ├── repository/   OrganizationRepository.java
│   │   └── runner/       OrganizationDataRunner.java
│   ├── study/
│   │   ├── controller/   StudyItemController.java
│   │   ├── entity/       StudyItem.java
│   │   ├── listener/     StudyItemEventListener.java
│   │   ├── repository/   StudyItemRepository.java
│   │   └── service/      StudyItemService.java
│   └── user/
│       ├── controller/   UserController.java
│       ├── entity/       User.java
│       ├── repository/   UserRepository.java
│       └── service/      UserService.java
├── security/
│   ├── JwtAuthenticationFilter.java
│   ├── JwtService.java
│   └── SecurityConfig.java
├── shared/
│   ├── constant/         ErrorMessages.java, ValidationPatterns.java
│   ├── dto/              Request / Response DTOs
│   ├── event/            StudyItemCreatedEvent, UpdatedEvent, DeletedEvent
│   ├── exception/        GlobalExceptionHandler, custom exceptions
│   └── util/             Utility classes
├── common/
│   ├── annotation/       Custom annotations
│   ├── helper/           Helper classes
│   └── mapper/           Object mappers
├── config/               OpenApiConfig, RedisConfig, SentryConfig
├── controller/           AuthController
└── service/              UserDetailsServiceImpl
```

---

## API Endpoints

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

---

## Authentication Flow

```
Client → POST /auth/login { email, password }
       ← 200 { token: "eyJ..." }

Client → GET /study-items
         Header: Authorization: Bearer <token>
       ← 200 { content: [...], page: 0, totalElements: N }
         (401 if token missing / 403 if token invalid)
```

`JwtAuthenticationFilter` intercepts every request, validates the token and loads the `UserDetails` into the `SecurityContext`.

---

## Event-Driven Audit Logging

```
StudyItemService.create()
    → applicationEventPublisher.publishEvent(StudyItemCreatedEvent)
         → StudyItemEventListener.onCreated()
              → AuditLogService.logAction()
                   → AuditLog persisted to DB
```

Events are synchronous Spring application events. The listener is decoupled from the service layer.

---

## Technology Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3 |
| Security | Spring Security 6 + JWT (jjwt) |
| Persistence | Spring Data JPA + Hibernate |
| Cache | Redis + Spring Cache (`@Cacheable` / `@CacheEvict`) |
| Rate Limiting | Bucket4j (60 req/min per IP) |
| Database (prod) | PostgreSQL |
| Database (test) | H2 in-memory |
| Build | Maven (mvnw wrapper) |
| Container | Docker (multi-stage build) |
| CI/CD | GitHub Actions |
| Hosting | Railway (backend) + Vercel (frontend) |
| Monitoring | Sentry |
| Code Quality | SonarCloud + Qodana |

---

## Configuration Profiles

| Profile | Database | Usage |
|---|---|---|
| `dev` | PostgreSQL (local) | Local development |
| `prod` | PostgreSQL (env vars) | Production / Docker |
| `test` | H2 in-memory | CI/CD / unit tests |
| `staging` | PostgreSQL (env vars) | Staging environment |

All sensitive values (JWT secret, DB credentials, Sentry DSN) are injected via **environment variables**.  
See `.env.example` for the full list.

---

## CI/CD Pipelines

```
push / PR → main or develop
    │
    ├─ build-and-test   compile + 67 tests (H2, no DB required)
    ├─ security-scan    Trivy vulnerability scanner
    ├─ build-docker     push image to Docker Hub  (main branch only)
    └─ deploy           Railway deploy             (main branch only)
```

| Workflow | File |
|---|---|
| Build, test, scan, Docker push | `.github/workflows/ci-cd.yml` |
| Railway production deploy | `.github/workflows/deploy.yml` |

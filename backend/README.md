# StudyFlow — Backend

> Spring Boot 3.3 REST API with JWT authentication, Redis cache and PostgreSQL.  
> Part of the [StudyFlow SaaS](https://github.com/felipecorinthiano011/studyflow-saas) project — see the root README for full documentation.

**Production:** https://studyflow-saas-production.up.railway.app  
**API Docs (Swagger):** https://studyflow-saas-production.up.railway.app/swagger-ui.html

---

## Quick Start

### Prerequisites
- Java 21+
- Docker & Docker Compose (or a local PostgreSQL + Redis instance)

### 1. Configure environment

```bash
cp .env.example .env
# Edit .env — set JWT_SECRET (minimum 32 characters)
```

### 2. Run with Docker

```bash
docker-compose up -d
# API   → http://localhost:8080
# Redis → localhost:6379
```

### 3. Run locally (without Docker)

Start PostgreSQL and Redis first, then:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Running Tests

```bash
# All 67 tests (H2 in-memory — no database required)
./mvnw test

# With JaCoCo coverage report
./mvnw clean test jacoco:report
# Open: target/site/jacoco/index.html

# Windows script
.\run-tests.bat

# Linux / macOS script
chmod +x run-tests.sh && ./run-tests.sh
```

---

## Environment Variables

| Variable | Required | Description |
|---|:---:|---|
| `JWT_SECRET` | ✔ | Secret key — minimum 32 characters |
| `DB_URL` | ✔ (prod) | PostgreSQL JDBC URL |
| `DB_USERNAME` | ✔ (prod) | Database username |
| `DB_PASSWORD` | ✔ (prod) | Database password |
| `REDIS_URL` | ✗ | Redis connection URL (defaults to localhost) |
| `SENTRY_DSN` | ✗ | Sentry error tracking DSN |

---

For architecture details, CI/CD setup, API reference and full feature list see the [root README](../README.md).

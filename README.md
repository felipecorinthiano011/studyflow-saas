# StudyFlow SaaS
> Full-stack SaaS platform for study management — Java Spring Boot REST API + Angular 21 frontend with JWT authentication.
[![CI/CD](https://github.com/felipecorinthiano011/studyflow-saas/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/felipecorinthiano011/studyflow-saas/actions/workflows/ci-cd.yml)
[![Deploy Staging](https://github.com/felipecorinthiano011/studyflow-saas/actions/workflows/deploy-staging.yml/badge.svg)](https://github.com/felipecorinthiano011/studyflow-saas/actions/workflows/deploy-staging.yml)
[![Deploy Production](https://github.com/felipecorinthiano011/studyflow-saas/actions/workflows/deploy-prod.yml/badge.svg)](https://github.com/felipecorinthiano011/studyflow-saas/actions/workflows/deploy-prod.yml)
## Tech Stack
### Backend
| Technology | Purpose |
|---|---|
| Java 21 + Spring Boot 3.3 | Core framework |
| Spring Security 6 + JWT | Authentication & authorisation |
| PostgreSQL | Production database |
| H2 | In-memory database for tests |
| Redis | Distributed cache (`@Cacheable`) |
| Bucket4j | Rate limiting (60 req/min per IP) |
| Spring Events + Audit Log | Decoupled event-driven architecture |
| Spring Actuator | Health check endpoint |
| Sentry | Error monitoring & tracing |
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
| Terraform | Infrastructure as Code |
---
## Getting Started
### 1. Clone & configure
```bash
git clone https://github.com/felipecorinthiano011/studyflow-saas.git
cd studyflow-saas
cp .env.example .env
# Edit .env — set JWT_SECRET (min 32 chars)
```
### 2. Run with Docker (full stack)
```bash
docker-compose up -d
# Backend → http://localhost:8080
# Redis   → localhost:6379
```
### 3. Run frontend
```bash
cd frontend && npm install && npm start
# → http://localhost:4200
```
---
## API Reference
| Method | Path | Auth | Description |
|--------|------|------|-------------|
| `POST` | `/users` | ✗ | Register new user |
| `POST` | `/auth/login` | ✗ | Login → returns JWT |
| `GET` | `/users` | ✔ | List all users |
| `GET` | `/users/me` | ✔ | Get current user profile |
| `POST` | `/study-items` | ✔ | Create study item |
| `GET` | `/study-items?page=0&size=20` | ✔ | List study items (paginated) |
| `PUT` | `/study-items/{id}` | ✔ | Update study item |
| `DELETE` | `/study-items/{id}` | ✔ | Delete study item |
| `GET` | `/actuator/health` | ✗ | Health check |
Paginated response:
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
```bash
# All 67 tests (H2 in-memory)
cd backend && ./mvnw test
# With coverage (JaCoCo)
./mvnw clean test jacoco:report
# → target/site/jacoco/index.html
```
### Test Suite (67 tests — all passing ✅)
| Class | Tests |
|---|---|
| AuthControllerTest | 3 |
| UserControllerTest | 5 |
| StudyItemControllerTest | 5 |
| UserServiceTest | 4 |
| StudyItemServiceTest | 7 |
| UserRepositoryTest | 4 |
| StudyItemRepositoryTest | 5 |
| JwtServiceTest | 4 |
| JwtAuthenticationFilterTest | 5 |
| UserDetailsServiceImplTest | 2 |
| GlobalExceptionHandlerTest | 5 |
| FrontendIntegrationTest | 8 |
| PerformanceTest + PerformanceAndLoadTest | 10 |
---
## CI/CD Pipelines
| Workflow | Trigger | What it does |
|---|---|---|
| `ci-cd.yml` | push/PR to main or develop | Build, test, Trivy scan, Docker push |
| `deploy-staging.yml` | push to develop | Tests → Docker → Railway staging → health check |
| `deploy-prod.yml` | push to main / release | Tests → Docker → **blue-green deploy** → Vercel → Sentry release |
### Blue-Green Deploy (deploy-prod.yml)
1. Reads `ACTIVE_SLOT` variable (`blue` or `green`)
2. Deploys new image to the **standby** slot
3. Polls `/actuator/health` up to 8 times (120s)
4. On success → updates `ACTIVE_SLOT` to point traffic to new slot
5. On failure → keeps old slot live (automatic rollback)
### Required GitHub Secrets
| Secret | Description |
|--------|-------------|
| `DOCKER_USERNAME` / `DOCKER_PASSWORD` | Docker Hub |
| `RAILWAY_TOKEN` | Railway API token |
| `RAILWAY_STAGING_SERVICE_ID` | Railway staging service |
| `RAILWAY_BLUE_SERVICE_ID` / `RAILWAY_GREEN_SERVICE_ID` | Blue-green slots |
| `STAGING_URL` / `STANDBY_URL` / `APP_URL` | Service URLs for health checks |
| `VERCEL_TOKEN` / `VERCEL_ORG_ID` / `VERCEL_PROJECT_ID` | Vercel |
| `SENTRY_AUTH_TOKEN` / `SENTRY_ORG` / `SENTRY_PROJECT` / `SENTRY_DSN` | Sentry (optional) |
| `GH_PAT` | PAT with `repo` scope (to update ACTIVE_SLOT variable) |
### Required GitHub Variables
| Variable | Value |
|---|---|
| `ACTIVE_SLOT` | `blue` or `green` |
---
## Infrastructure as Code (Terraform)
```bash
cd terraform
cp terraform.tfvars.example terraform.tfvars
# Fill in real values
terraform init
terraform plan
terraform apply
```
Provisions: Railway project (production + staging envs), PostgreSQL, Redis, backend blue/green/staging services, and Vercel frontend project.
---
## Key Features
### Backend
- ✅ **Pagination** — `PageResponseDTO<T>` on `GET /study-items`
- ✅ **Redis Cache** — `@Cacheable` on list, `@CacheEvict` on mutations
- ✅ **Rate Limiting** — Bucket4j 60 req/min per IP
- ✅ **Audit Logging** — `AuditLog` entity via Spring Events
- ✅ **Multi-tenancy** — `Organization` entity with nullable FK on User & StudyItem
- ✅ **Sentry** — auto-captures exceptions per environment
### Frontend
- ✅ **NgRx Signal Store** — `StudyItemsStore` with optimistic updates + pagination
- ✅ **Global Error Handling** — `GlobalErrorHandler` + `HttpErrorInterceptor` → toasts
- ✅ **Cypress E2E** — register, login, create-item, delete-item specs
- ✅ **Sentry Angular** — browserTracing + replay, disabled when DSN is empty
### DevOps
- ✅ **Staging environment** — `application-staging.properties` + `deploy-staging.yml`
- ✅ **Blue-green deploy** — automatic rollback on health check failure
- ✅ **Terraform IaC** — Railway + Vercel provisioned from code
---
## Security
- BCrypt password hashing
- JWT validated on every protected request
- All secrets via environment variables — no hardcoded credentials
- `terraform.tfvars` excluded from git
---
## License
MIT
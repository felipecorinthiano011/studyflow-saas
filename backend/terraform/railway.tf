# ── Railway Project ─────────────────────────────────────────────────────────────
resource "railway_project" "studyflow" {
  name = var.railway_project_name
}

# ── Environments ────────────────────────────────────────────────────────────────
resource "railway_environment" "production" {
  name       = "production"
  project_id = railway_project.studyflow.id
}

resource "railway_environment" "staging" {
  name       = "staging"
  project_id = railway_project.studyflow.id
}

# ── PostgreSQL — Production ──────────────────────────────────────────────────────
resource "railway_service" "postgres_prod" {
  name       = "postgres-prod"
  project_id = railway_project.studyflow.id
  source     = { image = "postgres:16-alpine" }
}

resource "railway_variable" "postgres_prod_password" {
  project_id     = railway_project.studyflow.id
  environment_id = railway_environment.production.id
  service_id     = railway_service.postgres_prod.id
  name           = "POSTGRES_PASSWORD"
  value          = var.jwt_secret # reusing secret for simplicity; use separate var in real projects
}

# ── PostgreSQL — Staging ─────────────────────────────────────────────────────────
resource "railway_service" "postgres_staging" {
  name       = "postgres-staging"
  project_id = railway_project.studyflow.id
  source     = { image = "postgres:16-alpine" }
}

# ── Redis — Production ───────────────────────────────────────────────────────────
resource "railway_service" "redis_prod" {
  name       = "redis-prod"
  project_id = railway_project.studyflow.id
  source     = { image = "redis:7-alpine" }
}

# ── Redis — Staging ──────────────────────────────────────────────────────────────
resource "railway_service" "redis_staging" {
  name       = "redis-staging"
  project_id = railway_project.studyflow.id
  source     = { image = "redis:7-alpine" }
}

# ── Backend Blue (production active slot) ────────────────────────────────────────
resource "railway_service" "backend_blue" {
  name       = "backend-blue"
  project_id = railway_project.studyflow.id
  source = {
    repo   = var.github_repo
    branch = "main"
  }
}

resource "railway_variable" "backend_blue_profile" {
  project_id     = railway_project.studyflow.id
  environment_id = railway_environment.production.id
  service_id     = railway_service.backend_blue.id
  name           = "SPRING_PROFILES_ACTIVE"
  value          = "prod"
}

resource "railway_variable" "backend_blue_jwt" {
  project_id     = railway_project.studyflow.id
  environment_id = railway_environment.production.id
  service_id     = railway_service.backend_blue.id
  name           = "JWT_SECRET"
  value          = var.jwt_secret
}

resource "railway_variable" "backend_blue_sentry" {
  project_id     = railway_project.studyflow.id
  environment_id = railway_environment.production.id
  service_id     = railway_service.backend_blue.id
  name           = "SENTRY_DSN"
  value          = var.sentry_dsn
}

# ── Backend Green (production standby slot) ───────────────────────────────────────
resource "railway_service" "backend_green" {
  name       = "backend-green"
  project_id = railway_project.studyflow.id
  source = {
    repo   = var.github_repo
    branch = "main"
  }
}

resource "railway_variable" "backend_green_profile" {
  project_id     = railway_project.studyflow.id
  environment_id = railway_environment.production.id
  service_id     = railway_service.backend_green.id
  name           = "SPRING_PROFILES_ACTIVE"
  value          = "prod"
}

resource "railway_variable" "backend_green_jwt" {
  project_id     = railway_project.studyflow.id
  environment_id = railway_environment.production.id
  service_id     = railway_service.backend_green.id
  name           = "JWT_SECRET"
  value          = var.jwt_secret
}

resource "railway_variable" "backend_green_sentry" {
  project_id     = railway_project.studyflow.id
  environment_id = railway_environment.production.id
  service_id     = railway_service.backend_green.id
  name           = "SENTRY_DSN"
  value          = var.sentry_dsn
}

# ── Backend Staging ───────────────────────────────────────────────────────────────
resource "railway_service" "backend_staging" {
  name       = "backend-staging"
  project_id = railway_project.studyflow.id
  source = {
    repo   = var.github_repo
    branch = "develop"
  }
}

resource "railway_variable" "backend_staging_profile" {
  project_id     = railway_project.studyflow.id
  environment_id = railway_environment.staging.id
  service_id     = railway_service.backend_staging.id
  name           = "SPRING_PROFILES_ACTIVE"
  value          = "staging"
}

resource "railway_variable" "backend_staging_jwt" {
  project_id     = railway_project.studyflow.id
  environment_id = railway_environment.staging.id
  service_id     = railway_service.backend_staging.id
  name           = "JWT_SECRET"
  value          = var.jwt_secret
}

resource "railway_variable" "backend_staging_sentry" {
  project_id     = railway_project.studyflow.id
  environment_id = railway_environment.staging.id
  service_id     = railway_service.backend_staging.id
  name           = "SENTRY_DSN"
  value          = var.sentry_dsn
}


# ── Vercel Project ───────────────────────────────────────────────────────────────
resource "vercel_project" "frontend" {
  name      = "studyflow-saas-frontend"
  framework = "angular"

  git_repository = {
    type = "github"
    repo = var.github_repo
  }

  # Angular build settings
  build_command    = "cd frontend && npm ci && npm run build"
  output_directory = "frontend/dist/frontend/browser"
  root_directory   = null
}

# ── Production Deployment Environment ────────────────────────────────────────────
resource "vercel_project_environment_variable" "api_url_prod" {
  project_id = vercel_project.frontend.id
  key        = "NG_APP_API_URL"
  value      = "https://studyflow-saas-production.up.railway.app"
  target     = ["production"]
}

resource "vercel_project_environment_variable" "sentry_dsn_prod" {
  project_id = vercel_project.frontend.id
  key        = "SENTRY_DSN"
  value      = var.sentry_dsn
  target     = ["production"]
  sensitive  = true
}

# ── Staging / Preview Environment ─────────────────────────────────────────────────
resource "vercel_project_environment_variable" "api_url_staging" {
  project_id = vercel_project.frontend.id
  key        = "NG_APP_API_URL"
  value      = "https://studyflow-saas-staging.up.railway.app"
  target     = ["preview"]
}

resource "vercel_project_environment_variable" "sentry_dsn_staging" {
  project_id = vercel_project.frontend.id
  key        = "SENTRY_DSN"
  value      = var.sentry_dsn
  target     = ["preview"]
  sensitive  = true
}

# ── Custom domain (optional) ──────────────────────────────────────────────────────
# resource "vercel_project_domain" "www" {
#   project_id = vercel_project.frontend.id
#   domain     = "www.studyflow.app"
# }


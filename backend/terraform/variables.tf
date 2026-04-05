# ── Tokens ─────────────────────────────────────────────────────────────────────
variable "railway_token" {
  description = "Railway API token"
  type        = string
  sensitive   = true
}

variable "vercel_token" {
  description = "Vercel API token"
  type        = string
  sensitive   = true
}

# ── Railway project ─────────────────────────────────────────────────────────────
variable "railway_project_name" {
  description = "Name of the Railway project"
  type        = string
  default     = "studyflow-saas"
}

# ── Environments ────────────────────────────────────────────────────────────────
variable "environments" {
  description = "Railway environment names"
  type        = list(string)
  default     = ["production", "staging"]
}

# ── Vercel ──────────────────────────────────────────────────────────────────────
variable "vercel_team_id" {
  description = "Vercel team ID (leave empty for personal account)"
  type        = string
  default     = ""
}

variable "github_repo" {
  description = "GitHub repository in format owner/repo"
  type        = string
  default     = "your-github-username/studyflow-saas"
}

# ── App secrets injected into Railway ───────────────────────────────────────────
variable "jwt_secret" {
  description = "JWT signing secret (min 32 chars)"
  type        = string
  sensitive   = true
}

variable "sentry_dsn" {
  description = "Sentry DSN for error monitoring"
  type        = string
  sensitive   = true
  default     = ""
}


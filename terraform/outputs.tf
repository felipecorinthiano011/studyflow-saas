output "railway_project_id" {
  description = "Railway project ID"
  value       = railway_project.studyflow.id
}

output "railway_production_env_id" {
  description = "Railway production environment ID"
  value       = railway_environment.production.id
}

output "railway_staging_env_id" {
  description = "Railway staging environment ID"
  value       = railway_environment.staging.id
}

output "backend_blue_service_id" {
  description = "Backend blue (active) service ID for blue-green deploy"
  value       = railway_service.backend_blue.id
}

output "backend_green_service_id" {
  description = "Backend green (standby) service ID for blue-green deploy"
  value       = railway_service.backend_green.id
}

output "backend_staging_service_id" {
  description = "Backend staging service ID"
  value       = railway_service.backend_staging.id
}

output "vercel_project_id" {
  description = "Vercel project ID"
  value       = vercel_project.frontend.id
}

output "vercel_frontend_url" {
  description = "Vercel frontend URL"
  value       = "https://${vercel_project.frontend.name}.vercel.app"
}


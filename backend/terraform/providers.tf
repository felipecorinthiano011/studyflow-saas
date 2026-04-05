terraform {
  required_version = ">= 1.6.0"

  required_providers {
    railway = {
      source  = "terraform-community-modules/railway"
      version = "~> 0.3"
    }
    vercel = {
      source  = "vercel/vercel"
      version = "~> 2.0"
    }
  }

  # Remote state in Terraform Cloud (optional — replace with your org/workspace)
  # backend "remote" {
  #   organization = "your-org"
  #   workspaces { name = "studyflow-saas" }
  # }
}

provider "railway" {
  token = var.railway_token
}

provider "vercel" {
  api_token = var.vercel_token
}


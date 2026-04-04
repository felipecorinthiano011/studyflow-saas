#!/usr/bin/env pwsh
Set-Location "C:\Projects\studyflow-saas\backend"
Write-Host "Starting Integration Tests..."
& ".\mvnw.cmd" test -Dtest=FrontendIntegrationTest --batch-mode
Write-Host "Integration Tests Completed!"


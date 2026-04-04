@echo off
setlocal enabledelayedexpansion
cd /d "C:\Projects\studyflow-saas\backend"
echo Testing directory change...
echo Current directory: %cd%
echo.
echo Running Maven tests...
call mvnw.cmd test -Dtest=FrontendIntegrationTest -X > test_results.txt 2>&1
echo Test execution completed - check test_results.txt


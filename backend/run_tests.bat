@echo off
cd C:\Projects\studyflow-saas\backend
echo Running Integration Tests...
call mvnw.cmd test -Dtest=FrontendIntegrationTest --batch-mode
echo Tests Completed!


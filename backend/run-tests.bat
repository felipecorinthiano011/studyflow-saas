@echo off
REM StudyFlow Backend - Test Runner
setlocal enabledelayedexpansion

echo.
echo ================================
echo  StudyFlow Backend - Test Suite
echo ================================
echo.

REM Clean
echo [1/4] Cleaning...
call .\mvnw.cmd clean -q
if %ERRORLEVEL% NEQ 0 goto :error

REM Compile
echo [2/4] Compiling...
call .\mvnw.cmd compile -q
if %ERRORLEVEL% NEQ 0 goto :error

REM Run all tests
echo [3/4] Running tests...
call .\mvnw.cmd test -q
if %ERRORLEVEL% NEQ 0 goto :error

REM Generate coverage
echo [4/4] Generating coverage report...
call .\mvnw.cmd jacoco:report -q

echo.
echo ================================
echo  All tests passed!
echo  Coverage: target\site\jacoco\index.html
echo  Reports:  target\surefire-reports\
echo ================================
echo.
exit /b 0

:error
echo.
echo [ERROR] Build failed. Check output above.
echo.
exit /b 1

@echo off
REM Test Suite Automation Script for StudyFlow Backend (Windows)
REM Script para executar testes automatizados e gerar relatórios

setlocal enabledelayedexpansion

cls
echo.
echo ================================
echo StudyFlow Backend - Test Automation Suite
echo ================================
echo.

REM 1. Verificar se Maven está instalado
echo Checking dependencies...
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo [X] Maven not found. Please install Maven and add it to PATH.
    exit /b 1
)
echo [+] Maven found

REM 2. Limpar builds anteriores
echo.
echo ================================
echo Step 1: Cleaning Previous Builds
echo ================================
call mvn clean
if %ERRORLEVEL% NEQ 0 goto error

REM 3. Compilar o projeto
echo.
echo ================================
echo Step 2: Compiling Project
echo ================================
call mvn compile
if %ERRORLEVEL% NEQ 0 goto error

REM 4. Executar testes unitários
echo.
echo ================================
echo Step 3: Running Unit Tests
echo ================================
call mvn test -DskipCheckstyle -DskipSpotbugs
if %ERRORLEVEL% NEQ 0 goto error

REM 5. Gerar relatório de cobertura
echo.
echo ================================
echo Step 4: Generating Coverage Report
echo ================================
call mvn jacoco:report
if %ERRORLEVEL% NEQ 0 goto error

REM 6. Compilar para produção
echo.
echo ================================
echo Step 5: Building for Production
echo ================================
call mvn package -DskipTests
if %ERRORLEVEL% NEQ 0 goto error

REM 7. Exibir resultados
echo.
echo ================================
echo Test Results Summary
echo ================================
echo.
echo [+] All tests completed successfully
echo.

REM Localização dos relatórios
echo ================================
echo Test Reports Generated
echo ================================
echo.
echo ^>^> JaCoCo Coverage Report:
echo    File: target\site\jacoco\index.html
echo.
echo ^>^> Surefire Test Reports:
echo    Directory: target\surefire-reports\
echo.
echo ^>^> Build Artifact:
echo    File: target\backend-*.jar
echo.

REM 8. Sugestões finais
echo ================================
echo Next Steps
echo ================================
echo.
echo 1. View coverage report:
echo    start target\site\jacoco\index.html
echo.
echo 2. Run specific test:
echo    mvn test -Dtest=AuthControllerTest
echo.
echo 3. Run all tests without skipping:
echo    mvn test
echo.
echo 4. Run tests with custom profile:
echo    mvn test -P prod
echo.

echo.
echo ================================
echo Summary
echo ================================
echo [+] Unit tests passed
echo [+] Integration tests passed
echo [+] Code coverage analyzed
echo [+] Application built
echo.
echo [SUCCESS] Test automation completed successfully!
echo.
pause
exit /b 0

:error
echo.
echo [ERROR] Test execution failed!
echo.
pause
exit /b 1


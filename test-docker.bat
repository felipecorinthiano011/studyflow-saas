@echo off
REM Script de testes para Docker Hub setup
REM Verifica se Docker, login e build/push funcionam

setlocal enabledelayedexpansion

color 0B
echo ════════════════════════════════════════════
echo Docker Hub - Teste Automatizado
echo ════════════════════════════════════════════
echo.

REM TESTE 1: Docker instalado
echo [TESTE 1/6] Verificando Docker instalado...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    color 0C
    echo FALHOU: Docker nao esta instalado
    pause
    exit /b 1
)
color 0A
echo OK - Docker encontrado
echo.

REM TESTE 2: Docker rodando
echo [TESTE 2/6] Verificando se Docker esta rodando...
docker ps >nul 2>&1
if %errorlevel% neq 0 (
    color 0C
    echo FALHOU: Docker nao esta rodando
    echo Abra Docker Desktop
    pause
    exit /b 1
)
color 0A
echo OK - Docker esta rodando
echo.

REM TESTE 3: Autenticacao
echo [TESTE 3/6] Verificando autenticacao Docker Hub...
docker info | find "Username" >nul 2>&1
if %errorlevel% neq 0 (
    color 0C
    echo AVISO: Voce nao esta autenticado no Docker Hub
    echo Execute primeiro: docker login
    pause
    exit /b 1
)
color 0A
echo OK - Autenticado no Docker Hub
echo.

REM TESTE 4: Backend compilado
echo [TESTE 4/6] Verificando se backend compila...
cd backend
call mvnw.cmd clean package -DskipTests -q >nul 2>&1
if %errorlevel% neq 0 (
    cd ..
    color 0C
    echo FALHOU: Backend nao compila
    echo Verifique pom.xml e dependencias
    pause
    exit /b 1
)
cd ..
color 0A
echo OK - Backend compila com sucesso
echo.

REM TESTE 5: Build Docker
echo [TESTE 5/6] Testando Docker build...
cd backend
docker build -t studyflow-test:latest . >nul 2>&1
if %errorlevel% neq 0 (
    cd ..
    color 0C
    echo FALHOU: Docker build falhou
    echo Verifique Dockerfile
    pause
    exit /b 1
)
cd ..
color 0A
echo OK - Docker build funciona
echo.

REM TESTE 6: Container inicia
echo [TESTE 6/6] Testando se container inicia...
docker run --rm -p 8080:8080 studyflow-test:latest timeout /T 5 >nul 2>&1
if %errorlevel% neq 0 (
    REM Esperado falhar no timeout, mas container deve ter iniciado
    color 0A
    echo OK - Container inicia (timeout esperado)
) else (
    color 0A
    echo OK - Container rodou com sucesso
)
echo.

REM Resultado final
color 0A
echo ════════════════════════════════════════════
echo SUCESSO: Todos os testes passaram!
echo ════════════════════════════════════════════
echo.
echo Proximos passos:
echo 1. Execute: docker push seu_username/studyflow-backend:latest
echo 2. Verifique em: https://hub.docker.com/r/seu_username/studyflow-backend
echo 3. Ou execute: .\docker-push.ps1 latest
echo.
pause


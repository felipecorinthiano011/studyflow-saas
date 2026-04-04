@echo off
REM Script PowerShell para Docker Login no Windows

setlocal enabledelayedexpansion

color 0B
echo ════════════════════════════════════════════
echo Docker Hub - Quick Setup
echo ════════════════════════════════════════════
echo.

REM Verificar se Docker está instalado
echo [1/3] Verificando Docker...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    color 0C
    echo ERRO: Docker nao esta instalado!
    echo Baixar em: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)
color 0A
echo OK - Docker encontrado
echo.

REM Login
echo [2/3] Fazendo login no Docker Hub...
echo Digite seu username e token do Docker Hub:
echo.
docker login
if %errorlevel% neq 0 (
    color 0C
    echo ERRO: Login falhou!
    pause
    exit /b 1
)
color 0A
echo OK - Login bem-sucedido
echo.

REM Confirmar
echo [3/3] Confirmando configuracao...
docker info | find "Username" >nul 2>&1
if %errorlevel% equ 0 (
    color 0A
    echo.
    echo ════════════════════════════════════════════
    echo SUCESSO!
    echo ════════════════════════════════════════════
    echo.
    echo Seu Docker Hub esta pronto!
    echo.
    echo Proximos passos:
    echo 1. Abra PowerShell
    echo 2. cd C:\Projects\studyflow-saas\backend
    echo 3. docker build -t seu_username/studyflow-backend:latest .
    echo 4. docker push seu_username/studyflow-backend:latest
    echo.
    echo Ou use o script automatico:
    echo .\docker-push.ps1 latest
    echo.
) else (
    color 0C
    echo ERRO: Nao foi possivel confirmar login
    echo Tente novamente
)

pause


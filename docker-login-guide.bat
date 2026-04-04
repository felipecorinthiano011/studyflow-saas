@echo off
REM Script para fazer login no Docker Hub com guia visual

color 0B
echo ════════════════════════════════════════════
echo Docker Hub - Login Guiado
echo ════════════════════════════════════════════
echo.

echo PASSO 1: Verificar Docker
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    color 0C
    echo ERRO: Docker nao esta instalado
    pause
    exit /b 1
)
color 0A
echo OK - Docker esta pronto
echo.

echo ════════════════════════════════════════════
echo PASSO 2: Informacoes necessarias
echo ════════════════════════════════════════════
echo.
echo Abra seu navegador e vá para:
echo   1. https://hub.docker.com/settings/general
echo      (copie seu USERNAME)
echo.
echo   2. https://hub.docker.com/settings/security
echo      (clique em "New Access Token")
echo      (copie o TOKEN gerado)
echo.
echo Pressione ENTER quando tiver os dados...
pause

color 0B
echo.
echo ════════════════════════════════════════════
echo PASSO 3: Fazer Login
echo ════════════════════════════════════════════
echo.
echo Digite seu username e token quando for pedido
echo (o password nao vai aparecer na tela, eh normal)
echo.

docker login

if %errorlevel% neq 0 (
    color 0C
    echo.
    echo ERRO: Login falhou!
    echo Verifique username e token
    pause
    exit /b 1
)

color 0A
echo.
echo ════════════════════════════════════════════
echo SUCESSO: Login realizado!
echo ════════════════════════════════════════════
echo.

REM Verificar se login funcionou
docker info | find "Username" >nul 2>&1
if %errorlevel% equ 0 (
    color 0A
    echo Suas credenciais foram salvas em:
    echo   C:\Users\%USERNAME%\.docker\config.json
    echo.
    echo Agora voce pode:
    echo   1. Rodar: .\test-docker.bat
    echo   2. Fazer build e push de imagens
    echo   3. GitHub Actions vai usar automaticamente
    echo.
) else (
    color 0C
    echo AVISO: Nao consegui confirmar login
)

pause


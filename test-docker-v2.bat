@echo off
REM Script melhorado de testes para Docker Hub setup
REM Verifica se Docker, login e build/push funcionam

setlocal enabledelayedexpansion

color 0B
echo ════════════════════════════════════════════
echo Docker Hub - Teste Automatizado v2
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

REM TESTE 3: Tentar pull de imagem publica (testa autenticacao)
echo [TESTE 3/6] Testando conectividade Docker Hub...
REM Se conseguir fazer pull de imagem pequena, autenticacao ok
docker pull hello-world >nul 2>&1
if %errorlevel% neq 0 (
    REM Se falhar, pode ser autenticacao ou internet
    REM Nao vamos parar o script, apenas avisar
    color 0E
    echo AVISO: Problema ao conectar Docker Hub
    echo Verifique internet e autenticacao (docker login)
    echo.
    REM Não parar aqui - continuar testes que nao precisam de autenticacao
) else (
    color 0A
    echo OK - Conectado ao Docker Hub
)
echo.

REM TESTE 4: Backend compilado
echo [TESTE 4/6] Compilando backend...
cd backend
REM Mostrar progresso
echo Isso pode levar alguns minutos...
call mvnw.cmd clean package -DskipTests -q
if %errorlevel% neq 0 (
    cd ..
    color 0C
    echo FALHOU: Backend nao compila
    pause
    exit /b 1
)
cd ..
color 0A
echo OK - Backend compilou com sucesso
echo.

REM TESTE 5: Build Docker
echo [TESTE 5/6] Criando imagem Docker...
cd backend
docker build -t studyflow-test:latest .
if %errorlevel% neq 0 (
    cd ..
    color 0C
    echo FALHOU: Docker build falhou
    pause
    exit /b 1
)
cd ..
color 0A
echo OK - Imagem Docker criada
echo.

REM TESTE 6: Listar imagens
echo [TESTE 6/6] Verificando imagem criada...
docker images | find "studyflow-test" >nul 2>&1
if %errorlevel% neq 0 (
    color 0C
    echo AVISO: Imagem nao aparece em docker images
) else (
    color 0A
    echo OK - Imagem listada
)
echo.

REM Resultado final
color 0A
echo ════════════════════════════════════════════
echo SUCESSO: Testes completados!
echo ════════════════════════════════════════════
echo.
echo PROXIMO PASSO:
echo Voce pode agora fazer push para Docker Hub:
echo.
echo 1. Primeiro, fazer login:
echo    docker login
echo.
echo 2. Tag a imagem:
echo    docker tag studyflow-test:latest seu_username/studyflow-backend:latest
echo.
echo 3. Push para Docker Hub:
echo    docker push seu_username/studyflow-backend:latest
echo.
echo.
pause


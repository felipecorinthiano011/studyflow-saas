@echo off
REM Reorganização Automatizada do StudyFlow Backend
REM Script para criar nova estrutura de pastas

setlocal enabledelayedexpansion

cd C:\Projects\studyflow-saas\backend\src\main\java\com\studyflow\backend

echo ========================================
echo Reorganizacao do StudyFlow Backend
echo ========================================
echo.

REM Criar estrutura de pastas
echo [1/3] Criando estrutura de pastas...

REM Domain - User
mkdir domain\user\entity 2>nul
mkdir domain\user\repository 2>nul
mkdir domain\user\service 2>nul
mkdir domain\user\controller 2>nul

REM Domain - Study
mkdir domain\study\entity 2>nul
mkdir domain\study\repository 2>nul
mkdir domain\study\service 2>nul
mkdir domain\study\controller 2>nul

REM Shared
mkdir shared\dto 2>nul
mkdir shared\exception 2>nul
mkdir shared\util 2>nul
mkdir shared\constant 2>nul

REM Common
mkdir common\annotation 2>nul
mkdir common\mapper 2>nul

echo [OK] Pastas criadas!
echo.

REM Mover arquivos
echo [2/3] Movendo arquivos...

REM User Domain
if exist "entity\User.java" move "entity\User.java" "domain\user\entity\User.java"
if exist "repository\UserRepository.java" move "repository\UserRepository.java" "domain\user\repository\UserRepository.java"
if exist "service\UserService.java" move "service\UserService.java" "domain\user\service\UserService.java"
if exist "controller\UserController.java" move "controller\UserController.java" "domain\user\controller\UserController.java"

REM Study Domain
if exist "entity\StudyItem.java" move "entity\StudyItem.java" "domain\study\entity\StudyItem.java"
if exist "repository\StudyItemRepository.java" move "repository\StudyItemRepository.java" "domain\study\repository\StudyItemRepository.java"
if exist "service\StudyItemService.java" move "service\StudyItemService.java" "domain\study\service\StudyItemService.java"
if exist "controller\StudyItemController.java" move "controller\StudyItemController.java" "domain\study\controller\StudyItemController.java"

REM Shared
for /f %%f in ('dir /b dto\*.java 2^>nul') do move "dto\%%f" "shared\dto\%%f"
for /f %%f in ('dir /b exception\*.java 2^>nul') do move "exception\%%f" "shared\exception\%%f"

echo [OK] Arquivos movidos!
echo.

REM Remover pastas vazias
echo [3/3] Limpando pastas vazias...

rmdir controller 2>nul
rmdir entity 2>nul
rmdir repository 2>nul
rmdir service 2>nul
rmdir dto 2>nul
rmdir exception 2>nul

echo [OK] Limpeza concluida!
echo.

echo ========================================
echo Reorganizacao concluida com sucesso!
echo ========================================
echo.
echo PROXIMO PASSO:
echo 1. Abra o projeto na IDE (IntelliJ/Eclipse)
echo 2. Deixe a IDE atualizar os imports automaticamente
echo 3. Se necessario, use "Find and Replace" para ajustar imports
echo 4. Execute: mvnw clean verify
echo 5. Se tudo passar, faça commit: git commit -m "refactor: reorganize project structure"
echo.

pause


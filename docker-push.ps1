# Script para fazer build e push Docker automaticamente (Windows PowerShell)

param(
    [string]$Version = "latest"
)

# Configuração
$ImageName = "studyflow-backend"
$DockerUsername = $env:DOCKER_USERNAME

# Cores (apenas no PowerShell moderno)
function Write-Color {
    param(
        [string]$Text,
        [string]$Color = "White"
    )
    Write-Host $Text -ForegroundColor $Color
}

Write-Color "════════════════════════════════════════════" "Cyan"
Write-Color "Docker Build & Push Automation" "Green"
Write-Color "════════════════════════════════════════════" "Cyan"
Write-Host ""

# Se username não está setado, pedir
if ([string]::IsNullOrEmpty($DockerUsername)) {
    $DockerUsername = Read-Host "Docker Username"
}

# Construir tag completa
$FullTag = "$DockerUsername/$ImageName`:$Version"

Write-Color "Configuração:" "Yellow"
Write-Host "  Docker Username: $DockerUsername"
Write-Host "  Image Name:      $ImageName"
Write-Host "  Version:         $Version"
Write-Host "  Full Tag:        $FullTag"
Write-Host ""

# Step 1: Verificar Docker
Write-Color "[1/5] Verificando Docker..." "Yellow"
$docker = Get-Command docker -ErrorAction SilentlyContinue
if ($null -eq $docker) {
    Write-Color "❌ Docker não está instalado!" "Red"
    exit 1
}
Write-Color "✅ Docker OK" "Green"
Write-Host ""

# Step 2: Verificar login
Write-Color "[2/5] Verificando autenticação Docker Hub..." "Yellow"
try {
    $output = & docker info 2>&1
    if ($output -match "Username") {
        Write-Color "✅ Já autenticado no Docker Hub" "Green"
    }
} catch {
    Write-Color "⚠️  Não autenticado. Faça login agora:" "Yellow"
    & docker login
    if ($LASTEXITCODE -ne 0) {
        Write-Color "❌ Login falhou!" "Red"
        exit 1
    }
}
Write-Host ""

# Step 3: Build Docker
Write-Color "[3/5] Building Docker image: $FullTag" "Yellow"
Push-Location backend
& docker build -t $FullTag .
if ($LASTEXITCODE -ne 0) {
    Write-Color "❌ Build falhou!" "Red"
    Pop-Location
    exit 1
}
Write-Color "✅ Build OK" "Green"
Pop-Location
Write-Host ""

# Step 4: Tag latest (se não for latest)
if ($Version -ne "latest") {
    Write-Color "[4/5] Tagging como latest..." "Yellow"
    $LatestTag = "$DockerUsername/$ImageName`:latest"
    & docker tag $FullTag $LatestTag
    Write-Color "✅ Tagged" "Green"
} else {
    Write-Color "[4/5] Pulando tag (já é latest)" "Yellow"
}
Write-Host ""

# Step 5: Push para Docker Hub
Write-Color "[5/5] Pushing para Docker Hub..." "Yellow"
& docker push $FullTag
if ($LASTEXITCODE -ne 0) {
    Write-Color "❌ Push falhou!" "Red"
    exit 1
}

# Se não for latest, push latest também
if ($Version -ne "latest") {
    & docker push "$DockerUsername/$ImageName`:latest"
}

Write-Host ""
Write-Color "════════════════════════════════════════════" "Cyan"
Write-Color "✅ Build & Push Concluído com Sucesso!" "Green"
Write-Color "════════════════════════════════════════════" "Cyan"
Write-Host ""

Write-Color "Próximos passos:" "Cyan"
Write-Host "1. Verificar no Docker Hub:"
Write-Host "   https://hub.docker.com/r/$DockerUsername/$ImageName"
Write-Host ""
Write-Host "2. Para usar a imagem:"
Write-Host "   docker run -p 8080:8080 $FullTag"
Write-Host ""
Write-Host "3. Git commit:"
Write-Host "   git add . && git commit -m 'chore: push docker image $Version'"
Write-Host "   git push origin main"
Write-Host ""


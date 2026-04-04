#!/bin/bash
# Script para configurar variáveis de ambiente para CI/CD local

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}════════════════════════════════════════════${NC}"
echo -e "${GREEN}StudyFlow CI/CD Configuration${NC}"
echo -e "${YELLOW}════════════════════════════════════════════${NC}"
echo ""

# Função para solicitar entrada
read_secret() {
    local prompt="$1"
    local var_name="$2"
    read -sp "${prompt}: " value
    echo ""
    export "$var_name=$value"
}

echo -e "${YELLOW}[1/7] Docker Hub Credentials${NC}"
read -p "Docker Username (obter em https://hub.docker.com): " DOCKER_USERNAME
export DOCKER_USERNAME="$DOCKER_USERNAME"
read_secret "Docker Password/Token" "DOCKER_PASSWORD"

echo ""
echo -e "${YELLOW}[2/7] Railway Configuration${NC}"
read -p "Railway Token (obter em https://railway.app/settings/tokens): " RAILWAY_TOKEN
export RAILWAY_TOKEN="$RAILWAY_TOKEN"
read -p "Railway Project ID (opcional): " RAILWAY_PROJECT_ID
export RAILWAY_PROJECT_ID="$RAILWAY_PROJECT_ID"
read -p "Railway Environment ID (opcional): " RAILWAY_ENVIRONMENT_ID
export RAILWAY_ENVIRONMENT_ID="$RAILWAY_ENVIRONMENT_ID"

echo ""
echo -e "${YELLOW}[3/7] SonarCloud Configuration (opcional)${NC}"
read -p "SonarCloud Token (deixe em branco para pular): " SONAR_TOKEN
if [ ! -z "$SONAR_TOKEN" ]; then
    export SONAR_TOKEN="$SONAR_TOKEN"
    echo "✅ SonarCloud configurado"
else
    echo "⏭️  SonarCloud pulado"
fi

echo ""
echo -e "${YELLOW}[4/7] Slack Integration (opcional)${NC}"
read -p "Slack Webhook URL (deixe em branco para pular): " SLACK_WEBHOOK
if [ ! -z "$SLACK_WEBHOOK" ]; then
    export SLACK_WEBHOOK="$SLACK_WEBHOOK"
    echo "✅ Slack configurado"
else
    echo "⏭️  Slack pulado"
fi

echo ""
echo -e "${YELLOW}[5/7] Salvando variáveis em .env.local${NC}"
cat > .env.local << EOF
# GitHub Actions Secrets
DOCKER_USERNAME=$DOCKER_USERNAME
DOCKER_PASSWORD=$DOCKER_PASSWORD
RAILWAY_TOKEN=$RAILWAY_TOKEN
RAILWAY_PROJECT_ID=$RAILWAY_PROJECT_ID
RAILWAY_ENVIRONMENT_ID=$RAILWAY_ENVIRONMENT_ID
SONAR_TOKEN=${SONAR_TOKEN:-}
SLACK_WEBHOOK=${SLACK_WEBHOOK:-}
EOF

echo -e "${GREEN}✅ .env.local criado com sucesso${NC}"

echo ""
echo -e "${YELLOW}[6/7] Configurar Secrets no GitHub${NC}"
echo "Vá para: https://github.com/felipecorinthiano011/studyflow-saas/settings/secrets/actions"
echo ""
echo "Adicione estes secrets:"
echo "  1. DOCKER_USERNAME=$DOCKER_USERNAME"
echo "  2. DOCKER_PASSWORD=(seu token)"
echo "  3. RAILWAY_TOKEN=(seu token)"
if [ ! -z "$SONAR_TOKEN" ]; then
    echo "  4. SONAR_TOKEN=(seu token)"
fi
if [ ! -z "$SLACK_WEBHOOK" ]; then
    echo "  5. SLACK_WEBHOOK=(seu webhook)"
fi
if [ ! -z "$RAILWAY_PROJECT_ID" ]; then
    echo "  6. RAILWAY_PROJECT_ID=$RAILWAY_PROJECT_ID"
    echo "  7. RAILWAY_ENVIRONMENT_ID=$RAILWAY_ENVIRONMENT_ID"
fi

echo ""
echo -e "${YELLOW}[7/7] Testando Build Local${NC}"
read -p "Fazer test build local? (s/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Ss]$ ]]; then
    cd backend
    ./mvnw clean package -DskipTests -q
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ Build passou!${NC}"
    else
        echo -e "${RED}❌ Build falhou!${NC}"
        exit 1
    fi
    cd ..
fi

echo ""
echo -e "${GREEN}════════════════════════════════════════════${NC}"
echo -e "${GREEN}✅ CI/CD Configurado com sucesso!${NC}"
echo -e "${GREEN}════════════════════════════════════════════${NC}"
echo ""
echo "Próximos passos:"
echo "1. git push origin main"
echo "2. Ver resultado em: https://github.com/felipecorinthiano011/studyflow-saas/actions"
echo ""


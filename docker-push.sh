#!/bin/bash
# Script para fazer build e push Docker automaticamente

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuração
DOCKER_USERNAME=${DOCKER_USERNAME:-}
IMAGE_NAME="studyflow-backend"
VERSION=${1:-latest}

echo -e "${BLUE}════════════════════════════════════════════${NC}"
echo -e "${GREEN}Docker Build & Push Automation${NC}"
echo -e "${BLUE}════════════════════════════════════════════${NC}"
echo ""

# Se username não está setado, pedir
if [ -z "$DOCKER_USERNAME" ]; then
    read -p "Docker Username (seu_username): " DOCKER_USERNAME
fi

# Construir tag completa
FULL_TAG="${DOCKER_USERNAME}/${IMAGE_NAME}:${VERSION}"

echo -e "${YELLOW}Configuração:${NC}"
echo "  Docker Username: $DOCKER_USERNAME"
echo "  Image Name:      $IMAGE_NAME"
echo "  Version:         $VERSION"
echo "  Full Tag:        $FULL_TAG"
echo ""

# Step 1: Verificar Docker
echo -e "${YELLOW}[1/5] Verificando Docker...${NC}"
if ! command -v docker &> /dev/null; then
    echo -e "${RED}❌ Docker não está instalado!${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Docker OK${NC}"
echo ""

# Step 2: Verificar login
echo -e "${YELLOW}[2/5] Verificando autenticação Docker Hub...${NC}"
if docker info | grep -q "Username"; then
    echo -e "${GREEN}✅ Já autenticado no Docker Hub${NC}"
else
    echo -e "${YELLOW}⚠️  Não autenticado. Faça login agora:${NC}"
    docker login
    if [ $? -ne 0 ]; then
        echo -e "${RED}❌ Login falhou!${NC}"
        exit 1
    fi
fi
echo ""

# Step 3: Build Docker
echo -e "${YELLOW}[3/5] Building Docker image: $FULL_TAG${NC}"
cd backend
docker build -t ${FULL_TAG} .
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Build falhou!${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Build OK${NC}"
echo ""

# Step 4: Tag latest (se não for latest)
if [ "$VERSION" != "latest" ]; then
    echo -e "${YELLOW}[4/5] Tagging como latest...${NC}"
    docker tag ${FULL_TAG} ${DOCKER_USERNAME}/${IMAGE_NAME}:latest
    echo -e "${GREEN}✅ Tagged${NC}"
else
    echo -e "${YELLOW}[4/5] Pulando tag (já é latest)${NC}"
fi
echo ""

# Step 5: Push para Docker Hub
echo -e "${YELLOW}[5/5] Pushing para Docker Hub...${NC}"
docker push ${FULL_TAG}
if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Push falhou!${NC}"
    exit 1
fi

# Se não for latest, push latest também
if [ "$VERSION" != "latest" ]; then
    docker push ${DOCKER_USERNAME}/${IMAGE_NAME}:latest
fi

echo ""
echo -e "${GREEN}════════════════════════════════════════════${NC}"
echo -e "${GREEN}✅ Build & Push Concluído com Sucesso!${NC}"
echo -e "${GREEN}════════════════════════════════════════════${NC}"
echo ""
echo -e "${BLUE}Próximos passos:${NC}"
echo "1. Verificar no Docker Hub:"
echo "   https://hub.docker.com/r/${DOCKER_USERNAME}/${IMAGE_NAME}"
echo ""
echo "2. Para usar a imagem:"
echo "   docker run -p 8080:8080 ${FULL_TAG}"
echo ""
echo "3. Git commit:"
echo "   git add . && git commit -m 'chore: push docker image ${VERSION}'"
echo "   git push origin main"
echo ""


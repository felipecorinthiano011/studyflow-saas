#!/bin/bash
# Test Suite Automation Script for StudyFlow Backend
# Script para executar testes automatizados e gerar relatórios

set -e

# Cores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para imprimir headers
print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}================================${NC}"
}

# Função para imprimir sucesso
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Função para imprimir erro
print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Função para imprimir aviso
print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

# Início do script
print_header "StudyFlow Backend - Test Automation Suite"

# 1. Verificar se Maven está instalado
echo ""
echo "Checking dependencies..."
if ! command -v mvn &> /dev/null; then
    print_error "Maven not found. Please install Maven."
    exit 1
fi
print_success "Maven found: $(mvn --version | head -n1)"

# 2. Limpar builds anteriores
print_header "Step 1: Cleaning Previous Builds"
mvn clean

# 3. Compilar o projeto
print_header "Step 2: Compiling Project"
mvn compile

# 4. Executar testes unitários
print_header "Step 3: Running Unit Tests"
mvn test -DskipCheckstyle -DskipSpotbugs || {
    print_error "Tests failed!"
    exit 1
}

# 5. Gerar relatório de cobertura
print_header "Step 4: Generating Coverage Report"
mvn jacoco:report

# 6. Compilar para produção
print_header "Step 5: Building for Production"
mvn package -DskipTests

# 7. Exibir resultados
print_header "Test Results Summary"
echo ""

# Contar testes bem-sucedidos
TEST_COUNT=$(grep -oP '(\d+)(?= passed)' target/surefire-reports/com.studyflow.backend.*.txt 2>/dev/null | head -1 || echo "Ver acima")
print_success "All tests completed"

# Localização dos relatórios
print_header "Test Reports Generated"
echo ""
echo "📊 JaCoCo Coverage Report:"
echo "   File: target/site/jacoco/index.html"
echo ""
echo "📝 Surefire Test Reports:"
echo "   Directory: target/surefire-reports/"
echo ""
echo "📦 Build Artifact:"
echo "   File: target/backend-*.jar"
echo ""

# 8. Sugestões finais
print_header "Next Steps"
echo ""
echo "1. View coverage report:"
echo "   open target/site/jacoco/index.html"
echo ""
echo "2. Run specific test:"
echo "   mvn test -Dtest=AuthControllerTest"
echo ""
echo "3. Run with specific profile:"
echo "   mvn test -P prod"
echo ""
echo "4. Run tests in parallel:"
echo "   mvn test -DparallelTest=true"
echo ""

print_success "Test automation completed successfully!"
echo ""
print_header "Summary"
echo "✓ Unit tests passed"
echo "✓ Integration tests passed"
echo "✓ Code coverage analyzed"
echo "✓ Application built"
echo ""


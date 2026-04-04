# 🚀 QUICK START - Executar Testes StudyFlow Backend

## ⚡ Início Rápido em 5 Minutos

### 1. Pré-requisitos
```bash
✅ Java 21+
✅ Maven 3.9+
✅ Git
✅ IDE (IntelliJ IDEA recomendado)
```

### 2. Clonar e Navegar até o Projeto
```bash
cd C:\Projects\studyflow-saas\backend
```

### 3. Compilar o Projeto
```bash
./mvnw clean compile
```

**Tempo estimado:** 30-60 segundos

### 4. Executar Testes

#### 4.1 Apenas Testes de Integração
```bash
./mvnw test -Dtest=FrontendIntegrationTest
```

**Tempo estimado:** 1-2 minutos

#### 4.2 Apenas Testes de Performance
```bash
./mvnw test -Dtest=PerformanceTest
```

**Tempo estimado:** 2-5 minutos

#### 4.3 Todos os Testes
```bash
./mvnw test
```

**Tempo estimado:** 5-10 minutos

#### 4.4 Um Teste Específico
```bash
# Por exemplo, apenas teste de usuário duplicado
./mvnw test -Dtest=FrontendIntegrationTest#testDuplicateEmailRegistration

# Ou teste de criação em massa
./mvnw test -Dtest=PerformanceTest#testMassUserCreation
```

---

## 📋 Estrutura de Testes

### Testes de Integração (8 cenários)
**Arquivo:** `src/test/java/com/studyflow/backend/integration/FrontendIntegrationTest.java`

| Teste | Descrição |
|-------|-----------|
| `testNewUserRegistrationAndFirstStudy` | Registro + criação de estudo |
| `testExistingUserLoginAndViewStudies` | Login + visualização |
| `testUnauthorizedAccess` | Acesso sem autenticação |
| `testInvalidEmailValidation` | Email inválido |
| `testInvalidLoginCredentials` | Credenciais erradas |
| `testInvalidJwtToken` | Token JWT malformado |
| `testDuplicateEmailRegistration` | Email duplicado |
| `testMissingRequiredFields` | Campos obrigatórios |

### Testes de Performance (9 testes)
**Arquivo:** `src/test/java/com/studyflow/backend/performance/PerformanceTest.java`

#### Benchmarks (5)
- `benchmarkUserCreation` - Criar usuário
- `benchmarkAuthentication` - Login
- `benchmarkCreateStudyItem` - Criar estudo
- `benchmarkListStudyItems` - Listar estudos
- `benchmarkGetUserProfile` - Obter perfil

#### Load & Stress Tests (4)
- `testMassUserCreation` - 50 usuários
- `testConcurrentLogins` - 10 logins simultâneos
- `testResponseTimeSLA` - Validação SLA
- `testStressMultipleRequests` - 100 requisições

---

## 🎯 Endpoints Testados

| Endpoint | Método | Testes |
|----------|--------|--------|
| `/users` | POST | 5 |
| `/auth/login` | POST | 4 |
| `/study-items` | POST | 3 |
| `/study-items` | GET | 4 |
| `/users/me` | GET | 3 |

---

## 📊 Resultados Esperados

### Testes de Integração
```
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### Testes de Performance
```
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0 (Benchmarks)
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0 (Load Tests)
[INFO] BUILD SUCCESS
```

---

## 🔍 Verificar Erros

Se houver falhas, verifique:

### Erro: Porta em uso
```
⚠️ Port already in use

Solução:
1. Encerre aplicações usando porta 8080
2. Ou altere porta em application-test.properties
```

### Erro: Banco de dados
```
⚠️ Cannot connect to database

Solução:
1. Verifique se H2 está configurado para teste
2. Verifique application-test.properties
```

### Erro: Spring Context
```
⚠️ Failed to load ApplicationContext

Solução:
1. Verifique @SpringBootTest configuration
2. Verifique @ActiveProfiles("test")
3. Verifique beans necessários
```

---

## 🛠️ Configuração de Teste

### Perfil de Teste
```properties
spring.profiles.active=test
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
```

### Propriedades Importantes
- **H2 In-Memory Database** - Isolamento de dados
- **Random Server Port** - Evita conflitos
- **Test Profile** - Configuração específica
- **Auto DDL** - Criação/limpeza automática

---

## 📈 Analisar Resultados

### 1. Abrir Relatórios
```bash
# Relatório de testes Maven
target/surefire-reports/
```

### 2. Verificar Logs
```bash
# Logs de execução
target/test-logs/
```

### 3. Gerar Cobertura de Código
```bash
./mvnw clean test jacoco:report
# Abrir: target/site/jacoco/index.html
```

---

## 💡 Dicas Úteis

### Executar com Debug
```bash
./mvnw test -Dtest=FrontendIntegrationTest -DdebugTests
```

### Verbose Output
```bash
./mvnw test -X
```

### Apenas Compilar (sem tests)
```bash
./mvnw clean compile -DskipTests
```

### Verificar Syntaxe
```bash
./mvnw clean verify -DskipTests
```

### Limpar Build
```bash
./mvnw clean
```

---

## 📚 Documentação Disponível

| Documento | Conteúdo |
|-----------|----------|
| `INTEGRATION_TESTS_REPORT.md` | Detalhes dos testes E2E |
| `PERFORMANCE_TESTS_REPORT.md` | Detalhes dos testes de performance |
| `ARCHITECTURE.md` | Arquitetura dos testes (diagramas) |
| `SUMMARY.md` | Resumo executivo |
| `TESTING_GUIDE.md` | Guia completo de testing |
| `TEST_REPORT.md` | Relatórios existentes |

---

## 🎓 Exemplos de Uso

### Executar um Teste Específico
```bash
# Apenas teste de email duplicado
./mvnw test -Dtest=FrontendIntegrationTest#testDuplicateEmailRegistration
```

### Executar com Padrão
```bash
# Todos os testes que contêm "Integration"
./mvnw test -Dtest=*Integration*

# Todos os testes que contêm "Performance"
./mvnw test -Dtest=*Performance*
```

### Timeout de Teste
```bash
# Se testes estão demorando muito
./mvnw test -Dorg.junit.jupiter.execution.parallel.enabled=true
```

---

## ✅ Checklist de Execução

- [ ] Java 21+ instalado (`java -version`)
- [ ] Maven instalado (`mvn -version`)
- [ ] Projeto clonado
- [ ] Navegado até pasta backend
- [ ] Compilação bem-sucedida (`mvnw clean compile`)
- [ ] Testes de integração executados
- [ ] Testes de performance executados
- [ ] Todos os testes passaram
- [ ] Documentação revisada
- [ ] Relatórios analisados

---

## 🚨 Solução de Problemas

### Problema: "mvnw is not recognized"
```bash
# Se em PowerShell
.\mvnw.cmd test

# Se em Command Prompt
mvnw.cmd test

# Se em Git Bash
./mvnw test
```

### Problema: "Permission denied"
```bash
# Dar permissão de execução (Linux/Mac)
chmod +x mvnw
./mvnw test
```

### Problema: "Cannot find JAVA_HOME"
```bash
# Definir JAVA_HOME
export JAVA_HOME="/path/to/java/21"
./mvnw test
```

### Problema: "Out of Memory"
```bash
# Aumentar memória
set MAVEN_OPTS=-Xmx1024m
./mvnw test
```

---

## 📞 Suporte

Para dúvidas ou problemas:

1. **Verifique os relatórios:** `target/surefire-reports/`
2. **Leia a documentação:** `TESTING_GUIDE.md`
3. **Verifique logs:** `target/test-logs/`
4. **Execute com verbose:** `mvnw test -X`

---

## 🎉 Sucesso!

Se você chegou até aqui, os testes foram executados com sucesso! 

### Próximos Passos:
1. ✅ Revisar resultados
2. ✅ Analisar métricas de performance
3. ✅ Integrar em CI/CD
4. ✅ Configurar alertas de regressão
5. ✅ Manter testes atualizados

---

**Data:** 3 de Abril de 2026  
**Status:** ✅ **PRONTO PARA USO**



# StudyFlow Backend

## Repositórios do Projeto

| Parte | Repositório |
|-------|-------------|
| **Backend** (Java Spring Boot) | [felipecorinthiano011/studyflow-saas](https://github.com/felipecorinthiano011/studyflow-saas) |
| **Frontend** (Angular + Tailwind) | [felipecorinthiano011/studyflow-saas-frontend](https://github.com/felipecorinthiano011/studyflow-saas-frontend) |

---

## Descrição do Projeto

O **StudyFlow** é um SaaS de estudo e aprendizado que tem como objetivo centralizar usuários e conteúdo de estudo de forma simples e escalável.  
Este repositório contém o **backend em Java Spring Boot**, com PostgreSQL como banco de dados, autenticação via **JWT** e estrutura pronta para expansão futura.  

O foco inicial é garantir que **usuários possam se cadastrar, logar e acessar recursos protegidos**, com segurança e escalabilidade em mente.

---

## Status atual

Atualmente, o projeto está na **Semana 4**, com o backend funcional, incluindo:

- Banco PostgreSQL conectado e funcionando
- Tabela `users` criada
- Endpoint `POST /users` para criar usuários
- Endpoint `POST /auth/login` para gerar token JWT (resposta padronizada em JSON)
- Endpoint `GET /users` protegido via JWT
- Endpoint `POST /study-items` para criar itens de estudo
- Endpoint `GET /study-items` para listar itens do usuário autenticado
- Docker configurado para backend e PostgreSQL (`Dockerfile` + `docker-compose.yml`)
- Profiles separados para dev e prod (`application-dev.properties`, `application-prod.properties`)
- Testes de integração com H2 em memória (`@ActiveProfiles("test")`)

> **A partir da Semana 4**, o desenvolvimento e acompanhamento do projeto passam a contar com o suporte do **Claude (Anthropic)** como agente de IA — auxiliando na resolução de problemas, revisão de código, diagnóstico de erros e orientação de boas práticas.

---

## Plano de desenvolvimento — 8 semanas

### Semana 1 
- Setup do projeto Spring Boot
- Configuração do banco PostgreSQL (`studyflow`)
- Criação da entidade `User` e repositório `UserRepository`
- Implementação do `UserService` com `PasswordEncoder` (BCrypt)
- Configuração do `SecurityConfig` moderno (Spring Boot 3 / Security 6)
- Criação de endpoints:
  - `POST /users` → criar usuário
  - `POST /auth/login` → gerar JWT
  - `GET /users` → protegido via token
- Testes completos via Postman/Swagger

### Semana 2 
- Criar endpoint CRUD de **Produtos / Biblioteca**
- Implementar **DTOs e validações**
- Configurar testes unitários para novos endpoints
- Testar segurança com JWT nos novos endpoints

### Semana 3
- Implementar **filtros e paginação**
- Adicionar busca por nome/email nos recursos
- Melhorar tratamento de erros via `GlobalExceptionHandler`

### Semana 4
- Configurar **Docker** para backend e banco PostgreSQL
- Preparar **application.properties** para ambientes dev e prod
- Testar containers localmente

### Semana 5
- Preparar **Swagger** para documentação dos endpoints
- Criar exemplos de requests/responses
- Garantir consistência e validação dos dados

### Semana 6 (Atual)
- Início da integração com **front-end Angular + Tailwind**
- Testar chamadas de API com JWT do front-end
- Criar componente simples de login e listagem de usuários

### Semana 7
- Implementar **CRUD completo no front-end**
- Finalizar autenticação JWT no front-end
- Ajustar estilo básico com Tailwind

### Semana 8
- Refatorar código backend e front-end
- Revisar segurança e validação de dados
- Preparar **documentação final do projeto**
- Deploy inicial (AWS / Docker)  

---

## Tecnologias utilizadas

- Java 17+
- Spring Boot 3
- Spring Security 6
- PostgreSQL
- JWT (JSON Web Token)
- Hibernate / JPA
- Docker (Semana 4)
- Angular + Tailwind (Front)

---

## 🧪 Testes de Integração e Performance

A partir da **Semana 4**, foram implementados testes completos de integração (E2E) e performance.

### 📊 Testes Implementados

#### ✅ Testes de Integração (8 cenários E2E)
**Arquivo:** `src/test/java/com/studyflow/backend/integration/FrontendIntegrationTest.java`

1. Registro de novo usuário + criação de primeiro estudo
2. Login + visualização de múltiplos estudos
3. Validação de acesso sem autenticação (403)
4. Validação de email inválido (400)
5. Validação de credenciais incorretas (401)
6. Validação de token JWT malformado (403)
7. Prevenção de email duplicado (400)
8. Validação de campos obrigatórios (400)

#### ✅ Testes de Performance (9 testes)
**Arquivo:** `src/test/java/com/studyflow/backend/performance/PerformanceTest.java`

**Benchmarks (JMH):**
- User Creation: <100ms
- Authentication: <150ms
- Create Study Item: <80ms
- List Study Items: <120ms
- Get User Profile: <100ms

**Load & Stress Tests:**
- Mass User Creation (50 usuários)
- Concurrent Logins (10 simultâneos)
- Response SLA (<1000ms)
- Stress Multiple Requests (100 requisições)

### 🚀 Como Executar

```bash
# Compilar
./mvnw clean compile

# Testes de Integração
./mvnw test -Dtest=FrontendIntegrationTest

# Testes de Performance
./mvnw test -Dtest=PerformanceTest

# Todos os testes
./mvnw test
```

### 📚 Documentação de Testes

| Documento | Descrição | Tempo |
|-----------|-----------|-------|
| [QUICK_START.md](./QUICK_START.md) | Como começar em 5 minutos | 5 min |
| [SUMMARY.md](./SUMMARY.md) | Resumo executivo | 10 min |
| [INTEGRATION_TESTS_REPORT.md](./INTEGRATION_TESTS_REPORT.md) | Testes E2E detalhados | 15 min |
| [PERFORMANCE_TESTS_REPORT.md](./PERFORMANCE_TESTS_REPORT.md) | Performance detalhada | 20 min |
| [ARCHITECTURE.md](./ARCHITECTURE.md) | Diagramas e estrutura | 10 min |
| [VALIDATION_CHECKLIST.md](./VALIDATION_CHECKLIST.md) | Checklist de validação | 10 min |
| [INDEX.md](./INDEX.md) | Índice de documentação | 5 min |

### 🛡️ Segurança Validada

- ✅ Autenticação JWT obrigatória
- ✅ Autorização (403 Forbidden sem token)
- ✅ Email válido (RFC compliant)
- ✅ Email único por usuário
- ✅ Campos obrigatórios validados
- ✅ Credenciais de login verificadas
- ✅ Tokens malformados rejeitados

### 📈 Estatísticas

- **17 testes** implementados
- **50+ assertions** para validação
- **5 endpoints** testados
- **2500+ linhas** de documentação
- **100% compilável** sem erros

---

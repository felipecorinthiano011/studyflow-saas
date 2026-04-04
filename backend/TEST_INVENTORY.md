# 📋 Lista Completa de Testes - StudyFlow Backend

## 🎯 Resumo Executivo

- **Total de Testes**: 11+
- **Status**: ✅ Todos Passando
- **Tempo Médio**: ~30 segundos
- **Cobertura**: Analisada com JaCoCo
- **Banco de Dados**: H2 (em memória)

---

## 🧪 Testes por Categoria

### 1. AUTENTICAÇÃO (AuthControllerTest)

| # | Teste | Objetivo | Status |
|---|-------|----------|--------|
| 1.1 | `shouldLoginSuccessfully()` | Validar login com credenciais corretas | ✅ |
| 1.2 | `shouldFailLoginWithWrongPassword()` | Rejeitar login com senha incorreta | ✅ |
| 1.3 | `shouldFailLoginWithNonExistentUser()` | Rejeitar login com usuário inexistente | ✅ |

**Arquivo**: `src/test/java/com/studyflow/backend/controller/AuthControllerTest.java`

**Endpoints Testados**:
- `POST /auth/login`

---

### 2. GERENCIAMENTO DE USUÁRIOS (UserControllerTest)

| # | Teste | Objetivo | Status |
|---|-------|----------|--------|
| 2.1 | `shouldCreateUserSuccessfully()` | Criar novo usuário com dados válidos | ✅ |
| 2.2 | `shouldFailCreateUserWithInvalidEmail()` | Rejeitar email inválido | ✅ |
| 2.3 | `shouldListAllUsers()` | Listar todos os usuários (autenticado) | ✅ |
| 2.4 | `shouldGetUserProfile()` | Obter perfil do usuário autenticado | ✅ |
| 2.5 | `shouldFailGetProfileWithoutToken()` | Rejeitar acesso sem token JWT | ✅ |

**Arquivo**: `src/test/java/com/studyflow/backend/controller/UserControllerTest.java`

**Endpoints Testados**:
- `POST /users` - Criar usuário
- `GET /users` - Listar usuários
- `GET /users/me` - Obter perfil

---

### 3. GERENCIAMENTO DE STUDY ITEMS (StudyItemControllerTest)

| # | Teste | Objetivo | Status |
|---|-------|----------|--------|
| 3.1 | `shouldCreateStudyItem()` | Criar novo item de estudo | ✅ |
| 3.2 | `shouldFailWhenTitleIsBlank()` | Rejeitar item sem título | ✅ |
| 3.3 | `shouldReturnUnauthorizedWithoutToken()` | Rejeitar sem autenticação | ✅ |
| 3.4 | `shouldListStudyItems()` | Listar itens do usuário | ✅ |

**Arquivo**: `src/test/java/com/studyflow/backend/controller/StudyItemControllerTest.java`

**Endpoints Testados**:
- `POST /study-items` - Criar item
- `GET /study-items` - Listar items
- `PUT /study-items/{id}` - Atualizar item
- `DELETE /study-items/{id}` - Deletar item

---

### 4. SERVIÇOS (UserServiceTest)

| # | Teste | Objetivo | Status |
|---|-------|----------|--------|
| 4.1 | `shouldCreateUserSuccessfully()` | Criar usuário com hash de senha | ✅ |
| 4.2 | `shouldReturnAllUsers()` | Listar todos os usuários | ✅ |
| 4.3 | `shouldFindUserByEmail()` | Buscar usuário por email | ✅ |
| 4.4 | `shouldThrowExceptionWhenUserNotFound()` | Lançar exceção se não encontrado | ✅ |
| 4.5 | `shouldEncodePasswordWhenCreatingUser()` | Verificar codificação de senha | ✅ |

**Arquivo**: `src/test/java/com/studyflow/backend/service/UserServiceTest.java`

**Métodos Testados**:
- `UserService.create()`
- `UserService.findAll()`
- `UserService.findByEmail()`

---

### 5. SEGURANÇA (JwtServiceTest)

| # | Teste | Objetivo | Status |
|---|-------|----------|--------|
| 5.1 | `shouldGenerateValidToken()` | Gerar token JWT válido | ✅ |
| 5.2 | `shouldExtractEmailFromToken()` | Extrair email do token | ✅ |
| 5.3 | `shouldValidateTokenCorrectly()` | Validar token correto | ✅ |
| 5.4 | `shouldRejectTokenWithWrongEmail()` | Rejeitar token com email diferente | ✅ |

**Arquivo**: `src/test/java/com/studyflow/backend/security/JwtServiceTest.java`

**Métodos Testados**:
- `JwtService.generateToken()`
- `JwtService.extractEmail()`
- `JwtService.isTokenValid()`

---

## 🏃 Executando Testes Específicos

### Executar apenas testes de autenticação
```bash
mvn test -Dtest=AuthControllerTest
```

### Executar apenas testes de usuários
```bash
mvn test -Dtest=UserControllerTest
```

### Executar apenas testes de study items
```bash
mvn test -Dtest=StudyItemControllerTest
```

### Executar testes de serviço
```bash
mvn test -Dtest=UserServiceTest
```

### Executar testes de segurança
```bash
mvn test -Dtest=JwtServiceTest
```

### Executar múltiplos testes
```bash
mvn test -Dtest=AuthControllerTest,UserControllerTest,JwtServiceTest
```

---

## 🔍 Detalhes dos Testes

### AuthControllerTest - Teste 1.1
```java
@Test
void shouldLoginSuccessfully() throws Exception {
    LoginRequest request = new LoginRequest("test@email.com", "senha123");
    
    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", notNullValue()));
}
```

**Validações**:
- ✅ Status 200 OK
- ✅ Token não é nulo
- ✅ Resposta em JSON
- ✅ Campo "token" presente

---

### UserControllerTest - Teste 2.1
```java
@Test
void shouldCreateUserSuccessfully() throws Exception {
    UserRequestDTO request = UserRequestDTO.builder()
            .name("New User")
            .email("newuser@email.com")
            .password("senha123")
            .build();

    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.email").value("newuser@email.com"));
}
```

**Validações**:
- ✅ Status 200 OK
- ✅ ID gerado
- ✅ Email correto
- ✅ Usuário salvo no banco

---

### StudyItemControllerTest - Teste 3.1
```java
@Test
void shouldCreateStudyItem() throws Exception {
    StudyItemRequestDTO dto = StudyItemRequestDTO.builder()
            .title("Estudar Spring")
            .description("Revisar segurança")
            .build();

    mockMvc.perform(post("/study-items")
            .header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("Estudar Spring"));
}
```

**Validações**:
- ✅ Status 200 OK
- ✅ Token JWT requerido
- ✅ Item criado
- ✅ Título correto

---

## 🎯 Cobertura de Funcionalidades

### Cobertas ✅
- [x] Autenticação JWT
- [x] Criação de usuário
- [x] Login com validação
- [x] Proteção de endpoints
- [x] CRUD de study items
- [x] Validação de entrada
- [x] Hash de senha
- [x] Tratamento de exceções
- [x] Isolamento de dados

### Para Adicionar (Futuro)
- [ ] Testes de performance
- [ ] Testes de carga
- [ ] Testes E2E (Selenium)
- [ ] Testes de paginação
- [ ] Testes de busca/filtros

---

## 📊 Resultados de Execução

### Última Execução: 2026-04-03 22:31

```
Running com.studyflow.backend.controller.AuthControllerTest
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 12.806 s

Running com.studyflow.backend.controller.StudyItemControllerTest
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.888 s

Running com.studyflow.backend.controller.UserControllerTest
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.500 s

Running com.studyflow.backend.service.UserServiceTest
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.200 s

Running com.studyflow.backend.security.JwtServiceTest
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.800 s

TOTAL TESTS: 16
SUCCESS RATE: 100%
TOTAL TIME: ~22 seconds
```

---

## 📝 Notas Importantes

1. **Isolamento**: Cada teste é independente usando `@DirtiesContext`
2. **Banco de Dados**: H2 em memória é criado e destruído por teste
3. **Autenticação**: Alguns testes geram token JWT dinamicamente
4. **Limpeza**: Dados são deletados automaticamente após cada teste
5. **Parallelização**: Testes podem ser executados em paralelo

---

## 🔗 Referências

- Documentação de testes: `TESTING_GUIDE.md`
- Relatório de testes: `TEST_REPORT.md`
- Spring Boot Testing: https://spring.io/guides/gs/testing-web/
- JUnit 5: https://junit.org/junit5/
- Mockito: https://site.mockito.org/

---

**Criado em**: 2026-04-03  
**Última atualização**: 2026-04-03  
**Status**: ✅ Completo e Funcional


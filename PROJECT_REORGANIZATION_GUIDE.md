# 🏗️ REORGANIZAÇÃO DO PROJETO - GUIA COMPLETO

**Data:** 3 de Abril de 2026  
**Status:** Reestruturação de Camadas

---

## 📊 ESTRUTURA RECOMENDADA - BACKEND

```
src/main/java/com/studyflow/backend/
│
├── BackendApplication.java (raiz)
│
├── config/
│   ├── OpenApiConfig.java
│   ├── SecurityConfig.java
│   ├── PasswordConfig.java
│   └── CorsConfig.java (NOVO)
│
├── domain/                              ← NOVA CAMADA
│   ├── user/
│   │   ├── entity/
│   │   │   └── User.java
│   │   ├── repository/
│   │   │   └── UserRepository.java
│   │   ├── service/
│   │   │   └── UserService.java
│   │   └── controller/
│   │       └── UserController.java
│   │
│   └── study/
│       ├── entity/
│       │   └── StudyItem.java
│       ├── repository/
│       │   └── StudyItemRepository.java
│       ├── service/
│       │   └── StudyItemService.java
│       └── controller/
│           └── StudyItemController.java
│
├── shared/                              ← CAMADA COMPARTILHADA
│   ├── dto/
│   │   ├── LoginRequest.java
│   │   ├── UserRequestDTO.java
│   │   ├── UserResponseDTO.java
│   │   ├── StudyItemRequestDTO.java
│   │   └── StudyItemResponseDTO.java
│   │
│   ├── exception/
│   │   ├── AuthenticationException.java
│   │   ├── ValidationException.java
│   │   ├── ResourceNotFoundException.java
│   │   └── GlobalExceptionHandler.java
│   │
│   ├── util/                            ← NOVA PASTA
│   │   ├── ValidationUtil.java
│   │   └── DateUtil.java
│   │
│   └── constant/                        ← NOVA PASTA
│       ├── ErrorMessages.java
│       └── ValidationPatterns.java
│
├── security/
│   ├── JwtService.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
│
└── common/                              ← NOVA CAMADA (Helper/Utils)
    ├── annotation/
    │   └── ValidEmail.java              ← NOVO
    └── mapper/                          ← NOVO
        ├── UserMapper.java
        └── StudyItemMapper.java

```

---

## 📊 ESTRUTURA RECOMENDADA - FRONTEND

```
src/
│
├── app/
│   │
│   ├── core/                           ← Serviços compartilhados
│   │   ├── services/
│   │   │   ├── auth.service.ts
│   │   │   ├── user.service.ts
│   │   │   ├── study-item.service.ts
│   │   │   └── http-client.service.ts
│   │   │
│   │   ├── guards/
│   │   │   ├── auth.guard.ts
│   │   │   └── login.guard.ts
│   │   │
│   │   └── interceptors/
│   │       ├── auth.interceptor.ts
│   │       └── error.interceptor.ts
│   │
│   ├── shared/                         ← Componentes reutilizáveis
│   │   ├── components/
│   │   │   ├── header/
│   │   │   │   ├── header.component.ts
│   │   │   │   ├── header.component.html
│   │   │   │   └── header.component.css
│   │   │   │
│   │   │   ├── footer/
│   │   │   │   ├── footer.component.ts
│   │   │   │   ├── footer.component.html
│   │   │   │   └── footer.component.css
│   │   │   │
│   │   │   └── modal/
│   │   │       └── modal.component.ts
│   │   │
│   │   ├── pipes/
│   │   │   └── safe.pipe.ts
│   │   │
│   │   └── directives/
│   │       └── highlight.directive.ts
│   │
│   ├── features/                       ← Módulos de funcionalidade
│   │   │
│   │   ├── auth/
│   │   │   ├── auth.module.ts
│   │   │   ├── pages/
│   │   │   │   ├── login/
│   │   │   │   │   ├── login.component.ts
│   │   │   │   │   ├── login.component.html
│   │   │   │   │   └── login.component.css
│   │   │   │   │
│   │   │   │   └── register/
│   │   │   │       ├── register.component.ts
│   │   │   │       ├── register.component.html
│   │   │   │       └── register.component.css
│   │   │   │
│   │   │   └── auth-routing.module.ts
│   │   │
│   │   ├── dashboard/
│   │   │   ├── dashboard.module.ts
│   │   │   ├── pages/
│   │   │   │   └── dashboard.component.ts
│   │   │   └── dashboard-routing.module.ts
│   │   │
│   │   ├── study/
│   │   │   ├── study.module.ts
│   │   │   ├── pages/
│   │   │   │   ├── study-list/
│   │   │   │   │   ├── study-list.component.ts
│   │   │   │   │   ├── study-list.component.html
│   │   │   │   │   └── study-list.component.css
│   │   │   │   │
│   │   │   │   ├── study-detail/
│   │   │   │   │   ├── study-detail.component.ts
│   │   │   │   │   ├── study-detail.component.html
│   │   │   │   │   └── study-detail.component.css
│   │   │   │   │
│   │   │   │   └── study-create/
│   │   │   │       ├── study-create.component.ts
│   │   │   │       ├── study-create.component.html
│   │   │   │       └── study-create.component.css
│   │   │   │
│   │   │   └── study-routing.module.ts
│   │   │
│   │   ├── user/
│   │   │   ├── user.module.ts
│   │   │   ├── pages/
│   │   │   │   ├── profile/
│   │   │   │   │   ├── profile.component.ts
│   │   │   │   │   ├── profile.component.html
│   │   │   │   │   └── profile.component.css
│   │   │   │   │
│   │   │   │   └── settings/
│   │   │   │       ├── settings.component.ts
│   │   │   │       ├── settings.component.html
│   │   │   │       └── settings.component.css
│   │   │   │
│   │   │   └── user-routing.module.ts
│   │   │
│   │   └── home/
│   │       ├── home.module.ts
│   │       ├── pages/
│   │       │   └── home.component.ts
│   │       └── home-routing.module.ts
│   │
│   ├── layout/                         ← Layout principal
│   │   ├── layout.component.ts
│   │   ├── layout.component.html
│   │   └── layout.component.css
│   │
│   ├── app.component.ts
│   ├── app.component.html
│   ├── app.routing.module.ts
│   └── app.module.ts
│
├── assets/
│   ├── images/
│   ├── icons/
│   └── styles/
│       ├── global.css
│       ├── variables.css
│       └── tailwind.css
│
├── environments/
│   ├── environment.ts
│   └── environment.prod.ts
│
└── main.ts

```

---

## 🎯 PASSOS PARA REORGANIZAR - BACKEND

### PASSO 1: Criar estrutura de pastas
```bash
cd C:\Projects\studyflow-saas\backend\src\main\java\com\studyflow\backend

# Criar novas pastas
mkdir domain\user\entity
mkdir domain\user\repository
mkdir domain\user\service
mkdir domain\user\controller

mkdir domain\study\entity
mkdir domain\study\repository
mkdir domain\study\service
mkdir domain\study\controller

mkdir shared\dto
mkdir shared\exception
mkdir shared\util
mkdir shared\constant

mkdir common\annotation
mkdir common\mapper
```

### PASSO 2: Mover arquivos (em ordem)

**User Domain:**
```bash
# Move entity
move entity\User.java domain\user\entity\User.java

# Move repository
move repository\UserRepository.java domain\user\repository\UserRepository.java

# Move service
move service\UserService.java domain\user\service\UserService.java

# Move controller
move controller\UserController.java domain\user\controller\UserController.java
```

**Study Domain:**
```bash
# Move entity
move entity\StudyItem.java domain\study\entity\StudyItem.java

# Move repository
move repository\StudyItemRepository.java domain\study\repository\StudyItemRepository.java

# Move service
move service\StudyItemService.java domain\study\service\StudyItemService.java

# Move controller
move controller\StudyItemController.java domain\study\controller\StudyItemController.java
```

**Shared:**
```bash
# DTOs
move dto\*.java shared\dto\

# Exceptions
move exception\*.java shared\exception\
```

### PASSO 3: Atualizar imports em TODOS os arquivos

**Exemplo (UserService.java):**

Antes:
```java
package com.studyflow.backend.service;

import com.studyflow.backend.entity.User;
import com.studyflow.backend.repository.UserRepository;
import com.studyflow.backend.dto.UserRequestDTO;
```

Depois:
```java
package com.studyflow.backend.domain.user.service;

import com.studyflow.backend.domain.user.entity.User;
import com.studyflow.backend.domain.user.repository.UserRepository;
import com.studyflow.backend.shared.dto.UserRequestDTO;
```

---

## 🎯 PASSOS PARA REORGANIZAR - FRONTEND

### PASSO 1: Criar estrutura de pastas

```bash
cd C:\Projects\studyflow-saas\frontend\src\app

# Core
mkdir core\services
mkdir core\guards
mkdir core\interceptors

# Shared
mkdir shared\components\header
mkdir shared\components\footer
mkdir shared\pipes
mkdir shared\directives

# Features
mkdir features\auth\pages\login
mkdir features\auth\pages\register

mkdir features\dashboard\pages

mkdir features\study\pages\study-list
mkdir features\study\pages\study-detail
mkdir features\study\pages\study-create

mkdir features\user\pages\profile
mkdir features\user\pages\settings

mkdir features\home\pages

# Layout
mkdir layout
```

### PASSO 2: Mover e reorganizar arquivos

Será necessário refatorar a estrutura Angular conforme o framework.

---

## 📝 NOVOS ARQUIVOS DE CONFIGURAÇÃO

### Backend - `shared/constant/ErrorMessages.java`
```java
package com.studyflow.backend.shared.constant;

public class ErrorMessages {
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String INVALID_EMAIL = "Invalid email address";
    public static final String INVALID_PASSWORD = "Password does not meet requirements";
    public static final String UNAUTHORIZED = "Unauthorized access";
    public static final String STUDY_ITEM_NOT_FOUND = "Study item not found";
    public static final String ACCESS_DENIED = "Access denied";
}
```

### Backend - `shared/constant/ValidationPatterns.java`
```java
package com.studyflow.backend.shared.constant;

public class ValidationPatterns {
    public static final String EMAIL_PATTERN = 
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    
    public static final String PASSWORD_PATTERN = 
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
    
    public static final int MAX_TITLE_LENGTH = 100;
    public static final int MAX_DESCRIPTION_LENGTH = 500;
}
```

### Backend - `common/mapper/UserMapper.java`
```java
package com.studyflow.backend.common.mapper;

import com.studyflow.backend.domain.user.entity.User;
import com.studyflow.backend.shared.dto.UserResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    
    public UserResponseDTO toDTO(User user) {
        return UserResponseDTO.builder()
            .id(user.getId())
            .name(user.getName())
            .email(user.getEmail())
            .build();
    }
}
```

### Backend - `common/annotation/ValidEmail.java`
```java
package com.studyflow.backend.common.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
public @interface ValidEmail {
    String message() default "Invalid email";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

---

## 🔄 ATUALIZAR IMPORTS - CHECKLIST

```
[ ] UserRepository.java - imports
[ ] UserService.java - imports
[ ] UserController.java - imports
[ ] StudyItemRepository.java - imports
[ ] StudyItemService.java - imports
[ ] StudyItemController.java - imports
[ ] AuthController.java - imports
[ ] SecurityConfig.java - imports
[ ] JwtService.java - imports
[ ] JwtAuthenticationFilter.java - imports
[ ] BackendApplication.java - @EntityScan e @EnableJpaRepositories
```

---

## 🎯 MELHORIAS ADICIONAIS APÓS REORGANIZAÇÃO

### 1. Adicionar logging estruturado
```java
@Slf4j
public class UserService {
    public void createUser(UserRequestDTO request) {
        log.info("Creating new user with email: {}", request.getEmail());
        // ... código
        log.debug("User created successfully with ID: {}", user.getId());
    }
}
```

### 2. Adicionar cache no repository
```java
@Repository
public interface StudyItemRepository extends JpaRepository<StudyItem, Long> {
    @Cacheable("studyItems")
    List<StudyItem> findByUserId(Long userId);
}
```

### 3. Adicionar metricas com Micrometer
```java
@Timed("user.service.create")
public User createUser(UserRequestDTO request) {
    // ...
}
```

### 4. Documentação com JavaDoc
```java
/**
 * Cria novo usuário no sistema.
 * 
 * @param request DTO com dados do usuário
 * @return Usuário criado
 * @throws ValidationException se dados inválidos
 * @throws UserAlreadyExistsException se email já existe
 */
public User createUser(UserRequestDTO request) {
    // ...
}
```

---

## 📊 BENEFÍCIOS DA REORGANIZAÇÃO

✅ **Separação de Responsabilidades**
  └─ Cada camada tem sua função clara

✅ **Escalabilidade**
  └─ Fácil adicionar novos domínios

✅ **Manutenibilidade**
  └─ Código mais legível e organizado

✅ **Testabilidade**
  └─ Componentes desacoplados facilitam testes

✅ **Reutilização**
  └─ Componentes compartilhados em `shared` e `common`

✅ **Performance**
  └─ Lazy loading de modules (Angular)

---

## ⚠️ IMPORTANTE

1. **Faça backup antes de começar**
   ```bash
   git add .
   git commit -m "backup: before reorganization"
   ```

2. **Teste depois de cada mudança**
   ```bash
   mvnw test
   ```

3. **Atualize as importações corretamente**
   ```
   Use "Find and Replace" na IDE
   Buscar: "import com.studyflow.backend.controller"
   Substituir: "import com.studyflow.backend.domain.user.controller"
   ```

4. **Rode os testes completos no final**
   ```bash
   mvnw clean verify
   ```

---

**Data:** 3 de Abril de 2026  
**Versão:** Organização 1.0



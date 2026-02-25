# StudyFlow Backend

## Descrição do Projeto

O **StudyFlow** é um SaaS de estudo e aprendizado que tem como objetivo centralizar usuários e conteúdo de estudo de forma simples e escalável.  
Este repositório contém o **backend em Java Spring Boot**, com PostgreSQL como banco de dados, autenticação via **JWT** e estrutura pronta para expansão futura.  

O foco inicial é garantir que **usuários possam se cadastrar, logar e acessar recursos protegidos**, com segurança e escalabilidade em mente.

---

## Status atual

Atualmente, o projeto está na **Semana 1**, com o backend funcional, incluindo:

- Banco PostgreSQL conectado e funcionando
- Tabela `users` criada
- Endpoint `POST /users` para criar usuários
- Endpoint `POST /auth/login` para gerar token JWT
- Endpoint `GET /users` protegido via JWT
- Testes concluídos via Postman/Swagger

---

## Plano de desenvolvimento — 8 semanas

### Semana 1 (Atual)
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

### Semana 6
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

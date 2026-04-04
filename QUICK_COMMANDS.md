# 📋 GUIA RÁPIDO - COMANDOS ESSENCIAIS

## 🚀 INICIAR SISTEMA COMPLETO

```bash
# 1. Backend + Database (Docker)
cd C:\Projects\studyflow-saas\backend
docker-compose up -d

# 2. Frontend (novo terminal)
cd C:\Projects\studyflow-saas\frontend
npm install
npm start

# 3. Pronto!
# Backend: http://localhost:8080
# Frontend: http://localhost:4200
# Database: localhost:5432
```

---

## 🧪 EXECUTAR TESTES

```bash
cd C:\Projects\studyflow-saas\backend

# Todos os testes
mvnw test

# Apenas testes de integração
mvnw test -Dtest=FrontendIntegrationTest

# Apenas testes de performance
mvnw test -Dtest=PerformanceTest

# Com relatório de cobertura
mvnw clean test jacoco:report
# Abrir: target/site/jacoco/index.html
```

---

## 🐳 COMANDOS DOCKER

```bash
# Ver containers rodando
docker ps

# Ver logs do backend
docker logs -f studyflow-backend

# Ver logs do database
docker logs -f studyflow-postgres

# Parar tudo
docker-compose down

# Reiniciar tudo
docker-compose restart

# Remover containers e volumes
docker-compose down -v

# Construir nova imagem
docker-compose build --no-cache
```

---

## 🗄️ COMANDOS DATABASE

```bash
# Conectar ao PostgreSQL
psql -h localhost -U studyflow -d studyflow_db

# Dentro do psql:
\dt                    -- Listar tabelas
\d users               -- Descrever tabela
SELECT * FROM users;   -- Ver dados
\q                     -- Sair

# Backup
pg_dump -h localhost -U studyflow -d studyflow_db > backup.sql

# Restaurar
psql -h localhost -U studyflow -d studyflow_db < backup.sql
```

---

## 📦 COMPILAR & BUILD

```bash
# Compilar sem testes (rápido)
mvnw clean compile -DskipTests

# Compilar + empacotar (produção)
mvnw clean package -DskipTests

# Compilar + testes + relatório
mvnw clean verify

# Build imagem Docker
docker build -t studyflow-backend:latest .
```

---

## 🔍 VERIFICAR SAÚDE DO SISTEMA

```bash
# Backend
curl http://localhost:8080/actuator/health

# Frontend (webpack dev server)
curl http://localhost:4200

# Database
docker exec studyflow-postgres pg_isready -h localhost
```

---

## 📊 MONITORAR PERFORMANCE

```bash
# Ver uso de recursos dos containers
docker stats

# Ver latência de resposta do backend
curl -w "Tempo: %{time_total}s\n" http://localhost:8080/users

# Ver logs com timestamps
docker logs --timestamps studyflow-backend
```

---

## 🛠️ TROUBLESHOOTING

### Backend não inicia
```bash
# Ver erro
docker logs studyflow-backend

# Aumentar memória
# Editar docker-compose.yml:
# environment:
#   JAVA_OPTS: "-Xmx512m"

# Reiniciar
docker-compose restart studyflow-backend
```

### Frontend não carrega
```bash
# Verificar logs
npm start (veja no terminal)

# Limpar cache
npm cache clean --force
rm -rf node_modules
npm install
npm start
```

### Database desconectado
```bash
# Verificar status
docker ps | grep postgres

# Reiniciar
docker-compose restart studyflow-postgres

# Verificar logs
docker logs studyflow-postgres
```

### Porta já em uso
```bash
# Achar processo usando porta 8080
netstat -ano | findstr :8080

# Achar processo usando porta 4200
netstat -ano | findstr :4200

# Matar processo (Windows)
taskkill /PID <PID> /F
```

---

## 📝 COMMITS & GIT

```bash
# Clonar (primeira vez)
git clone <url> studyflow-saas

# Criar branch para feature
git checkout -b feature/nome-feature

# Fazer commit
git add .
git commit -m "feat: descrição concisa"

# Push para remote
git push origin feature/nome-feature

# Merge para main (após PR review)
git checkout main
git merge feature/nome-feature
git tag v0.1.0
```

---

## 🚀 DEPLOY (Próximo)

```bash
# Build para produção
mvnw clean package -DskipTests -Pprod

# Build imagem Docker
docker build -t studyflow-backend:v1.0.0 .

# Push para registry
docker tag studyflow-backend:v1.0.0 seu-registry/studyflow-backend:v1.0.0
docker push seu-registry/studyflow-backend:v1.0.0

# Deploy em Railway/Heroku/AWS
# (depende da plataforma)
```

---

## 📊 ENDPOINTS PRINCIPAIS

```
POST   /auth/login           - Fazer login
POST   /users                - Registrar usuário
GET    /users/me             - Obter perfil (protegido)
GET    /users                - Listar usuários (protegido)
POST   /study-items          - Criar estudo (protegido)
GET    /study-items          - Listar estudos (protegido)
GET    /actuator/health      - Verificar saúde
GET    /swagger-ui.html      - API Docs
```

---

## 📌 DICAS IMPORTANTES

1. **Sempre rodar testes antes de commit**
   ```bash
   mvnw test
   ```

2. **Não commitar credenciais**
   - Usar `.env` para secrets
   - Adicionar `.env` ao `.gitignore`

3. **Manter logs**
   ```bash
   docker logs studyflow-backend > logs.txt
   ```

4. **Backup regular do database**
   ```bash
   pg_dump -h localhost -U studyflow -d studyflow_db > backup_$(date +%Y%m%d).sql
   ```

5. **Monitorar performance**
   ```bash
   docker stats
   ```

---

## 🆘 CONTATOS & RESOURCES

- **Backend Issues:** Verificar `docker logs studyflow-backend`
- **Database Issues:** Verificar `docker logs studyflow-postgres`
- **Frontend Issues:** Verificar console do browser (F12)
- **Tests Failed:** Executar `mvnw test -X` para verbose

---

**Última atualização:** 3 de Abril de 2026  
**Versão:** 1.0



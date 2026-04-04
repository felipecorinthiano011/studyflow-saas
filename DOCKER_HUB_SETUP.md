# 🐳 DOCKER HUB SETUP - GUIA COMPLETO

**Data:** 3 de Abril de 2026  
**Status:** Configuração Docker Hub

---

## ✅ PASSO 1: LOGIN NO DOCKER LOCALMENTE

```bash
# Fazer login no Docker Hub
docker login

# Será pedido:
# Username: seu_username
# Password: seu_token (não a senha da conta!)

# Confirmar login
docker login --username seu_username
```

**Resultado esperado:**
```
Login Succeeded
Logging in with your password grants Docker CLI access to all your repositories.
```

---

## 🏗️ PASSO 2: BUILD DA IMAGEM LOCAL

### Opção A: Build automático com Docker Compose (recomendado)

```bash
cd C:\Projects\studyflow-saas\backend

# Build com tag do Docker Hub
docker-compose build

# Ou com nome específico
docker build -t seu_username/studyflow-backend:latest .
```

### Opção B: Build manual

```bash
cd C:\Projects\studyflow-saas\backend

# Build local
docker build -t studyflow-backend:latest .

# Tag para Docker Hub
docker tag studyflow-backend:latest seu_username/studyflow-backend:latest
docker tag studyflow-backend:latest seu_username/studyflow-backend:v1.0.0
```

---

## 📤 PASSO 3: PUSH PARA DOCKER HUB

```bash
# Push para Docker Hub
docker push seu_username/studyflow-backend:latest
docker push seu_username/studyflow-backend:v1.0.0

# Ver progresso
# [=====>                  ] 50MB/100MB
# [==========>             ] 100MB/100MB
# Pushed successfully!
```

---

## ✅ PASSO 4: VERIFICAR NO DOCKER HUB

Vá para: `https://hub.docker.com/r/seu_username/studyflow-backend`

Você deve ver:
- ✅ Tags criadas (latest, v1.0.0)
- ✅ Tamanho da imagem
- ✅ Data de push
- ✅ Instruções de pull

---

## 🔐 GUARDAR CREDENTIALS

### Opção A: Guardar credenciais localmente (automático)

```bash
# Já feito ao fazer login
# Arquivo salvo em:
# Windows: %USERPROFILE%\.docker\config.json
# Mac/Linux: ~/.docker/config.json
```

### Opção B: Usar .env (não recomendado)

```bash
# Criar arquivo .env.local
cat > .env.local << EOF
DOCKER_USERNAME=seu_username
DOCKER_PASSWORD=seu_token
EOF

# NUNCA commit .env.local!
```

---

## 🔄 WORKFLOW COMPLETO (Desenvolvimento)

```bash
# 1. Fazer mudanças no código
# ... editar arquivo ...

# 2. Compilar e testar localmente
cd backend
./mvnw clean package -DskipTests

# 3. Build Docker
docker build -t seu_username/studyflow-backend:latest .

# 4. Testar imagem
docker run -p 8080:8080 seu_username/studyflow-backend:latest

# 5. Push para Docker Hub
docker push seu_username/studyflow-backend:latest

# 6. Commit no Git
git add .
git commit -m "feat: nova feature"
git push origin main

# GitHub Actions vai:
# - Rodar testes
# - Build Docker
# - Push automático
```

---

## 📊 DOCKER HUB DASHBOARD

Acessar: `https://hub.docker.com/r/seu_username/studyflow-backend`

Você pode:
- ✅ Ver histórico de pushes
- ✅ Gerenciar tags
- ✅ Ver tamanho das imagens
- ✅ Configurar descrição
- ✅ Gerenciar colaboradores

---

## 🔍 VERIFICAR IMAGEM NO DOCKER HUB

```bash
# Listar imagens locais
docker images

# Output:
# REPOSITORY                           TAG        IMAGE ID
# seu_username/studyflow-backend       latest     abc123...
# seu_username/studyflow-backend       v1.0.0     abc123...

# Ver tamanho
docker images --format "table {{.Repository}}\t{{.Size}}"
```

---

## 🚀 PULL IMAGEM DO DOCKER HUB (Depois)

```bash
# Em qualquer máquina:
docker pull seu_username/studyflow-backend:latest

# Rodar
docker run -p 8080:8080 seu_username/studyflow-backend:latest
```

---

## 📝 ACTUALIZAR DOCKER-COMPOSE PARA DOCKER HUB

Se quiser usar imagem do Docker Hub em vez de build local:

**Antes (build local):**
```yaml
backend:
  build: .  # Build local
  image: studyflow-backend:latest
```

**Depois (Docker Hub):**
```yaml
backend:
  image: seu_username/studyflow-backend:latest  # Pull do Docker Hub
  pull_policy: always
  environment:
    # ...configurações...
```

---

## 🔄 CI/CD AUTOMÁTICO (GitHub Actions)

Quando você faz `git push`:

```
1. GitHub Actions inicia
2. Build projeto
3. Build Docker
4. Login no Docker Hub (usa DOCKER_USERNAME e DOCKER_PASSWORD)
5. Push automático
6. ✅ Pronto em 5 minutos!
```

Não precisa fazer manualmente!

---

## ⚠️ BOAS PRÁTICAS

✅ **Faça:**
```bash
# Use versionamento semântico
docker tag app:v1.0.0
docker tag app:v1.0
docker tag app:latest
```

✅ **Use .dockerignore**
```
node_modules
target
.git
.env
```

✅ **Use secrets para credenciais**
```bash
# Nunca coloque senhas em código!
```

❌ **Evite:**
```bash
# Não use tokens no código
docker build -e DOCKER_PASSWORD=xxx

# Não faça push de desenvolvimento
docker push dev-version:abc123
```

---

## 🆘 TROUBLESHOOTING

### "Unauthorized: authentication required"
```bash
# Solução: Fazer login novamente
docker logout
docker login
# Coloque seu username e token
```

### "Denied: requested access to the resource is denied"
```bash
# Motivo: Imagem privada
# Solução: Tornar pública no Docker Hub
# Ou verificar se username está correto
```

### "No such file or directory"
```bash
# Motivo: Não está no diretório correto
# Solução:
cd C:\Projects\studyflow-saas\backend
docker build -t seu_username/studyflow-backend .
```

### "Image not found"
```bash
# Verificar se o Docker está rodando
docker ps

# Verificar imagens disponíveis
docker images
```

---

## 📊 EXEMPLO COMPLETO

```bash
# 1. Navegar para backend
cd C:\Projects\studyflow-saas\backend

# 2. Login no Docker Hub
docker login

# 3. Build imagem
docker build -t seunome/studyflow-backend:v1.0.0 .

# 4. Testar localmente
docker run -p 8080:8080 seunome/studyflow-backend:v1.0.0
# Testar em http://localhost:8080

# 5. Tag como latest
docker tag seunome/studyflow-backend:v1.0.0 seunome/studyflow-backend:latest

# 6. Push para Docker Hub
docker push seunome/studyflow-backend:v1.0.0
docker push seunome/studyflow-backend:latest

# 7. Verificar no Docker Hub
# https://hub.docker.com/r/seunome/studyflow-backend

# 8. Commit no Git
git add .
git commit -m "chore: push docker image v1.0.0"
git push origin main
```

---

## 🎯 PRÓXIMOS PASSOS

1. ✅ Já feito: Token no GitHub Actions
2. 👉 Agora: Login no Docker localmente
3. Depois: Build local e push (para testar)
4. Finalmente: GitHub Actions faz tudo automático

---

**Data:** 3 de Abril de 2026  
**Versão:** Docker Hub Setup 1.0



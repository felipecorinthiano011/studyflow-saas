# 🧪 TESTES DO DOCKER HUB - GUIA PRÁTICO

**Data:** 3 de Abril de 2026  
**Status:** Testes de Verificação

---

## ✅ TESTE 1: VERIFICAR DOCKER INSTALADO

```powershell
docker --version
```

**Resultado esperado:**
```
Docker version 26.1.0, build ...
```

**Se falhar:**
- [ ] Baixar Docker Desktop: https://www.docker.com/products/docker-desktop
- [ ] Reiniciar o computador após instalar

---

## ✅ TESTE 2: FAZER LOGIN NO DOCKER

```powershell
docker login
```

**Será pedido:**
```
Username: seu_username_docker_hub
Password: seu_token (não sua senha!)
```

**Resultado esperado:**
```
Login Succeeded
```

**Se falhar:**
- [ ] Verificar username (não é email)
- [ ] Usar token, não senha da conta
- [ ] Token pode ter expirado, gerar novo

---

## ✅ TESTE 3: BUILD LOCAL

```powershell
# Navegar para backend
cd C:\Projects\studyflow-saas\backend

# Build com nome correto
docker build -t seu_username/studyflow-backend:test .
```

**Resultado esperado:**
```
[1/10] FROM openjdk:21-slim
[2/10] WORKDIR /app
...
[10/10] BUILD SUCCESS
Successfully tagged seu_username/studyflow-backend:test
```

**Se falhar:**
```
[ERROR] Failed to execute goal
```
→ Executar: `mvnw clean package -DskipTests` no backend primeiro

---

## ✅ TESTE 4: LISTAR IMAGENS BUILT

```powershell
docker images
```

**Resultado esperado:**
```
REPOSITORY                              TAG      IMAGE ID      CREATED
seu_username/studyflow-backend         test     abc123def     2 minutes ago
seu_username/studyflow-backend         latest   ...           ...
```

**Se não aparecer:**
→ Build falhou no teste 3

---

## ✅ TESTE 5: RODAR IMAGEM LOCALMENTE

```powershell
# Rodar container
docker run -p 8080:8080 seu_username/studyflow-backend:test

# Ou rodar em background
docker run -d -p 8080:8080 seu_username/studyflow-backend:test
```

**Resultado esperado:**
```
Container ID: abc123def456
...
Started application in 15 seconds
```

**Verificar em navegador:**
```
http://localhost:8080/actuator/health
```

Deve retornar:
```json
{
  "status": "UP"
}
```

**Se não funcionar:**
- [ ] Porta 8080 já em uso? Usar: `docker run -p 9000:8080 ...`
- [ ] Database desconectado? Rodar docker-compose primeiro

---

## ✅ TESTE 6: PUSH PARA DOCKER HUB

```powershell
# Tag como latest
docker tag seu_username/studyflow-backend:test seu_username/studyflow-backend:latest

# Push para Docker Hub
docker push seu_username/studyflow-backend:latest
```

**Resultado esperado:**
```
The push refers to repository [docker.io/seu_username/studyflow-backend]
test: digest: sha256:abc123... size: 456MB
Pushed successfully
```

**Se falhar:**
- [ ] Não fez login? Executar `docker login` novamente
- [ ] Token expirado? Gerar novo token no Docker Hub
- [ ] Username errado? Verificar em https://hub.docker.com/settings/general

---

## ✅ TESTE 7: VERIFICAR NO DOCKER HUB

Abrir navegador:
```
https://hub.docker.com/r/seu_username/studyflow-backend
```

**Você deve ver:**
- ✅ Imagem aparece lá
- ✅ Tag "latest" listado
- ✅ Tamanho da imagem (~500MB)
- ✅ Data de push
- ✅ Descrição (se preencheu)

---

## ✅ TESTE 8: PULL DE OUTRA MÁQUINA (OU NOVA PASTA)

```powershell
# Deletar imagem local para testar pull
docker rmi seu_username/studyflow-backend:latest

# Fazer pull
docker pull seu_username/studyflow-backend:latest

# Rodar
docker run -p 8080:8080 seu_username/studyflow-backend:latest
```

**Resultado esperado:**
```
Pulling from seu_username/studyflow-backend
...
Status: Downloaded newer image
```

**Se conseguir rodar em outra máquina:**
✅ Docker Hub está funcionando!

---

## ✅ TESTE 9: VERIFICAR GITHUB ACTIONS

Ir para:
```
https://github.com/felipecorinthiano011/studyflow-saas/actions
```

**Você deve ver:**
- ✅ Workflows listados
- ✅ Status (verde = sucesso, vermelho = falha)
- ✅ Histórico de runs

**Testar manualmente:**
```bash
git push origin main
# Aguarde 5 minutos
# Vá em Actions
# Veja workflow rodando
```

---

## ✅ TESTE 10: VERIFICAR LOGS DO GITHUB ACTIONS

Na página de Actions:
```
1. Clique no workflow mais recente
2. Clique em "ci-cd" ou "build"
3. Expanda cada step
4. Procure por:
   ✅ "Docker build" com sucesso
   ✅ "Docker push" com sucesso
```

**Se houver erro:**
- [ ] Ver mensagem exata do erro
- [ ] Verificar se secrets estão corretos
- [ ] Tentar executar localmente para debug

---

## 🎬 TESTE RÁPIDO COMPLETO (5 MINUTOS)

```powershell
# 1. Login
docker login

# 2. Build
cd C:\Projects\studyflow-saas\backend
docker build -t seu_username/studyflow-backend:v1 .

# 3. Rodar
docker run -p 8080:8080 seu_username/studyflow-backend:v1

# 4. Em outro PowerShell, testar
curl http://localhost:8080/actuator/health

# 5. Se funcionar, push
docker push seu_username/studyflow-backend:v1

# 6. Verificar no Docker Hub
# https://hub.docker.com/r/seu_username/studyflow-backend
```

**Tempo total:** ~5 minutos

---

## 📊 CHECKLIST DE TESTES

```
[ ] Docker --version funciona
[ ] Docker login bem-sucedido
[ ] Build local funciona
[ ] Docker images lista sua imagem
[ ] Container roda localmente
[ ] Health check retorna 200
[ ] Push para Docker Hub funciona
[ ] Imagem aparece no Docker Hub
[ ] Pull de outra máquina funciona
[ ] GitHub Actions workflow passa
[ ] Secrets estão corretos no GitHub
[ ] CI/CD automático funciona (git push)
```

---

## 🆘 TROUBLESHOOTING

### "docker: command not found"
```
❌ Docker não está instalado
✅ Solução: Instalar Docker Desktop
```

### "no basic auth credentials"
```
❌ Não fez login
✅ Solução: docker login
```

### "denied: requested access to the resource is denied"
```
❌ Username ou senha errado
✅ Solução: Verificar username e regenerar token
```

### "error building image"
```
❌ Erro na compilação Java
✅ Solução: mvnw clean package -DskipTests (testar antes)
```

### "Unauthorized" no GitHub Actions
```
❌ DOCKER_USERNAME ou DOCKER_PASSWORD errado no GitHub
✅ Solução: Verificar secrets em Settings → Secrets
```

### "Connection refused" no localhost:8080
```
❌ Porta já em uso
✅ Solução: docker run -p 9000:8080 ...
```

---

## 📈 SINAIS DE SUCESSO

✅ **Tudo funcionando:**
- Docker build completa sem erros
- Container roda e responde em localhost:8080
- Imagem aparece no Docker Hub
- GitHub Actions passa em todas as steps
- Pull de outra máquina funciona

✅ **CI/CD Automático funcionando:**
- git push dispara workflow
- Testes rodam automaticamente
- Docker build acontece
- Push para Docker Hub automático
- Imagem atualizada no Docker Hub

---

## 🎯 PRÓXIMO PASSO

Após todos os testes passarem:

1. ✅ Docker Hub funcionando
2. ✅ CI/CD automático funcionando
3. 👉 Próximo: Configurar Railway para produção

---

**Data:** 3 de Abril de 2026  
**Versão:** Testes 1.0



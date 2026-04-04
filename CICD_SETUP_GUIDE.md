# 🔄 CI/CD CONFIGURATION GUIDE

**Data:** 3 de Abril de 2026  
**Status:** Setup Completo

---

## ✅ O QUE FOI CONFIGURADO

```
.github/workflows/
├── ci-cd.yml          - Pipeline de build, test e segurança
├── deploy.yml         - Deployment automático para Railway
└── sonar-project.properties - Configuração SonarQube
```

---

## 🎯 PIPELINE CI/CD EXPLICADO

### 1. **CI Pipeline (Continuous Integration)**

Executa automaticamente quando você faz `push` ou `pull request`:

```yaml
┌─────────────────────────────────────────┐
│  1. Checkout código                     │
├─────────────────────────────────────────┤
│  2. Setup Java 21 + Maven              │
├─────────────────────────────────────────┤
│  3. Build Backend                       │
├─────────────────────────────────────────┤
│  4. Rodar testes de integração          │
├─────────────────────────────────────────┤
│  5. Rodar testes de performance         │
├─────────────────────────────────────────┤
│  6. Gerar relatório de cobertura (JaCoCo)
├─────────────────────────────────────────┤
│  7. Upload para Codecov                 │
├─────────────────────────────────────────┤
│  8. Análise SonarQube                   │
├─────────────────────────────────────────┤
│  9. Scan de segurança (Trivy)           │
└─────────────────────────────────────────┘
```

### 2. **Docker Build (Quando main branch)**

Automático quando tudo passa:

```yaml
┌─────────────────────────────────────────┐
│  1. Build imagem Docker                 │
├─────────────────────────────────────────┤
│  2. Push para Docker Hub                │
│     (tag: latest + sha)                │
└─────────────────────────────────────────┘
```

### 3. **Deploy (Manual ou Release)**

Quando você quer fazer deploy:

```yaml
┌─────────────────────────────────────────┐
│  1. Checkout código                     │
├─────────────────────────────────────────┤
│  2. Build projeto                       │
├─────────────────────────────────────────┤
│  3. Build Docker                        │
├─────────────────────────────────────────┤
│  4. Deploy para Railway                 │
├─────────────────────────────────────────┤
│  5. Health check                        │
├─────────────────────────────────────────┤
│  6. Notificar Slack                     │
└─────────────────────────────────────────┘
```

---

## 🔐 SECRETS NECESSÁRIOS

Configure estes secrets no GitHub (Settings > Secrets):

### Essenciais:

1. **DOCKER_USERNAME**
   ```
   Seu username no Docker Hub
   Obter em: https://hub.docker.com
   ```

2. **DOCKER_PASSWORD**
   ```
   Seu token de acesso Docker Hub
   Criar em: https://hub.docker.com/settings/security
   ```

3. **RAILWAY_TOKEN**
   ```
   Token do Railway
   Obter em: https://railway.app/settings/tokens
   ```

### Opcionais (configure depois):

4. **SONAR_TOKEN** (SonarCloud)
   ```
   Token SonarCloud
   Obter em: https://sonarcloud.io/account/security
   ```

5. **SLACK_WEBHOOK** (Notificações)
   ```
   Webhook do Slack
   Criar em: Slack App > Webhooks
   ```

6. **RAILWAY_PROJECT_ID**
   ```
   ID do projeto no Railway
   Copiar da URL do projeto
   ```

7. **RAILWAY_ENVIRONMENT_ID**
   ```
   ID do environment no Railway
   Copiar das configurações
   ```

---

## 📋 COMO CONFIGURAR SECRETS NO GITHUB

1. Vá para: `https://github.com/felipecorinthiano011/studyflow-saas`
2. Settings → Secrets and variables → Actions
3. Clique em "New repository secret"
4. Adicione cada secret:
   - Name: `DOCKER_USERNAME`
   - Value: `seu_username`

Repita para cada secret.

---

## 🚀 COMO USAR O CI/CD

### Automaticamente (Com push):

```bash
# Push para main ou develop
git push origin main

# GitHub Actions executará automaticamente:
# 1. Build + Testes
# 2. Docker build (se main)
# 3. SonarQube scan
# 4. Trivy security scan
```

### Deploy Manual:

Opção 1 - Via workflow_dispatch:
```
1. Vá para: Actions
2. Clique em "Deploy to Production"
3. "Run workflow"
4. Escolha ambiente (staging/production)
```

Opção 2 - Via release:
```bash
# Criar tag e release
git tag v1.0.0
git push origin v1.0.0

# Ou via GitHub: Create Release
```

---

## 📊 VISUALIZAR RESULTADOS

### GitHub Actions:
```
https://github.com/felipecorinthiano011/studyflow-saas/actions
```

### SonarCloud:
```
https://sonarcloud.io/dashboard?id=felipecorinthiano011_studyflow-saas
```

### Codecov:
```
https://codecov.io/github/felipecorinthiano011/studyflow-saas
```

### Docker Hub:
```
https://hub.docker.com/r/seu_username/studyflow-backend
```

---

## 🔍 CHECKLIST DE CONFIGURAÇÃO

### GitHub Secrets:
- [ ] DOCKER_USERNAME
- [ ] DOCKER_PASSWORD
- [ ] RAILWAY_TOKEN
- [ ] SONAR_TOKEN (opcional)
- [ ] SLACK_WEBHOOK (opcional)
- [ ] RAILWAY_PROJECT_ID (para deploy)
- [ ] RAILWAY_ENVIRONMENT_ID (para deploy)

### Accounts Criadas:
- [ ] Docker Hub (https://hub.docker.com)
- [ ] SonarCloud (https://sonarcloud.io) - opcional
- [ ] Codecov (https://codecov.io) - opcional
- [ ] Railway (https://railway.app)
- [ ] Slack Integration (opcional)

### Testes Validados:
- [ ] Tests passando localmente
- [ ] Docker build funciona
- [ ] Health check OK

---

## 💡 DICAS

1. **CI/CD sempre passa?**
   - Verifique se os secrets estão corretos
   - Veja os logs: Actions → seu workflow → run

2. **Quer desabilitar algo?**
   - Comente a step no YAML

3. **Adicionar novo step?**
   - Veja: https://github.com/actions/

4. **Precisar de debug?**
   ```yaml
   - run: env | sort  # Ver todas as variáveis
   ```

---

## 📚 ARQUIVOS CRIADOS

1. `.github/workflows/ci-cd.yml`
   - Build, testes, segurança, SonarQube

2. `.github/workflows/deploy.yml`
   - Deploy para Railway com health check

3. `sonar-project.properties`
   - Configuração do SonarCloud

---

## 🎯 PRÓXIMOS PASSOS

1. **Criar contas:**
   - [ ] Docker Hub (grátis)
   - [ ] Railway (starter $5/mês)

2. **Adicionar secrets:**
   - [ ] DOCKER_USERNAME + DOCKER_PASSWORD
   - [ ] RAILWAY_TOKEN

3. **Testar pipeline:**
   - [ ] Fazer push em uma branch
   - [ ] Ver em Actions se passou

4. **Configurar deploy:**
   - [ ] Criar projeto no Railway
   - [ ] Adicionar RAILWAY_TOKEN e IDs

---

## ⚠️ IMPORTANTE

- **Nunca commit secrets** em código!
- Sempre use GitHub Secrets
- Tokens são privados - guarde bem
- Regenere tokens a cada ano

---

## 🆘 TROUBLESHOOTING

### "Secret not found"
→ Verifique o nome exato (case-sensitive)

### "Docker push failed"
→ DOCKER_USERNAME ou PASSWORD incorreto

### "SonarQube analysis failed"
→ SONAR_TOKEN inválido ou não configurado

### "Railway deploy failed"
→ RAILWAY_TOKEN expirado, obter novo

---

**Data:** 3 de Abril de 2026  
**Versão:** CI/CD 1.0



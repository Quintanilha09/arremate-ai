# Decisões Técnicas - ArremateAI

Este documento registra as decisões de tecnologia e arquitetura do projeto ArremateAI.

---

## Stack Tecnológico Definido

### Backend
- **Linguagem**: Java 17 (LTS)
- **Framework**: Spring Boot 3.2.2
- **Arquitetura**: MVC (Controller → Service → Repository)
- **Banco de Dados Principal**: PostgreSQL
- **Cache**: Redis (ElastiCache na AWS)
- **Busca (futuro)**: OpenSearch/Elasticsearch (quando necessário)

### Frontend
- **Linguagem**: TypeScript
- **Framework**: React
- **SSR/SEO**: Next.js (para otimização de SEO e performance)
- **Estilo**: Tailwind CSS (ou Material UI - definir)
- **State/API**: TanStack Query (React Query)

### Infraestrutura
- **Cloud Provider**: AWS
- **Compute**: ECS/Fargate (ou Elastic Beanstalk/App Runner para MVP)
- **Banco**: RDS PostgreSQL
- **Cache**: ElastiCache Redis
- **Storage**: S3 + CloudFront (para assets/imagens)
- **Mensageria**: SQS (para jobs de ingestão)
- **Observabilidade**: CloudWatch + logs estruturados

---

## Arquitetura: MVC Pragmático

### Estrutura de Pacotes (Backend)

```
src/main/java/com/leilao/arremateai/
├── controller/          # REST endpoints (HTTP)
├── service/             # Regras de negócio e orquestração
├── repository/          # Acesso a dados (JPA)
├── domain/              # Entidades de domínio + enums
├── dto/                 # Request/Response DTOs
├── integration/         # Clientes para APIs externas (PNCP, Superbid, etc.)
│   ├── pncp/
│   ├── superbid/
│   └── megaleiloes/
├── mapper/              # Normalização de dados (fonte → domínio)
├── job/                 # Schedulers de sincronização
├── config/              # Configurações (Security, OpenAPI, etc.)
├── exception/           # Exceções customizadas + handler global
└── util/                # Utilitários
```

### Princípios

✅ **Controller**: Apenas HTTP (request/response, validação `@Valid`, status codes)  
✅ **Service**: Regras de negócio, orquestração, transações (`@Transactional`)  
✅ **Repository**: Acesso a dados (JPA/SQL), sem lógica de negócio  
✅ **DTOs separados**: Não expor entidades JPA diretamente na API  
✅ **Integrações isoladas**: Um client/adapter por fonte (PNCP, Superbid, etc.)  
✅ **Normalização explícita**: Mapper/Service para converter dados externos → modelo interno  

❌ **Evitar**: Services gigantes, entidades JPA em payloads HTTP, regras no Controller/Repository, interfaces desnecessárias

---

## Integrações Priorizadas (MVP)

### Fase 1: Primeiras Fontes (Mês 1-2)
1. **PNCP** (API pública) - Prioridade MÁXIMA
2. **Superbid Exchange** (credenciamento B2B) - Iniciar contato

### Fase 2: Expansão (Mês 3-4)
3. **Mega Leilões** (API REST/SOAP)
4. **Plataforma White Label** (avaliar parceria)

### Fase 3: Bancos (Mês 5-6)
5. **Scraping/Apify** para Caixa e Santander
6. Segunda plataforma White Label

---

## Modelo de Dados (Entidades Principais)

### PostgreSQL (Relacional)

```sql
-- Leiloeira/Fonte
leiloeira
  id, nome, url, tipo_integracao (API/SCRAPING), 
  configuracao_json, status, ultima_sincronizacao

-- Leilão
leilao
  id, leiloeira_id, titulo, descricao, 
  data_inicio, data_encerramento, localizacao, status

-- Produto/Lote
produto
  id, leilao_id, leiloeira_id, titulo, descricao,
  categoria, subcategoria, condicao,
  valor_avaliacao, lance_minimo, lance_atual,
  fotos_urls[], especificacoes_jsonb,
  localizacao, data_limite, status,
  payload_original_jsonb

-- Usuário
usuario
  id, nome, email, senha_hash, role, 
  preferencias_jsonb, created_at

-- Favoritos
favorito
  id, usuario_id, produto_id, created_at

-- Alertas
alerta
  id, usuario_id, criterios_jsonb (categoria, preco_max, etc.), 
  ativo, created_at
```

### Redis (Cache)
- Cache de buscas populares (`search:{hash}`)
- Rate limiting (`ratelimit:{ip}`)
- Sessões de usuário (se não usar JWT stateless)

---

## Fluxo de Ingestão (Jobs Agendados)

```
Scheduler (ex.: @Scheduled 1x/dia)
  → ChaveLeiloeira (ex.: PNCP)
    → PNCPClient.buscarLeiloes()
      → Normaliza (PNCPMapper)
        → Salva no PostgreSQL (LeilaoRepository)
          → Publica evento (opcional: SQS)
            → Log/Métrica (sincronização OK)
```

---

## Checklist de Validações

- [x] Stack backend definido (Java + Spring Boot + PostgreSQL)
- [x] Stack frontend definido (React + TypeScript + Next.js)
- [x] Arquitetura escolhida (MVC pragmático)
- [x] Hospedagem definida (AWS)
- [x] Integrações mapeadas (PNCP, Superbid, Mega, White Label, Bancos)
- [x] Modelo de dados esboçado
- [ ] **Setup inicial do projeto backend (Spring Boot)**
- [ ] **Setup inicial do projeto frontend (Next.js)**
- [ ] **Configurar banco local (Docker Compose: PostgreSQL + Redis)**
- [ ] **Implementar primeiro endpoint (health check)**
- [ ] **Implementar primeira integração (PNCP)**
- [ ] **Configurar CI/CD básico**
- [ ] **Deploy MVP na AWS**

---

## Observabilidade

- **Logs estruturados**: JSON com correlation ID, timestamp, level, contexto
- **Métricas**: Quantidade de itens sincronizados, latência de integração, cache hit rate
- **Alertas**: Falha de sincronização, queda de fonte, latência alta

---

## Segurança

- Autenticação: JWT (stateless) ou Spring Session + Redis
- Autorização: Roles (USER, ADMIN)
- Rate limiting: Por IP/usuário (Redis)
- HTTPS obrigatório em produção
- Validação de input: Bean Validation (`@Valid`)
- Secrets: AWS Secrets Manager

---

## Próximos Passos Imediatos

1. **Setup de ambiente local** (Docker Compose)
2. **Criar projeto Spring Boot** (skeleton com OpenAPI, PostgreSQL, Redis)
3. **Criar projeto Next.js** (skeleton com TypeScript, Tailwind)
4. **Implementar primeiro endpoint** (`GET /health`, `GET /api/produtos`)
5. **Integrar PNCP** (client + mapper + scheduler)
6. **Testar localmente** (Postman/Insomnia + frontend)
7. **Deploy MVP** (AWS ECS/Fargate ou App Runner)

---

*Última atualização: 30/01/2026*

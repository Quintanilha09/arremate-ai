# 🎯 Roadmap ArremateAI - MVP para Apresentação

## 📊 Análise de Funcionalidades dos Concorrentes

### **QuintoAndar** (Plataforma de Aluguel/Compra)
- Sistema de busca avançada com múltiplos filtros
- Visualização em cards/lista com imagens destacadas
- Sistema de favoritos e comparação de imóveis
- Tour virtual e galeria de fotos
- Informações detalhadas: localização, metragem, comodidades
- Sistema de alertas personalizados
- Integração com mapa (geolocalização)
- Processo de aplicação/interesse direto na plataforma
- Dashboard com histórico de buscas e interações

### **OLX** (Marketplace Generalista)
- Filtros dinâmicos por categoria, preço, localização
- Sistema de mensagens entre comprador/vendedor
- Destaque de anúncios premium
- Avaliação/reputação de vendedores
- Upload múltiplo de imagens
- Compartilhamento social dos anúncios
- Busca por texto livre e sugestões
- Categorização hierárquica (categorias e subcategorias)

### **Airbnb** (Hospedagem)
- UI/UX excepcional com foco em imagens
- Sistema de avaliações e reviews
- Filtros avançados: datas, capacidade, comodidades
- Sistema de reserva integrado
- Wishlist/favoritos
- Experiência mobile-first
- Mapa interativo
- Sistema de notificações em tempo real
- Perfis de usuário ricos

---

## 🎨 Diretrizes de Design (Baseado em QuintoAndar + Airbnb)

### Princípios Visuais:
- **Clean & Minimalista**: Espaços em branco, tipografia legível
- **Mobile-First**: Design responsivo desde o início
- **Foco em Imagens**: Cards visuais, galerias de fotos
- **Micro-interações**: Hover effects, transições suaves
- **Paleta de Cores**: Neutras com acentos vibrantes (azul/verde confiança)

---

## 📋 Status Atual do Backend
✅ 30+ imóveis mockados disponíveis via API  
✅ Endpoint GET /api/imoveis (listar todos com filtros avançados)  
✅ Endpoint GET /api/imoveis/{id} (buscar por ID)  
✅ Endpoint POST /api/imoveis (cadastrar com validações)  
✅ Integração com Brasil API (nomes de bancos reais)  
✅ Busca textual por descrição  
✅ Filtros por UF, cidade, instituição, valor, tipo  
✅ Paginação e ordenação  
✅ Validações com Bean Validation  
✅ Tratamento global de exceções (GlobalExceptionHandler)

---


## 🚀 FASE 1: FUNDAÇÃO DO BACKEND (Semanas 1-2) - **ALTA PRIORIDADE**

### 1.1 Completar CRUD de Imóveis ⭐ **CONCLUÍDO** ✅
**Objetivo:** Operações completas de gerenciamento

**Tarefas:**
- [x] GET (listar todos) - **CONCLUÍDO**
- [x] GET /{id} (buscar por ID) - **CONCLUÍDO**
- [x] POST (cadastrar) - **CONCLUÍDO**
- [x] **PUT /{id} (atualizar completo)** - **CONCLUÍDO** ✅
- [x] **PATCH /{id} (atualizar parcial)** - **CONCLUÍDO** ✅
- [x] **DELETE /{id} (remover)** - **CONCLUÍDO** (Soft delete) ✅

**Endpoints implementados:**
```java
PUT    /api/imoveis/{id}      // Atualização completa ✅
PATCH  /api/imoveis/{id}      // Atualização parcial ✅
DELETE /api/imoveis/{id}      // Remoção (soft delete) ✅
```

**Critérios de Aceite:**
- [x] Validações mantidas no PUT/PATCH
- [x] Histórico de alterações (auditoria: updatedAt, createdAt)
- [x] Retornar 404 se imóvel não existir
- [x] Soft delete implementado (campo ativo)
- [x] Filtro automático para listar apenas ativos
- [x] Testes manuais documentados em TESTES_CRUD.md

📝 **Documentação:** Ver [FASE_1.1_CONCLUIDA.md](FASE_1.1_CONCLUIDA.md)

---

### 1.2 Expandir Modelo de Dados ⭐ **CONCLUÍDO** ✅
**Objetivo:** Enriquecer dados dos imóveis para competir com QuintoAndar/OLX

**Novos campos implementados na entidade `Imovel`:**
- [x] Quartos, banheiros, vagas
- [x] Endereço completo, CEP
- [x] Latitude e longitude (coordenadas GPS)
- [x] Condição (NOVO, USADO, REFORMADO)
- [x] Aceita financiamento
- [x] Observações
- [x] Status (DISPONIVEL, VENDIDO, SUSPENSO)

**Entidade `ImagemImovel` criada:**
- [x] Relacionamento 1:N com Imovel
- [x] URL, legenda, principal, ordem
- [x] Repository com queries customizadas

**Tarefas:**
- [x] Criar migration V003 para adicionar novos campos
- [x] Atualizar DTOs (Request/Response) com validações
- [x] Atualizar Mapper com todos os campos
- [x] Atualizar validações (CEP, coordenadas, enums)
- [x] Criar entidade ImagemImovel
- [x] Criar repository ImagemImovelRepository
- [x] Atualizar Service (PUT e PATCH)
- [x] Compilação bem-sucedida ✅

📝 **Documentação:** Ver [FASE_1.2_CONCLUIDA.md](FASE_1.2_CONCLUIDA.md) e [TESTES_FASE_1.2.md](TESTES_FASE_1.2.md)

---

### 1.3 Sistema de Upload de Imagens ⭐ **ALTA PRIORIDADE**
```java
// Informações essenciais
private Integer quartos;              // Número de quartos
private Integer banheiros;            // Número de banheiros
private Integer vagas;                // Vagas de garagem
private String endereco;              // Endereço completo
private String cep;                   // CEP
private BigDecimal latitude;          // Coordenadas para mapa
private BigDecimal longitude;         // Coordenadas para mapa

// Informações adicionais
private String condicao;              // Novo, Usado, Reformado
private Boolean aceitaFinanciamento;  // Aceita financiamento bancário
private String observacoes;           // Observações gerais

// Imagens (relacionamento 1:N)
private List<ImagemImovel> imagens;   // Galeria de fotos

// Auditoria e controle
private LocalDateTime updatedAt;      // Data última atualização
private Boolean ativo;                // Soft delete
private String status;                // DISPONIVEL, VENDIDO, SUSPENSO
```

**Nova entidade `ImagemImovel`:**
```java
@Entity
public class ImagemImovel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "imovel_id")
    private Imovel imovel;
    
    private String url;               // URL da imagem
    private String legenda;           // Descrição da imagem
    private Boolean principal;        // Imagem de capa
    private Integer ordem;            // Ordem de exibição
    private LocalDateTime createdAt;
}
```

**Tarefas:**
- [ ] Criar migration para adicionar novos campos
- [ ] Atualizar DTOs (Request/Response)
- [ ] Atualizar Mapper
- [ ] Atualizar validações
- [ ] Criar entidade ImagemImovel
- [ ] Criar repository e service para imagens
- [ ] Atualizar dados mockados com novos campos

---

### 1.3 Sistema de Upload de Imagens ⭐ **CONCLUÍDO** ✅
**Objetivo:** Permitir múltiplas imagens por imóvel (inspirado em OLX/QuintoAndar)

**Tecnologias implementadas:**
- Spring Boot File Upload (multipart/form-data)
- Armazenamento local em disco (/uploads)
- Validação: formato (jpg, jpeg, png, webp), tamanho máximo (5MB)

**Endpoints implementados:**
```java
POST   /api/imoveis/{id}/imagens          // Upload múltiplo ✅
GET    /api/imoveis/{id}/imagens          // Listar imagens ✅
PUT    /api/imoveis/imagens/{imgId}       // Atualizar ordem/legenda ✅
DELETE /api/imoveis/imagens/{imgId}       // Remover imagem ✅
PATCH  /api/imoveis/imagens/{imgId}/principal  // Definir como principal ✅
```

**Recursos implementados:**
- [x] Configuração de upload (5MB/arquivo, 20MB/request)
- [x] ImagemService com validações rigorosas
- [x] ImagemController com todos os endpoints
- [x] Validação de formato e tamanho
- [x] Geração de UUID para nomes únicos
- [x] Definição automática de imagem principal
- [x] Remoção automática de arquivos físicos
- [x] Servir imagens via /uploads/{filename}

📝 **Documentação:** Ver [FASE_1.3_CONCLUIDA.md](FASE_1.3_CONCLUIDA.md)

---

### 1.4 Endpoints de Estatísticas e Dashboard ⭐ **ALTA PRIORIDADE**
**Objetivo:** Fornecer dados analíticos para dashboard (inspirado em Airbnb)

**Endpoints:**
```java
GET /api/imoveis/estatisticas
GET /api/imoveis/destaques
GET /api/imoveis/recentes
GET /api/imoveis/mais-procurados
```

**Exemplo de resposta `/api/imoveis/estatisticas`:**
```json
{
  "totalImoveis": 150,
  "totalPorUf": {
    "SP": 50,
    "RJ": 30,
    "MG": 20
  },
  "valorMedio": 500000,
  "valorMinimo": 150000,
  "valorMaximo": 2000000,
  "tiposMaisComuns": {
    "Apartamento": 45,
    "Casa": 35,
    "Terreno": 20
  },
  "instituicoesAtuantes": 15
}
```

**Tarefas:**
- [ ] Criar StatisticsService
- [ ] Implementar queries agregadas (JPA/JPQL)
- [ ] Criar DTOs de resposta
- [ ] Adicionar cache Redis (opcional)
- [ ] Testar performance com muitos registros

---

### 1.5 Sistema de Favoritos (Wishlist) ⭐ **CONCLUÍDO** ✅
**Objetivo:** Usuários salvam imóveis de interesse (inspirado em Airbnb)

**Modelo implementado:**
```java
@Entity
public class Favorito {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private String usuarioId;  // Temporário até Fase 2 (JWT)
    
    @ManyToOne
    @JoinColumn(name = "imovel_id")
    private Imovel imovel;
    
    private LocalDateTime createdAt;
}
```

**Endpoints implementados:**
```java
POST   /api/favoritos/{imovelId}           // Adicionar aos favoritos ✅
DELETE /api/favoritos/{imovelId}           // Remover dos favoritos ✅
GET    /api/favoritos                      // Listar favoritos do usuário ✅
GET    /api/favoritos/{imovelId}/status    // Verificar se é favorito ✅
GET    /api/favoritos/count                // Contar favoritos ✅
```

**Tarefas:**
- [x] Criar entidade Favorito
- [x] Criar repository com queries customizadas
- [x] Implementar service com validações
- [x] Implementar 5 endpoints REST
- [x] Adicionar validações (imóvel existe, não duplicar)
- [x] Migration V005 criada
- [x] Constraint única (usuario + imovel)
- [x] Cascade delete configurado
- [x] Índices otimizados
- [x] Tratamento de exceções
- [x] Compilação bem-sucedida ✅
- [x] Documentação completa ✅

**Header temporário:** `X-Usuario-Id` (será substituído por JWT na Fase 2)

📝 **Documentação:** Ver [FASE_1.5_CONCLUIDA.md](FASE_1.5_CONCLUIDA.md)

---

## 🔐 FASE 2: AUTENTICAÇÃO E USUÁRIOS (Semanas 3-4) - **ALTA PRIORIDADE**

### 2.1 Sistema de Usuários
**Objetivo:** Gestão de contas (comprador, vendedor, admin)

**Entidade Usuario:**
```java
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nome;
    private String email;          // Único
    private String senha;          // Bcrypt
    private String telefone;
    private String cpf;
    
    @Enumerated(EnumType.STRING)
    private TipoUsuario tipo;      // COMPRADOR, VENDEDOR, ADMIN
    
    private Boolean ativo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

enum TipoUsuario {
    COMPRADOR, VENDEDOR, ADMIN
}
```

**Tarefas:**
- [ ] Criar entidade Usuario
- [ ] Criar repository e service
- [ ] Implementar criptografia de senha (BCrypt)
- [ ] CRUD básico de usuários

---

### 2.2 Autenticação JWT
**Objetivo:** Login seguro e stateless

**Tecnologias:**
- Spring Security
- JWT (JSON Web Token)
- Redis para blacklist de tokens (opcional)

**Endpoints:**
```java
POST /api/auth/register     // Cadastro
POST /api/auth/login        // Login (retorna JWT)
POST /api/auth/logout       // Logout (invalida token)
GET  /api/auth/me           // Dados do usuário logado
POST /api/auth/refresh      // Refresh token
```

**Tarefas:**
- [ ] Configurar Spring Security
- [ ] Implementar geração de JWT
- [ ] Criar filtro de autenticação
- [ ] Proteger endpoints sensíveis (POST, PUT, DELETE)
- [ ] Documentar fluxo de autenticação

---

### 2.3 Autorização por Roles
**Objetivo:** Controlar acesso por tipo de usuário

**Regras:**
- **COMPRADOR:** Pode buscar, favoritar, visualizar
- **VENDEDOR:** Pode criar/editar seus próprios imóveis
- **ADMIN:** Acesso total

**Tarefas:**
- [ ] Implementar anotações @PreAuthorize
- [ ] Validar ownership (vendedor só edita seus imóveis)
- [ ] Criar testes de autorização

---

## 📡 FASE 3: INTEGRAÇÕES E DADOS REAIS (Semanas 5-6) - **MÉDIA PRIORIDADE**

### 3.1 Integração com APIs Públicas de Leilão
**Objetivo:** Buscar imóveis reais de leilões oficiais

**Fontes de Dados:**
- **PNCP (Portal Nacional de Contratações Públicas)**
- **Caixa Econômica Federal** (leilões de imóveis)
- **Banco do Brasil** (leilões)
- **Superbid, Mega Leilões, etc.**

**Tarefas:**
- [ ] Pesquisar e documentar APIs disponíveis
- [ ] Criar clients HTTP (RestTemplate/WebClient)
- [ ] Implementar parsers de dados
- [ ] Agendar sincronização automática (Scheduled Tasks)
- [ ] Mapear dados externos para modelo interno
- [ ] Tratar inconsistências e duplicações

---

### 3.2 Geolocalização e Mapas
**Objetivo:** Mostrar imóveis em mapa interativo (inspirado em Airbnb)

**Tecnologias:**
- Google Maps API ou Mapbox
- Geocoding (converter endereço em lat/long)

**Endpoints:**
```java
GET /api/imoveis/mapa?bounds=lat1,lng1,lat2,lng2  // Imóveis em área
GET /api/imoveis/proximos?lat={lat}&lng={lng}&raio={km}  // Próximos
```

**Tarefas:**
- [ ] Integrar com API de geocoding
- [ ] Adicionar lat/long nos dados mockados
- [ ] Criar query espacial (PostGIS ou JPA nativo)
- [ ] Implementar endpoint de busca por área

---

### 3.3 Sistema de Notificações
**Objetivo:** Alertar usuários sobre novos leilões (inspirado em QuintoAndar)

**Tipos de Notificação:**
- Email: novos imóveis que atendem critérios salvos
- Push notification (futuro)
- In-app notifications

**Modelo:**
```java
@Entity
public class AlertaUsuario {
    @Id
    private Long id;
    
    @ManyToOne
    private Usuario usuario;
    
    private String uf;
    private String cidade;
    private String tipoImovel;
    private BigDecimal valorMin;
    private BigDecimal valorMax;
    
    private Boolean ativo;
}
```

**Tarefas:**
- [ ] Criar entidade de alertas
- [ ] CRUD de alertas personalizados
- [ ] Implementar job de verificação (Scheduled)
- [ ] Integrar com serviço de email (SendGrid/AWS SES)

---

## 📚 FASE 4: DOCUMENTAÇÃO E QUALIDADE (Semanas 7-8) - **ALTA PRIORIDADE**

### 4.1 Documentação da API (Swagger/OpenAPI) ⭐ **CRÍTICO**
**Objetivo:** Documentação interativa e completa

**Tecnologias:**
- SpringDoc OpenAPI 3
- Swagger UI

**Tarefas:**
- [ ] Adicionar dependência springdoc-openapi
- [ ] Configurar Swagger UI (/swagger-ui.html)
- [ ] Documentar todos os endpoints com @Operation
- [ ] Adicionar exemplos de request/response
- [ ] Documentar códigos de erro
- [ ] Gerar JSON OpenAPI spec

**Endpoint de acesso:**
```
http://localhost:8080/swagger-ui.html
http://localhost:8080/api-docs
```

---

### 4.2 Testes Automatizados ⭐ **ALTA PRIORIDADE**

**Testes Unitários:**
- [ ] Service layer (ImovelService, UsuarioService)
- [ ] Mappers (ImovelMapper)
- [ ] Validações customizadas
- [ ] Specifications (ImovelSpecifications)

**Testes de Integração:**
- [ ] Controllers (MockMvc)
- [ ] Repositories (DataJpaTest)
- [ ] Autenticação JWT
- [ ] Endpoints completos (WebMvcTest)

**Tecnologias:**
- JUnit 5
- Mockito
- Spring Boot Test
- H2 Database (testes)
- TestContainers (opcional)

**Meta de Cobertura:**
- Mínimo 70% de cobertura
- Crítico: 90%+ em services

---

### 4.3 Melhorias de Performance e Cache

**Estratégias:**
- [ ] Cache de estatísticas (Redis)
- [ ] Cache de busca frequente (Redis)
- [ ] Paginação eficiente (cursor-based)
- [ ] Índices otimizados no banco
- [ ] Lazy loading de imagens
- [ ] CDN para imagens (CloudFront/CloudFlare)

**Tarefas:**
- [ ] Configurar Spring Cache
- [ ] Identificar queries lentas (EXPLAIN)
- [ ] Adicionar índices compostos
- [ ] Implementar rate limiting (Bucket4j)
- [ ] Profiling com Spring Actuator

---

### 4.4 Logs e Monitoramento

**Tecnologias:**
- SLF4J + Logback
- Spring Actuator
- Prometheus + Grafana (opcional)
- Sentry (error tracking)

**Tarefas:**
- [ ] Estruturar logs JSON
- [ ] Adicionar correlation ID
- [ ] Configurar níveis de log por ambiente
- [ ] Implementar health checks customizados
- [ ] Configurar métricas (Micrometer)

---

## 🗄️ FASE 5: INFRAESTRUTURA E DEPLOY (Semanas 9-10) - **MÉDIA PRIORIDADE**

### 5.1 Containerização Completa

**Docker Compose:**
```yaml
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - redis
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      
  postgres:
    image: postgres:16
    volumes:
      - postgres-data:/var/lib/postgresql/data
      
  redis:
    image: redis:7
    
  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
```

**Tarefas:**
- [ ] Criar Dockerfile otimizado (multi-stage)
- [ ] Configurar docker-compose.yml completo
- [ ] Adicionar scripts de inicialização
- [ ] Documentar setup local

---

### 5.2 CI/CD Pipeline

**GitHub Actions:**
```yaml
name: CI/CD

on:
  push:
    branches: [main, develop]
  pull_request:

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - Checkout
      - Setup Java 17
      - Run tests
      - Coverage report
      
  build:
    needs: test
    steps:
      - Build JAR
      - Build Docker image
      - Push to registry
      
  deploy:
    needs: build
    steps:
      - Deploy to staging/production
```

**Tarefas:**
- [ ] Configurar GitHub Actions
- [ ] Setup de ambientes (dev, staging, prod)
- [ ] Automatizar testes no PR
- [ ] Automatizar deploy em staging

---

### 5.3 Deploy em Cloud (AWS/Azure/GCP)

**AWS Stack Sugerido:**
- **Compute:** ECS Fargate (containerizado)
- **Database:** RDS PostgreSQL
- **Cache:** ElastiCache Redis
- **Storage:** S3 (imagens)
- **CDN:** CloudFront
- **Load Balancer:** Application Load Balancer
- **Secrets:** AWS Secrets Manager

**Tarefas:**
- [ ] Criar infraestrutura como código (Terraform)
- [ ] Configurar ambientes isolados
- [ ] Setup de backup automatizado
- [ ] Configurar HTTPS (certificado SSL)
- [ ] Configurar domínio customizado

---

## 💻 FASE 6: FRONTEND (Semanas 11-16) - **VIRA POR ÚLTIMO**

### 6.1 Setup do Projeto Frontend

**Stack Tecnológica:**
- **Framework:** Next.js 14+ (App Router)
- **Linguagem:** TypeScript
- **UI Library:** Tailwind CSS + shadcn/ui
- **State Management:** Zustand ou Context API
- **HTTP Client:** Axios
- **Maps:** Google Maps / Mapbox
- **Forms:** React Hook Form + Zod

**Estrutura do Projeto:**
```
frontend/
├── app/                    # Next.js App Router
│   ├── (auth)/            # Rotas autenticadas
│   ├── (public)/          # Rotas públicas
│   ├── imoveis/
│   │   ├── page.tsx       # Listagem
│   │   └── [id]/
│   │       └── page.tsx   # Detalhes
│   ├── favoritos/
│   └── admin/
├── components/
│   ├── ui/                # shadcn/ui components
│   ├── cards/
│   ├── filters/
│   └── maps/
├── lib/
│   ├── api.ts             # Axios setup
│   └── auth.ts            # JWT handling
└── types/
    └── imovel.ts
```

---

### 6.2 Páginas Principais

**Ordem de Implementação:**

1. **Listagem de Imóveis** (`/imoveis`)
   - [ ] Grid de cards com imagens
   - [ ] Filtros laterais (QuintoAndar style)
   - [ ] Paginação infinita ou numérica
   - [ ] Toggle lista/mapa
   - [ ] Ordenação (preço, data, relevância)

2. **Detalhes do Imóvel** (`/imoveis/[id]`)
   - [ ] Galeria de imagens fullscreen
   - [ ] Informações detalhadas
   - [ ] Mapa de localização
   - [ ] Botão de favoritar
   - [ ] Botão de contato/interesse
   - [ ] Imóveis similares

3. **Mapa Interativo** (`/mapa`)
   - [ ] Visualização em mapa (Airbnb style)
   - [ ] Marcadores clusterizados
   - [ ] Preview card ao hover
   - [ ] Filtros integrados

4. **Favoritos** (`/favoritos`)
   - [ ] Listagem de imóveis salvos
   - [ ] Comparação lado a lado
   - [ ] Exportar para PDF

5. **Dashboard Admin** (`/admin`)
   - [ ] Gestão de imóveis (CRUD)
   - [ ] Estatísticas visuais (charts)
   - [ ] Upload de imagens
   - [ ] Moderação

6. **Autenticação**
   - [ ] Login (`/login`)
   - [ ] Cadastro (`/cadastro`)
   - [ ] Recuperação de senha
   - [ ] Perfil do usuário (`/perfil`)

---

### 6.3 Componentes Principais

**Componentes a Criar:**
- [ ] `ImovelCard` - Card de imóvel (grid)
- [ ] `ImovelListItem` - Item de lista
- [ ] `FilterSidebar` - Filtros avançados
- [ ] `ImageGallery` - Galeria lightbox
- [ ] `MapView` - Mapa com marcadores
- [ ] `FavoritoButton` - Botão de favoritar
- [ ] `SearchBar` - Busca com autocomplete
- [ ] `PriceRangeSlider` - Slider de preço
- [ ] `Navbar` - Navegação responsiva
- [ ] `Footer` - Rodapé

---

### 6.4 Integrações e Funcionalidades

**Essenciais:**
- [ ] Integração com backend (Axios + SWR/React Query)
- [ ] Autenticação JWT (interceptors)
- [ ] Gerenciamento de estado (Zustand)
- [ ] Responsividade (mobile-first)
- [ ] SEO (Next.js metadata)
- [ ] Loading states e skeletons
- [ ] Error boundaries
- [ ] Acessibilidade (WCAG 2.1)

**Nice to Have:**
- [ ] PWA (Progressive Web App)
- [ ] Dark mode
- [ ] Internacionalização (i18n)
- [ ] Analytics (Google Analytics)
- [ ] A/B testing

---

## 📊 CRONOGRAMA CONSOLIDADO (MVP - 16 semanas)

| Fase | Duração | Prioridade | Status |
|------|---------|-----------|--------|
| **1. Fundação Backend** | 2 semanas | 🔴 CRÍTICA | 🟡 Em andamento (40%) |
| **2. Autenticação** | 2 semanas | 🔴 CRÍTICA | ⚪ Não iniciado |
| **3. Integrações** | 2 semanas | 🟠 ALTA | ⚪ Não iniciado |
| **4. Documentação/Qualidade** | 2 semanas | 🔴 CRÍTICA | ⚪ Não iniciado |
| **5. Infraestrutura** | 2 semanas | 🟡 MÉDIA | ⚪ Não iniciado |
| **6. Frontend** | 6 semanas | 🟠 ALTA | ⚪ Não iniciado |

---

## 🎯 MARCOS (Milestones)

### 🏁 Milestone 1: Backend MVP (Semana 4)
- ✅ CRUD completo de imóveis
- ✅ Upload de imagens
- ✅ Estatísticas
- ✅ Swagger completo
- ✅ 70% cobertura de testes

### 🏁 Milestone 2: Backend Completo (Semana 8)
- ✅ Autenticação JWT
- ✅ Sistema de favoritos
- ✅ Notificações por email
- ✅ Integração com 2+ fontes de leilão
- ✅ Geolocalização funcional

### 🏁 Milestone 3: Deploy Staging (Semana 10)
- ✅ Backend em produção (AWS/Azure)
- ✅ CI/CD funcional
- ✅ Monitoramento ativo

### 🏁 Milestone 4: Frontend MVP (Semana 14)
- ✅ Listagem e detalhes de imóveis
- ✅ Filtros e busca
- ✅ Login/cadastro
- ✅ Favoritos

### 🏁 Milestone 5: MVP Completo (Semana 16)
- ✅ Todas as funcionalidades integradas
- ✅ Testes end-to-end
- ✅ Deploy em produção
- ✅ Documentação completa
- ✅ Pronto para apresentação

---

## 🚨 RISCOS E MITIGAÇÕES

| Risco | Probabilidade | Impacto | Mitigação |
|-------|--------------|---------|-----------|
| APIs de leilão indisponíveis | Alta | Alto | Manter dados mockados como fallback |
| Atraso no desenvolvimento | Média | Alto | Priorizar MVP, postergar features secundárias |
| Problemas de performance | Média | Médio | Implementar cache desde o início |
| Dificuldade com upload de imagens | Baixa | Médio | Usar serviços gerenciados (S3/Cloudinary) |
| Complexidade do frontend | Média | Alto | Usar biblioteca de componentes (shadcn) |

---

## 📝 NOTAS FINAIS

### O que NÃO fazer (para manter foco no MVP):
- ❌ Sistema de chat em tempo real
- ❌ Inteligência artificial de recomendação (v2)
- ❌ Marketplace de serviços (advogados, corretores)
- ❌ App mobile nativo (focar em PWA)
- ❌ Sistema de lances online
- ❌ Integração com múltiplas formas de pagamento

### Priorização para apresentação a empresas:
1. **Backend robusto e documentado** (Swagger impecável)
2. **Frontend visual e responsivo** (mesmo que simples)
3. **Dados reais ou mockados de qualidade**
4. **Deploy funcionando em produção**
5. **README excelente com screenshots/GIFs**

### Tecnologias a adicionar no pom.xml (próximos passos):
```xml
<!-- Swagger -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>

<!-- AWS S3 -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
</dependency>

<!-- Spring Security + JWT -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>

<!-- Email -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
```

---

**Última atualização:** 02/02/2026  
**Versão do Roadmap:** 2.0 (Análise competitiva incluída)

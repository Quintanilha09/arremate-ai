# 🚀 Próximos Passos de Desenvolvimento - ArremateAI

**Última atualização:** 06/02/2026

---

## ✅ Status Atual

### Backend Concluído:
- ✅ CRUD completo de Imóveis (GET, POST, PUT, PATCH, DELETE)
- ✅ Sistema de Upload de Imagens (múltiplas por imóvel)
- ✅ Sistema de Favoritos
- ✅ Autenticação JWT + OAuth2 Google
- ✅ Sistema de Usuários (COMPRADOR, VENDEDOR, ADMIN)
- ✅ Sistema de Perfil do Usuário (com avatar)
- ✅ Recuperação de Senha (2FA por email)
- ✅ Integração Brasil API (dados mockados realistas)

### Frontend Concluído:
- ✅ Página de Login/Registro (com OAuth2 Google)
- ✅ Listagem de Imóveis com filtros
- ✅ Detalhes do Imóvel com galeria
- ✅ Cadastro/Edição de Imóveis
- ✅ Upload de Imagens (múltiplas)
- ✅ Sistema de Favoritos
- ✅ **Página de Perfil do Usuário** (FASE 1/5 - CONCLUÍDA)
  - Edição de dados pessoais com validação
  - Upload/remoção de avatar
  - Alteração de senha (exceto OAuth2)
  - Confirmação 2FA para mudança de telefone
- ✅ Página de Recuperação de Senha (esqueci senha)
- ✅ Máscaras automáticas (CPF, telefone)

### Frontend Pendente:
- ❌ **Página "Meus Anúncios"** - Link existe no header mas página não implementada
- ❌ **Página "Configurações"** - Link existe no header mas página não implementada

---

## 📋 PRIORIDADES CONFIRMADAS (5 FASES)

### 🎯 FASE 1/5 - Página de Perfil do Usuário ✅ **CONCLUÍDA**
- ✅ Gerenciamento completo do perfil
- ✅ Upload de avatar
- ✅ Alteração de senha
- ✅ Recuperação de senha (2FA)
- ✅ Confirmação de mudanças sensíveis (telefone)

---

### 🎯 FASE 2/5 - Sistema de Busca Avançada ⚡ **PRÓXIMA PRIORIDADE**

**Objetivo:** Melhorar experiência de busca com autocomplete e histórico

#### Backend:
- [ ] **Endpoint de Autocomplete**
  - `GET /api/imoveis/autocomplete?q={termo}`
  - Busca em: descrição, endereço, cidade, UF
  - Retorna sugestões ordenadas por relevância
  - Limite de 10 resultados

- [ ] **Endpoint de Histórico de Buscas**
  - `POST /api/buscas/salvar` - Salvar termo buscado
  - `GET /api/buscas/historico` - Listar últimas 10 buscas
  - `DELETE /api/buscas/limpar` - Limpar histórico

- [ ] **Otimizações de Performance**
  - Índices full-text no PostgreSQL
  - Implementar debounce no autocomplete
  - Cache Redis para termos populares (opcional)

#### Frontend:
- [ ] **Input de Busca com Autocomplete**
  - Componente `<SearchBar />` com dropdown de sugestões
  - Debounce de 300ms
  - Highlights nos termos encontrados
  - Navegação por teclado (arrows, enter)

- [ ] **Histórico de Buscas**
  - Salvar no localStorage + backend
  - Exibir abaixo do input quando vazio
  - Botão para limpar histórico
  - Ícone de relógio/histórico

- [ ] **Melhorias na Página de Listagem**
  - Chips de filtros ativos (removíveis)
  - Ordenação: relevância, preço (crescente/decrescente), data
  - Paginação melhorada (load more + infinite scroll)

**Critérios de Aceite:**
- Autocomplete responde em < 200ms
- Histórico sincronizado entre sessões
- UI responsiva e acessível (ARIA labels)

---

### 🎯 FASE 3/5 - Melhorias no Sistema de Favoritos 💖 **PLANEJADA**

**Objetivo:** Expandir funcionalidades de favoritos (inspirado em Airbnb)

#### Backend:
- [ ] **Organização em Listas**
  - Entidade `ListaFavoritos` (nome, descrição, privacidade)
  - Usuário pode criar múltiplas listas
  - Endpoint `POST /api/favoritos/listas` - Criar lista
  - Endpoint `GET /api/favoritos/listas` - Listar listas do usuário
  - Endpoint `POST /api/favoritos/listas/{id}/imoveis/{imovelId}` - Adicionar à lista

- [ ] **Compartilhamento de Listas**
  - Gerar link público para compartilhar
  - `GET /api/favoritos/listas/{id}/compartilhar` - Gerar token
  - `GET /api/favoritos/publico/{token}` - Visualizar lista compartilhada

- [ ] **Notificações de Mudanças**
  - Alertar quando imóvel favoritado mudar de preço
  - Endpoint `PATCH /api/favoritos/{id}/alertas` - Ativar/desativar alertas

#### Frontend:
- [ ] **Página de Favoritos Melhorada**
  - Abas para diferentes listas
  - Modal para criar nova lista
  - Drag & drop para mover entre listas
  - Botão de compartilhar lista

- [ ] **Widget de Favoritos**
  - Badge de contagem no header
  - Dropdown de acesso rápido
  - Preview dos últimos 3 favoritos

**Critérios de Aceite:**
- Usuário pode criar até 10 listas
- Links de compartilhamento expiram em 30 dias
- Notificações via email (opcional)

---

### 🎯 FASE 4/5 - Galeria de Imagens Melhorada 📸 **PLANEJADA**

**Objetivo:** Experiência visual superior (inspirado em Airbnb/QuintoAndar)

#### Backend:
- [ ] **Processamento de Imagens**
  - Redimensionamento automático (thumbnail, medium, large)
  - Compressão com qualidade ajustável
  - Geração de WebP para navegadores modernos
  - Biblioteca: ThumbnailAtor ou ImageMagick

- [ ] **CDN e Storage**
  - Migrar para AWS S3 ou Cloudflare R2
  - Servir via CDN para performance global
  - Lazy loading de imagens

#### Frontend:
- [ ] **Lightbox/Galeria Interativa**
  - Modal fullscreen com navegação (prev/next)
  - Zoom in/out com pinch/scroll
  - Thumbnails na parte inferior
  - Swipe em mobile
  - Biblioteca: PhotoSwipe ou react-image-gallery

- [ ] **Upload de Imagens Melhorado**
  - Drag & drop de múltiplas imagens
  - Preview antes do upload
  - Barra de progresso por arquivo
  - Reordenação por drag & drop
  - Definir imagem principal visualmente

- [ ] **Otimizações**
  - Lazy loading nativo (`loading="lazy"`)
  - Blur placeholder enquanto carrega
  - Suporte a imagens responsivas (srcset)

**Critérios de Aceite:**
- Imagens carregam em < 1s (3G)
- Máximo 10 imagens por imóvel
- Formatos aceitos: JPG, PNG, WebP

---

### 🎯 FASE 5/5 - Dashboard de Estatísticas 📊 **PLANEJADA**

**Objetivo:** Painel analítico para usuários e admins

#### Backend:
- [ ] **Endpoints de Estatísticas**
  - `GET /api/estatisticas/geral` - Visão geral da plataforma
    - Total de imóveis, usuários, favoritos
    - Crescimento mensal
    - Estados mais ativos
  
  - `GET /api/estatisticas/imoveis` - Estatísticas de imóveis
    - Distribuição por tipo, UF, faixa de preço
    - Valor médio, mínimo, máximo
    - Imóveis com mais visualizações/favoritos
  
  - `GET /api/estatisticas/usuario` - Dashboard pessoal
    - Imóveis cadastrados (se vendedor)
    - Favoritos por categoria
    - Histórico de buscas

- [ ] **Queries Otimizadas**
  - Agregações com JPA/JPQL
  - Cache de resultados (Redis)
  - Atualização a cada 1 hora

#### Frontend:
- [ ] **Página de Dashboard**
  - Cards com KPIs (total, crescimento %)
  - Gráficos de barras/pizza (Chart.js ou Recharts)
  - Tabela de "Top 10" imóveis
  - Mapa de calor por região (opcional)

- [ ] **Dashboard Pessoal**
  - Aba específica no perfil do usuário
  - Métricas de engajamento
  - Sugestões baseadas em favoritos

**Critérios de Aceite:**
- Carregamento de estatísticas em < 500ms
- Gráficos interativos e responsivos
- Atualização automática (polling a cada 5min)

---

## 🔴 PÁGINAS FALTANTES (Header Links)

### 📦 Página "Meus Anúncios" (Para VENDEDOR/ADMIN)
**Status:** ❌ Não implementada  
**Prioridade:** ALTA (link visível no header)

#### Backend:
- [ ] **Endpoint de Listagem**
  - `GET /api/imoveis/meus` - Listar imóveis do usuário logado
  - Filtros: status (DISPONIVEL, VENDIDO, SUSPENSO)
  - Paginação e ordenação

- [ ] **Endpoints de Gerenciamento**
  - `PATCH /api/imoveis/{id}/status` - Alterar status (ativar/pausar)
  - `GET /api/imoveis/{id}/estatisticas` - Views, favoritos, leads

#### Frontend:
- [ ] **Página `/meus-anuncios`**
  - Tabela/Cards com imóveis do usuário
  - Filtros por status
  - Ações: Editar, Pausar/Ativar, Ver estatísticas
  - Botão "Criar Novo Anúncio"
  - Badge de status (disponível/vendido/pausado)

- [ ] **Dashboard de Performance**
  - Total de visualizações
  - Total de favoritos
  - Leads gerados (se houver)
  - Imóveis mais vistos

**Critérios de Aceite:**
- Apenas VENDEDOR e ADMIN podem acessar
- Mostrar contagem total de anúncios
- Permitir edição rápida de preço/descrição

---

### ⚙️ Página "Configurações"
**Status:** ❌ Não implementada  
**Prioridade:** MÉDIA

#### Backend:
- [ ] **Endpoints de Preferências**
  - `GET /api/usuarios/preferencias` - Buscar preferências
  - `PUT /api/usuarios/preferencias` - Atualizar preferências
  - Entidade `Preferencias` (JSON ou tabela separada)

- [ ] **Tipos de Configurações**
  - Notificações (email, push, SMS)
  - Privacidade (perfil público/privado)
  - Preferências de busca (raio, categorias favoritas)
  - Tema (claro/escuro - opcional)

#### Frontend:
- [ ] **Página `/configuracoes`**
  - Tabs organizadas:
    - **Notificações:** Toggles para tipos de alertas
    - **Privacidade:** Quem pode ver perfil/favoritos
    - **Conta:** Desativar/excluir conta
    - **Aparência:** Tema (opcional)

- [ ] **Componentes**
  - Switch/Toggle para cada configuração
  - Modal de confirmação para ações críticas
  - Salvar automaticamente ou botão "Salvar Alterações"

**Critérios de Aceite:**
- Mudanças salvas em tempo real ou com confirmação
- Opção de exportar dados (LGPD)
- Opção de excluir conta permanentemente

---

## 🎨 MELHORIAS GERAIS (Backlog)

### Performance:
- [ ] Implementar Server-Side Rendering (SSR) com Next.js
- [ ] Configurar Service Worker para PWA
- [ ] Lazy loading de componentes pesados
- [ ] Otimizar bundle size (tree shaking, code splitting)

### SEO:
- [ ] Meta tags dinâmicas por página
- [ ] Sitemap.xml automático
- [ ] Structured Data (Schema.org) para imóveis
- [ ] Open Graph para compartilhamento social

### Acessibilidade:
- [ ] ARIA labels em todos os componentes
- [ ] Navegação completa por teclado
- [ ] Contraste de cores WCAG AA
- [ ] Screen reader testing

### Testes:
- [ ] Testes unitários (JUnit + Jest)
- [ ] Testes de integração (Testcontainers)
- [ ] Testes E2E (Playwright ou Cypress)
- [ ] Coverage mínimo de 70%

### DevOps:
- [ ] CI/CD com GitHub Actions
- [ ] Deploy automático (Railway, Render, Vercel)
- [ ] Monitoramento (Sentry, New Relic)
- [ ] Logs estruturados (ELK Stack)

---

## 🎯 ROADMAP DE EXECUÇÃO

### Semana 1-2 (ATUAL):
- ✅ Fase 1/5 - Página de Perfil (CONCLUÍDA)
- ❌ **Páginas Faltantes:** Meus Anúncios + Configurações
- ⚡ Iniciar Fase 2/5 - Busca Avançada

### Semana 3-4:
- Concluir Fase 2/5 - Busca Avançada
- Implementar "Meus Anúncios" (PRIORIDADE)
- Iniciar Fase 3/5 - Favoritos

### Semana 5-6:
- Concluir Fase 3/5 - Favoritos
- Implementar "Configurações" (se tempo permitir)
- Iniciar Fase 4/5 - Galeria

### Semana 7-8:
- Concluir Fase 4/5 - Galeria
- Iniciar Fase 5/5 - Dashboard

### Semana 9-10:
- Concluir Fase 5/5 - Dashboard
- Finalizar "Configurações" (se não feito antes)
- Testes finais e ajustes
- Documentação completa

---

## 📝 Notas Importantes

1. **⚠️ URGENTE:** Implementar "Meus Anúncios" e "Configurações" - links já visíveis no header mas páginas não existem (erro 404)
2. **Priorizar MVP:** Focar nas 5 fases + páginas faltantes antes de adicionar features extras
3. **Testar continuamente:** Não acumular bugs para o final
4. **Documentar decisões:** Atualizar este arquivo após cada fase
5. **Feedback do usuário:** Validar cada fase antes de avançar
6. **Performance first:** Otimizar desde o início, não deixar para depois

---

**Última revisão:** 06/02/2026  
**Responsável:** Equipe ArremateAI  
**Status:** 🟢 Em progresso (Fase 2/5)

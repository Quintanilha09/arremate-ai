# 🎨 Especificação Completa do Front-End - ArremateAI

## 📅 Data de Criação: 03/02/2026

---

## 🎯 Visão Geral

Desenvolvimento de uma plataforma web **elegante, responsiva e moderna** para agregação de leilões de múltiplos produtos (imóveis, automóveis, eletrônicos, etc.). O design deve inspirar confiança e decisões ponderadas, não compras por impulso.

---

## 🎨 Diretrizes de Design

### Princípios Visuais
- ✅ **Elegante e Minimalista**: Interface clean, espaços em branco, tipografia legível
- ✅ **Responsivo**: Mobile-first, adaptável a todos os dispositivos
- ✅ **Moderno**: Componentes atuais, micro-interações, transições suaves
- ✅ **Cores Frias**: Paleta baseada em azuis, cinzas e verdes (tons confiáveis)
- ✅ **Contraste Adequado**: Tonalidades agradáveis, evitar cores muito claras ou saturadas
- ✅ **Ícones de Qualidade**: Ícones elegantes, intuitivos e profissionais (sem ícones "feios")

### Referências de Inspiração
- **QuintoAndar** (https://www.quintoandar.com.br/) - UI/UX, layout de cards, filtros
- **OLX** (https://www.olx.com.br/) - Sistema de categorias, busca, anúncios

### Paleta de Cores Sugerida
- **Primária**: Azul profundo (#1E3A8A, #2563EB) - Confiança
- **Secundária**: Verde água (#0D9488, #14B8A6) - Destaque positivo
- **Neutras**: Cinzas (#F3F4F6, #9CA3AF, #374151, #1F2937) - Fundos e textos
- **Acento**: Vermelho suave (#EF4444) - Favoritos
- **Branco/Preto**: #FFFFFF, #111827 - Contraste

---

## 🧭 Estrutura de Navegação

### 1. Barra de Menu Superior (Global)

**Posição**: Fixa no topo, presente em **todas as páginas**

**Elementos** (da esquerda para a direita):

1. **Logo ArremateAI** (canto esquerdo)
   - Clicável, leva à Home
   - Design moderno e memorável

2. **Categorias** (dropdown/menu expandível)
   - Ícone + Texto: "Categorias"
   - Submenu com opções:
     - 🏠 Imóveis (MVP - implementado)
     - 🚗 Automóveis (futuro)
     - 💻 Eletrônicos (futuro)
     - 🪑 Móveis (futuro)
     - 🎨 Diversos (futuro)
   - Visual: Cards com ícones grandes ao abrir dropdown

3. **Anunciar** (botão destacado)
   - Ícone + Texto: "Anunciar"
   - Cor de destaque (verde ou azul vibrante)
   - Abre modal/página de cadastro de produto

4. **Perfil do Usuário** (canto direito)
   - Se **não logado**:
     - Botão "Entrar" ou "Login/Cadastro"
   - Se **logado**:
     - Avatar/Foto do usuário + Nome
     - Dropdown com:
       - ❤️ Favoritos
       - 👤 Meu Perfil
       - 📦 Meus Anúncios
       - ⚙️ Configurações
       - 🚪 Sair

**Comportamento**:
- **Desktop**: Todos os itens visíveis
- **Mobile**: Menu hamburguer, itens em sidebar

---

## 📄 Páginas e Funcionalidades

---

## 🏠 Página 1: HOME

### Objetivo
Apresentar o ArremateAI, explicar a proposta de valor e convidar o usuário a explorar produtos.

### Seções

#### 1.1. Hero Section (Topo)
- **Título principal**: "Encontre as Melhores Oportunidades em Leilões"
- **Subtítulo**: "Milhares de produtos de leiloeiras confiáveis em um só lugar"
- **CTA primário**: Botão grande "Ver Produtos em Leilões" → redireciona para `/imoveis` (ou `/produtos`)
- **CTA secundário**: "Como Funciona" → scroll suave para próxima seção
- **Visual**: Imagem de fundo sutil (imóveis, produtos diversos) ou ilustração moderna

#### 1.2. Autenticação Rápida
- **Se não logado**:
  - Cards: "Entrar" | "Cadastrar-se"
  - Opção: "Continuar com Google" (OAuth)
  - Formulário inline ou modal:
    - **Login**: Email + Senha + "Esqueci a senha"
    - **Cadastro**: Nome + Email + Senha + Confirmar Senha + Aceitar Termos

#### 1.3. Como Funciona (3 passos visuais)
- **Passo 1**: "Busque" - Ícone de lupa + Texto
- **Passo 2**: "Compare" - Ícone de gráfico + Texto
- **Passo 3**: "Arremate" - Ícone de martelo + Texto

#### 1.4. Categorias em Destaque
- Cards visuais (4-6 categorias)
- Cada card:
  - Ícone grande + Nome da categoria
  - Clicável, leva à listagem filtrada

#### 1.5. Destaques/Novidades (Carrossel)
- 5-10 produtos em destaque
- Cards horizontais deslizantes
- Cada card: Imagem + Título + Preço + Local + Botão "Ver Detalhes"

#### 1.6. Estatísticas
- Números de impacto:
  - "1.000+ Produtos Disponíveis"
  - "50+ Leiloeiras Parceiras"
  - "10.000+ Usuários Satisfeitos"

#### 1.7. Footer
- Links: Sobre | Contato | Termos | Privacidade | FAQ
- Redes sociais
- Copyright © 2026 ArremateAI

---

## 📦 Página 2: LISTAGEM DE PRODUTOS

### Objetivo
Exibir todos os produtos com filtros avançados, busca e paginação.

### URL
- `/imoveis` (MVP)
- `/automoveis`, `/eletronicos` (futuro)

### Layout

#### 2.1. Barra de Busca (destaque logo abaixo do menu)
- **Input grande**: Placeholder "Buscar por local, CEP, cidade, tipo de produto..."
- **Ícone de lupa** à esquerda
- **Botão "Buscar"** à direita
- **Funcionalidade**: Busca full-text no backend (endpoint `/api/imoveis?busca=...`)

#### 2.2. Filtros Rápidos (mesmo nível da busca)
- **Chips/Botões inline**:
  - **UF/Estado**: Dropdown com todos os estados brasileiros
  - **Cidade**: Input autocomplete (baseado no estado)
  - **Tipo**: Dropdown (Casa, Apartamento, Terreno, etc.)
  - **Faixa de Preço**: "Até R$ 200k", "R$ 200k - 500k", "R$ 500k+"
- **Botão "Aplicar Filtros"**: Destaque, aplica filtros simples
- **Botão "Mais Filtros"**: Abre modal/sidebar com filtros avançados

#### 2.3. Modal "Mais Filtros"
**Campos**:
- **Preço**: Range slider (mínimo - máximo)
- **Área (m²)**: Range slider
- **Quartos**: Dropdown (1, 2, 3, 4+)
- **Banheiros**: Dropdown (1, 2, 3+)
- **Vagas de Garagem**: Dropdown (0, 1, 2, 3+)
- **Condição**: Checkboxes (Novo, Usado, Seminovo, Reformado)
- **Aceita Financiamento**: Toggle/Switch
- **Data do Leilão**: Date picker (de - até)
- **Instituição**: Dropdown com leiloeiras
- **Ordenar por**: Dropdown (Menor Preço, Maior Preço, Mais Recente, Data do Leilão)

**Botões**:
- "Limpar Filtros" (secundário)
- "Aplicar Filtros" (primário)

#### 2.4. Listagem de Produtos (Grid)
**Layout**:
- **Desktop**: 3-4 colunas
- **Tablet**: 2 colunas
- **Mobile**: 1 coluna

**Card de Produto**:
- **Imagem principal**: 
  - Proporção 16:9 ou 4:3
  - **Ícone de coração** (favorito) no **canto superior direito**:
    - **Não favoritado**: Contorno branco/cinza, fundo transparente
    - **Favoritado**: Preenchido vermelho (#EF4444)
    - Clicável, alterna estado (chama endpoint `POST /api/favoritos/{id}`)
  - Hover: Leve zoom na imagem

- **Informações**:
  - **Título/Descrição**: 1-2 linhas, truncado com "..."
  - **Tipo**: Badge (Casa, Apartamento, etc.)
  - **Preço**: Destaque, fonte maior, bold (R$ 350.000)
  - **Localização**: Ícone de pin + Cidade/UF
  - **Características**: Ícones + Números (🛏️ 3 quartos | 🚿 2 banheiros | 🚗 2 vagas)
  - **Data do Leilão**: Ícone de calendário + Data
  - **Instituição**: Logo pequena ou texto (Caixa, BB, etc.)

- **Ações** (visíveis ao hover ou sempre em mobile):
  - **✏️ Editar**: Botão pequeno, ícone de lápis (apenas se usuário é dono do anúncio)
  - **🗑️ Excluir**: Botão pequeno, ícone de lixeira (apenas se usuário é dono)
  - **Ver Detalhes**: Botão/Link que leva à página de detalhes

**Paginação**:
- Botões: "Anterior" | Números (1, 2, 3...) | "Próximo"
- Exibir: "Mostrando 20 de 150 resultados"

**Empty State** (se nenhum resultado):
- Ilustração + Texto: "Nenhum produto encontrado com esses filtros"
- Botão "Limpar Filtros"

---

## ❤️ Página 3: FAVORITOS

### Objetivo
Listar todos os produtos favoritados pelo usuário.

### URL
- `/favoritos`

### Requisitos
- **Autenticação obrigatória**: Se não logado, redirecionar para login
- **Endpoint**: `GET /api/favoritos`

### Layout
Igual à **Listagem de Produtos**, mas:
- **Título da página**: "Meus Favoritos" ou "Produtos Salvos"
- **Sem filtros de busca/categoria** (ou filtros simplificados)
- **Ícone de coração**: Sempre preenchido (vermelho)
- **Ação de remover**: Clicar no coração remove da lista (chama `DELETE /api/favoritos/{id}`)
- **Empty State**: "Você ainda não possui favoritos. Explore nossos produtos!"

---

## ➕ Página 4: ANUNCIAR PRODUTO (Cadastro)

### Objetivo
Permitir que usuários cadastrem produtos para leilão.

### URL
- `/anunciar` ou `/cadastrar-produto`

### Requisitos
- **Autenticação obrigatória**
- **Permissão**: Apenas usuários com role ADMIN ou VENDEDOR (validar no backend)
- **Endpoint**: `POST /api/imoveis`

### Formulário (Multi-step ou Single Page)

#### 4.1. Informações Básicas
- **Categoria**: Dropdown (Imóvel, Automóvel, etc.)
- **Tipo**: Dropdown contextual (se Imóvel: Casa, Apt, etc.)
- **Título/Descrição**: Textarea (máx. 1000 caracteres)
- **Preço de Avaliação**: Input numérico (R$)
- **Condição**: Dropdown (Novo, Usado, Seminovo, etc.)

#### 4.2. Localização (se Imóvel)
- **CEP**: Input com máscara, autocomplete de endereço
- **UF**: Dropdown
- **Cidade**: Input
- **Bairro**: Input
- **Endereço Completo**: Input
- **Latitude/Longitude**: Inputs opcionais (ou buscar via CEP)

#### 4.3. Características (se Imóvel)
- **Área Total (m²)**: Input numérico
- **Quartos**: Dropdown (0-10+)
- **Banheiros**: Dropdown (0-10+)
- **Vagas**: Dropdown (0-10+)
- **Aceita Financiamento**: Toggle/Checkbox

#### 4.4. Leilão
- **Data do Leilão**: Date picker
- **Instituição**: Dropdown (Caixa, BB, etc.)
- **Link do Edital**: Input URL

#### 4.5. Upload de Imagens
**Funcionalidade**:
- **Máximo**: 20 imagens
- **Formatos**: JPG, PNG, WEBP
- **Tamanho máximo**: 5MB por imagem
- **Endpoint**: `POST /api/imoveis/{id}/imagens`

**Interface**:
- **Área de drag-and-drop**: "Arraste imagens ou clique para selecionar"
- **Preview das imagens**:
  - Thumbnails em grid (4-5 por linha)
  - Cada thumbnail com:
    - ❌ Botão de remover (canto superior direito)
    - 🔼🔽 Setas para reordenar (ou drag-and-drop)
    - ⭐ Botão "Definir como Principal" (primeira imagem é principal por padrão)
- **Indicador**: "3 de 20 imagens"

**Comportamento**:
- Upload imediato ao selecionar (ou aguardar submit do formulário)
- Barra de progresso durante upload
- Validação de formato/tamanho no front-end e back-end

#### 4.6. Observações/Extras
- **Textarea**: Informações adicionais, observações

#### 4.7. Botões de Ação
- **Cancelar**: Volta à listagem (confirmação se houver alterações)
- **Salvar como Rascunho**: (futuro) Salva sem publicar
- **Publicar Anúncio**: Submit do formulário

**Validações**:
- Campos obrigatórios marcados com *
- Mensagens de erro inline
- Toast/Notificação de sucesso ao publicar

---

## ✏️ Página 5: EDITAR PRODUTO

### Objetivo
Permitir edição de produtos já cadastrados.

### URL
- `/editar-produto/{id}` ou `/imoveis/{id}/editar`

### Requisitos
- **Autenticação obrigatória**
- **Permissão**: Apenas dono do anúncio ou ADMIN
- **Endpoint**: `PUT /api/imoveis/{id}` ou `PATCH /api/imoveis/{id}`

### Layout
- **Igual ao formulário de cadastro**, mas:
  - Campos pré-preenchidos com dados existentes
  - Imagens já cadastradas exibidas (com opções de remover/reordenar)
  - Botão: "Salvar Alterações" (ao invés de "Publicar")

---

## 🔐 Página 6: LOGIN/CADASTRO

### Objetivo
Autenticar usuários existentes ou criar novas contas.

### URL
- `/login` ou `/entrar`
- `/cadastro` ou `/registrar`

### Layout

#### 6.1. Login
**Formulário**:
- **Email**: Input
- **Senha**: Input password
- **Lembrar-me**: Checkbox
- **Esqueci a senha**: Link

**Botões**:
- "Entrar" (primário)
- "Continuar com Google" (OAuth2, botão estilizado)

**Link**: "Não tem conta? Cadastre-se"

**Endpoint**: `POST /api/auth/login`

#### 6.2. Cadastro
**Formulário**:
- **Nome Completo**: Input
- **Email**: Input
- **Telefone**: Input com máscara (opcional)
- **CPF**: Input com máscara (opcional)
- **Senha**: Input password
- **Confirmar Senha**: Input password
- **Tipo de Usuário**: Radio buttons (Comprador | Vendedor)
- **Aceito os Termos**: Checkbox obrigatório

**Botões**:
- "Cadastrar" (primário)
- "Continuar com Google" (OAuth2)

**Link**: "Já tem conta? Faça login"

**Endpoint**: `POST /api/auth/register`

**Validações**:
- Email único (validar no backend)
- Senha forte (mínimo 8 caracteres, 1 maiúscula, 1 número)
- Senhas devem coincidir
- Termos aceitos

---

## 🔍 Página 7: DETALHES DO PRODUTO

### Objetivo
Exibir todas as informações de um produto específico.

### URL
- `/imoveis/{id}` ou `/produto/{id}`

### Requisitos
- **Endpoint**: `GET /api/imoveis/{id}`

### Layout

#### 7.1. Galeria de Imagens (Topo)
- **Imagem principal**: Grande (60% da largura)
- **Thumbnails**: Grid lateral ou inferior (4-6 imagens)
- **Funcionalidades**:
  - Clicar na thumbnail muda a imagem principal
  - Clicar na principal abre lightbox/modal com navegação (setas, fechar)
  - Zoom ao hover (desktop)

#### 7.2. Informações Principais
- **Título/Descrição**
- **Preço**: Destaque visual
- **Tipo**: Badge
- **Localização**: Mapa interativo (Google Maps/Leaflet) com pin
- **Características**: Grid de ícones + valores
  - 🛏️ 3 quartos
  - 🚿 2 banheiros
  - 🚗 2 vagas
  - 📐 120 m²
  - ✅ Aceita financiamento

#### 7.3. Informações do Leilão
- **Data do Leilão**: Destaque com countdown (se próximo)
- **Instituição**: Logo + Nome
- **Link do Edital**: Botão "Ver Edital Completo"

#### 7.4. Observações/Descrição Completa
- Texto formatado (quebras de linha, parágrafos)

#### 7.5. Ações
- **❤️ Adicionar aos Favoritos**: Botão grande (ou remover se já favoritado)
- **📤 Compartilhar**: Botão com opções (WhatsApp, Facebook, copiar link)
- **✏️ Editar**: (se dono do anúncio)
- **🗑️ Excluir**: (se dono do anúncio)

#### 7.6. Produtos Similares (Carrossel)
- "Você também pode gostar"
- 4-6 produtos relacionados (mesma categoria/região)

---

## 🛠️ Componentes Reutilizáveis

### 1. Card de Produto
- Props: `produto`, `onFavorite`, `onEdit`, `onDelete`
- Estados: favorito, loading

### 2. Filtros
- Props: `filters`, `onChange`, `onApply`, `onClear`
- Estados: valores dos filtros

### 3. Upload de Imagens
- Props: `maxImages`, `onUpload`, `onRemove`, `onReorder`
- Estados: lista de imagens, progresso

### 4. Modal
- Props: `title`, `children`, `onClose`, `size`

### 5. Toast/Notificações
- Props: `message`, `type` (success, error, warning, info)

### 6. Breadcrumbs
- Props: `items` (array de {label, href})

### 7. Paginação
- Props: `currentPage`, `totalPages`, `onPageChange`

---

## 🧪 Testes e Validações

### Front-end
- **Validações de formulário**: Yup ou Zod
- **Máscaras**: react-input-mask (CEP, telefone, CPF)
- **Testes**: Jest + React Testing Library
- **Acessibilidade**: ARIA labels, navegação por teclado

### Integração com Backend
- **Axios** ou **Fetch API**
- **Interceptors**: Adicionar token JWT automaticamente
- **Error Handling**: Tratamento de erros 400, 401, 404, 500
- **Loading States**: Spinners, skeletons durante carregamento

---

## 📱 Responsividade

### Breakpoints
- **Mobile**: 0-640px
- **Tablet**: 641px-1024px
- **Desktop**: 1025px+

### Comportamentos
- **Menu**: Hamburguer em mobile
- **Grid de produtos**: 1 coluna (mobile), 2 (tablet), 3-4 (desktop)
- **Filtros**: Modal/sidebar em mobile, inline em desktop
- **Galeria de imagens**: Carrossel em mobile, grid em desktop

---

## 🚀 Stack Tecnológico Sugerido

### Framework/Biblioteca
- **React 18+** com **TypeScript**
- **Next.js 14+** (SSR, SEO, rotas, otimizações)

### Estilização
- **Tailwind CSS** (utility-first, responsivo, customizável)
- **shadcn/ui** ou **Headless UI** (componentes acessíveis)

### Gerenciamento de Estado
- **TanStack Query (React Query)** - Cache, fetching, sincronização
- **Zustand** ou **Context API** - Estado global (auth, favoritos)

### Formulários
- **React Hook Form** + **Zod** (validação)

### Upload de Arquivos
- **react-dropzone** (drag-and-drop de imagens)

### Mapas
- **Leaflet** + **react-leaflet** (open-source) ou **Google Maps API**

### Autenticação
- **NextAuth.js** (OAuth Google, credenciais JWT)

### Ícones
- **Lucide React** ou **Heroicons** (SVG, modernos, customizáveis)

### Notificações
- **react-hot-toast** ou **sonner**

### Carrosséis
- **Swiper.js** ou **Embla Carousel**

---

## 🎯 Entregas e Milestones

### Sprint 1: Setup e Home (1 semana)
- ✅ Configurar Next.js + Tailwind
- ✅ Criar layout base (menu, footer)
- ✅ Implementar página Home
- ✅ Integração com API de login/cadastro

### Sprint 2: Listagem e Filtros (2 semanas)
- ✅ Página de listagem de produtos
- ✅ Sistema de filtros (simples + avançados)
- ✅ Busca textual
- ✅ Paginação
- ✅ Cards de produto com favoritos

### Sprint 3: Detalhes e Favoritos (1 semana)
- ✅ Página de detalhes do produto
- ✅ Galeria de imagens + lightbox
- ✅ Página de favoritos
- ✅ Integração com API de favoritos

### Sprint 4: Cadastro e Edição (2 semanas)
- ✅ Formulário de anúncio (multi-step)
- ✅ Upload de imagens (drag-and-drop, reordenação)
- ✅ Validações completas
- ✅ Edição de produtos existentes

### Sprint 5: Autenticação e Perfil (1 semana)
- ✅ Login/Cadastro + OAuth Google
- ✅ Perfil do usuário
- ✅ Proteção de rotas

### Sprint 6: Polimento e Testes (1 semana)
- ✅ Responsividade em todos os dispositivos
- ✅ Testes E2E (Cypress/Playwright)
- ✅ Acessibilidade (WCAG)
- ✅ Performance (Lighthouse)

---

## 📊 Métricas de Sucesso

- **Performance**: Lighthouse score > 90
- **SEO**: Meta tags, sitemap, robots.txt
- **Acessibilidade**: WCAG 2.1 AA
- **Mobile**: 100% funcional em dispositivos móveis
- **Cross-browser**: Chrome, Firefox, Safari, Edge

---

## 📝 Observações Finais

1. **Categorias futuras**: Deixar estrutura preparada para automóveis, eletrônicos, etc.
2. **OAuth Google**: Implementar fluxo completo de autenticação social
3. **Notificações**: Sistema de alertas para novos produtos (futuro)
4. **Chat**: Possibilidade de mensagens entre comprador/vendedor (futuro)
5. **PWA**: Transformar em Progressive Web App (futuro)

---

## ✅ Checklist de Implementação

### Páginas
- [ ] Home
- [ ] Listagem de Produtos
- [ ] Detalhes do Produto
- [ ] Favoritos
- [ ] Anunciar Produto
- [ ] Editar Produto
- [ ] Login/Cadastro
- [ ] Perfil do Usuário

### Componentes
- [ ] Menu Superior
- [ ] Card de Produto
- [ ] Filtros
- [ ] Busca
- [ ] Upload de Imagens
- [ ] Galeria de Imagens
- [ ] Paginação
- [ ] Modal
- [ ] Toast/Notificações
- [ ] Loading States

### Funcionalidades
- [ ] Autenticação JWT
- [ ] OAuth Google
- [ ] Sistema de Favoritos
- [ ] Upload de Imagens (max 20)
- [ ] Edição/Exclusão de Produtos
- [ ] Filtros Avançados
- [ ] Busca Full-text
- [ ] Responsividade Completa

---

**Documento criado em**: 03/02/2026  
**Versão**: 1.0  
**Status**: Pronto para Implementação ✅

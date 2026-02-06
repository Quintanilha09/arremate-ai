# ArrematAI - Backend

Plataforma centralizada que agrega produtos de múltiplas leiloeiras brasileiras.

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.2.2**
- **PostgreSQL 16**
- **Redis 7** (preparado, não implementado)
- **Maven**
- **Spring Security + JWT**
- **OAuth2 (Google)**
- **JavaMailSender** (2FA por e-mail)

## ✨ Funcionalidades

### Autenticação
- ✅ Login/Registro com JWT
- ✅ **Google OAuth2** (Login com conta Google)
- ✅ **Verificação em 2 Etapas (2FA)** por e-mail
- ✅ Níveis de acesso (ADMIN, VENDEDOR, COMPRADOR)

### Imóveis
- ✅ CRUD completo de imóveis
- ✅ Upload de imagens (até 20 por imóvel)
- ✅ Filtros avançados (12+ parâmetros)
- ✅ Busca textual full-text
- ✅ Paginação e ordenação
- ✅ Soft delete

### Favoritos
- ✅ Sistema completo de favoritos
- ✅ Adicionar/remover imóveis
- ✅ Listagem por usuário

### Estatísticas
- ✅ Dashboard com dados agregados
- ✅ Total de imóveis, valores médios, etc.

## 📋 Pré-requisitos

- Java 17 instalado
- Docker e Docker Compose instalados
- Maven 3.8+ (ou use o wrapper `./mvnw`)
- Conta Google (para OAuth2) - [Ver guia](CONFIGURACAO_GOOGLE_OAUTH.md)
- Conta Gmail (para envio de e-mails 2FA) - [Ver guia](CONFIGURACAO_EMAIL.md)

## ⚙️ Setup Rápido

### 1. Configurar Variáveis de Ambiente

Copie o arquivo de exemplo e preencha com suas credenciais:

```bash
cp .env.example .env
```

**Edite o arquivo `.env` e configure:**
- `GOOGLE_CLIENT_ID` - [Como obter](CONFIGURACAO_GOOGLE_OAUTH.md)
- `GOOGLE_CLIENT_SECRET` - [Como obter](CONFIGURACAO_GOOGLE_OAUTH.md)
- `EMAIL_USERNAME` - Seu e-mail Gmail
- `E3. Rodar a aplicação

**IMPORTANTE:** As migrations serão executadas automaticamente na primeira execução.

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

Ou via Maven instalado:

```bash
mvn spring-boot:run
```

### 4sh
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

Ou via Maven instalado:

```bash
mvn spring-boot:run
```

### 3. Testar a aplicação

**Health Check:**
```bash
curl http://localhost:8080/api/health
```

**Listar imóveis:**
```bash
curl http://localhost:8080/api/imoveis
```

**Buscar imóveis com filtros:**
```bash
# Busca por cidade e características
curl "http://localhost:8080/api/imoveis?cidade=Curitiba&quartosMin=2&banheirosMin=1&vagasMin=1"

# Busca por faixa de preço e área
curl "http://localhost:8080/api/imoveis?valorMin=300000&valorMax=800000&areaMin=50&areaMax=200"

# B

**Testar 2FA:**
```bash
# Enviar código de verificação
curl -X POST http://localhost:8080/api/auth/2fa/enviar-codigo \
  -H "Content-Type: application/json" \
  -d '{"email":"seu-email@gmail.com"}'

# Verificar código (substitua 123456 pelo código recebido)
curl -X POST http://localhost:8080/api/auth/2fa/verificar-codigo \
  -H "Content-Type: application/json" \
  -d '{"email":"seu-email@gmail.com","codigo":"123456"}'
```usca textual (procura em múltiplos campos)
curl "http://localhost:8080/api/imoveis?busca=Apartamento"
```

## 📁 Estrutura do Projeto

```
src/main/java/com/leilao/arremateai/
├── controller/          # REST endpoints
│   ├── HealthController.java
│   └── ProdutoController.java
├── service/             # Regras de negócio
│   └── ProdutoService.java
├── repository/          # Acesso a dados
│   ├── LeiloeiraRepository.java
│   ├── LeilaoRepository.java
│   └── ProdutoRepository.java
├── domain/              # Entidades JPA
│   ├── Leiloeira.java
│   ├── Leilao.java
│   └── Produto.java
├── dto/                 # Request/Response DTOs
│   └── ProdutoResponse.java
└── ArremateaiApplication.java
```

## 🗄️ Banco de Dados

### Conexão Local

- **Host:** localhost
- **Porta:** 5432
- **Database:** arremateai
- **Usuário:** arremateai
- **Senha:** arremateai123

### Entidades Principais

- **Leiloeira**: Fontes de dados (PNCP, Superbid, etc.)
- **Leilao**: Leilões agregados
- **Produto**: Produtos/lotes disponíveis

## 🔌 Endpoints Disponíveis

### Health Check
```
GET /api/health
```

Resposta:
```json
{
  "status": "UP",
  "service": "arremateai",
  "timestamp": "2026-02-02T10:30:00",
  "version": "0.0.1-SNAPSHOT"
}
```

---

### 🏠 **Imóveis (CRUD Completo)** ✅ NOVO

#### **Listar Imóveis**
```
GET /api/imoveis
GET /api/imoveis?uf=SP&cidade=Curitiba&valorMin=300000&valorMax=800000&quartosMin=2&banheirosMin=1&vagasMin=1&areaMin=50&areaMax=200&page=0&size=20
```

**Parâmetros:**
- `uf` (opcional): Filtrar por UF
- `cidade` (opcional): Filtrar por cidade
- `tipoImovel` (opcional): Tipo (Casa, Apartamento, etc.)
- `instituicao` (opcional): Instituição financeira
- `valorMin` (opcional): Valor mínimo
- `valorMax` (opcional): Valor máximo
- `busca` (opcional): Busca textual (descrição, cidade, bairro, endereço, tipo, instituição)
- `quartosMin` (opcional): Número mínimo de quartos
- `banheirosMin` (opcional): Número mínimo de banheiros
- `vagasMin` (opcional): Número mínimo de vagas de garagem
- `areaMin` (opcional): Área mínima em m²
- `areaMax` (opcional): Área máxima em m²
- `page` (padrão: 0): Página
- `size` (padrão: 20): Itens por página
- `sortBy` (padrão: dataLeilao): Campo de ordenação
- `direction` (padrão: ASC): ASC ou DESC

#### **Buscar Imóvel por ID**
```
GET /api/imoveis/{id}
```

#### **Criar Novo Imóvel**
```
POST /api/imoveis
Content-Type: application/json

{
  "numeroLeilao": "2026-015",
  "descricao": "Casa de praia mobiliada, 4 suítes, piscina infinity com borda infinita e vista para o mar",
  "valorAvaliacao": 4500000.00,
  "dataLeilao": "2026-06-01T11:00:00",
  "uf": "SC",
  "instituicao": "Santander",
  "linkEdital": "https://example.com/leilao/015",
  "cidade": "Florianópolis",
  "bairro": "Jurerê Internacional",
  "areaTotal": 380.0,
  "tipoImovel": "CASA",
  "quartos": 4,
  "banheiros": 5,
  "vagas": 4,
  "endereco": "Rua das Bromélias, 789",
  "cep": "88053-300",
  "latitude": -27.4185,
  "longitude": -48.4953,
  "condicao": "NOVO",
  "aceitaFinanciamento": true,
  "observacoes": "Casa de praia de alto padrão completamente mobiliada e decorada por designer de interiores. 4 suítes com varanda e vista mar. Piscina infinity aquecida com borda infinita integrada ao mar. Deck em cumaru, jacuzzi para 8 pessoas. Cozinha gourmet Bertazzoni, adega climatizada, churrasqueira com forno de pizza. Sistema de som ambiente Sonos, ar condicionado em todos os ambientes. Gerador de energia. Segurança 24h no condomínio, acesso privativo à praia."
}
```

**Campos obrigatórios:**
- `numeroLeilao` - Identificador único do leilão
- `descricao` - Descrição do imóvel
- `valorAvaliacao` - Valor de avaliação
- `dataLeilao` - Data do leilão (ISO 8601)
- `uf` - Estado (2 letras)
- `instituicao` - Instituição organizadora

**Campos opcionais:**
- `linkEdital`, `cidade`, `bairro`, `areaTotal`, `tipoImovel`
- `quartos`, `banheiros`, `vagas` - Características do imóvel
- `endereco`, `cep`, `latitude`, `longitude` - Localização
- `condicao` - Estado do imóvel (NOVO, USADO, REFORMADO)
- `aceitaFinanciamento` - Boolean
- `observacoes` - Detalhes adicionais

#### **Atualizar Imóvel (Completo)**
```
PUT /api/imoveis/{id}
Content-Type: application/json

{
  "numeroLeilao": "2026-015",
  "descricao": "Casa de praia mobiliada atualizada",
  "valorAvaliacao": 4800000.00,
  "dataLeilao": "2026-06-01T11:00:00",
  "uf": "SC",
  "instituicao": "Santander",
  "linkEdital": "https://example.com/leilao/015",
  "cidade": "Florianópolis",
  "bairro": "Jurerê Internacional",
  "areaTotal": 380.0,
  "tipoImovel": "CASA",
  "quartos": 4,
  "banheiros": 5,
  "vagas": 4,
  "endereco": "Rua das Bromélias, 789",
  "cep": "88053-300",
  "latitude": -27.4185,
  "longitude": -48.4953,
  "condicao": "NOVO",
  "aceitaFinanciamento": true,
  "observacoes": "Observações atualizadas"
}
```

#### **Atualizar Imóvel (Parcial)**
```
PATCH /api/imoveis/{id}
Content-Type: application/json

{
  "valorAvaliacao": 500000,
  "cidade": "Campinas"
}
```

#### **Remover Imóvel (Soft Delete)**
```
DELETE /api/imoveis/{id}
```
_Nota: Remove logicamente (marca como inativo), não apaga do banco._

---

### Listar Produtos
```
GET /api/produtos?categoria=VEICULOS&valorMin=10000&valorMax=50000&page=0&size=20
```

Parâmetros:
- `categoria` (opcional): Filtrar por categoria
- `valorMin` (opcional): Valor mínimo
- `valorMax` (opcional): Valor máximo
- `page` (padrão: 0): Página
- `size` (padrão: 20): Itens por página
- `sortBy` (padrão: id): Campo de ordenação
- `direction` (padrão: DESC): ASC ou DESC

### Buscar Produto por ID
```
GET /api/produtos/{id}
```

## 🛠️ Comandos Úteis

### Parar containers
```bash
docker-compose down
```

### Ver logs dos containers
```bash
docker-compose logs -f
```

### Limpar e recriar banco
```bash
docker-compose down -v
docker-compose up -d
```

### Build do projeto
```bash
mvn clean package
```

### Rodar testes
```bash
mvn test
```

## 📝 Próximos Passos

- [ ] Implementar integração com PNCP
- [ ] Criar scheduler de sincronização
- [ ] Implementar cache com Redis
- [ ] Adicionar autenticação/autorização
- [ ] Criar entidades de Usuário, Favoritos e Alertas
- [ ] Implementar OpenAPI/Swagger
- [ ] Adicionar testes unitários e de integração
- [ ] Deploy na AWS

## 📚 Documentação

A documentação completa do projeto está em `/src/docs`:

- [Decisões Técnicas](src/docs/negócio/DECISOES_TECNICAS.md)
- [Listagem de APIs Públicas](src/docs/negócio/LISTAGEM_APIS_PUBLICAS.md)
- [Proposta](src/docs/negócio/PROPOSTA.md)
- [Roadmap](src/docs/negócio/ROADMAP.md)

## 📧 Contato

Projeto QuinBid - 2026

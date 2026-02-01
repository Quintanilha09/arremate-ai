# ArrematAI - Backend

Plataforma centralizada que agrega produtos de múltiplas leiloeiras brasileiras.

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.2.2**
- **PostgreSQL 16**
- **Redis 7**
- **Maven**

## 📋 Pré-requisitos

- Java 17 instalado
- Docker e Docker Compose instalados
- Maven 3.8+ (ou use o wrapper `./mvnw`)

## ⚙️ Setup Rápido

### 1. Subir banco de dados (PostgreSQL + Redis)

```bash
docker-compose up -d
```

Verificar se os containers estão rodando:

```bash
docker-compose ps
```

### 2. Rodar a aplicação

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

### 3. Testar a aplicação

**Health Check:**
```bash
curl http://localhost:8080/api/health
```

**Listar produtos:**
```bash
curl http://localhost:8080/api/produtos
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
  "timestamp": "2026-01-30T10:30:00",
  "version": "0.0.1-SNAPSHOT"
}
```

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

- [Decisões Técnicas](src/docs/DECISOES_TECNICAS.md)
- [Listagem de APIs Públicas](src/docs/LISTAGEM_APIS_PUBLICAS.md)
- [Proposta](src/docs/PROPOSTA.md)
- [Roadmap](src/docs/ROADMAP.md)

## 📧 Contato

Projeto QuinBid - 2026

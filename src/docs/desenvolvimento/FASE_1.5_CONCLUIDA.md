# 📋 Fase 1.5 - Sistema de Favoritos (Wishlist) ✅

**Data de Conclusão:** 02/02/2026  
**Status:** ✅ CONCLUÍDO

## 📝 Resumo

Implementação completa do sistema de favoritos (wishlist) que permite aos usuários salvar imóveis de interesse para consulta posterior. Esta funcionalidade foi inspirada no Airbnb e em plataformas de e-commerce modernas.

---

## 🏗️ Arquitetura Implementada

### Entidades

#### `Favorito.java`
```java
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "imovel_id"}))
public class Favorito {
    UUID id;
    String usuarioId;        // Temporário até Fase 2 (JWT)
    Imovel imovel;          // Relacionamento ManyToOne
    LocalDateTime createdAt;
}
```

**Características:**
- Constraint única para evitar duplicatas (usuário + imóvel)
- Cascade delete: ao remover imóvel, remove favoritos associados
- usuarioId como String temporariamente (será substituído por relação com Usuario na Fase 2)

---

## 📡 Endpoints Implementados

### Base URL: `/api/favoritos`

#### 1. **POST** `/{imovelId}` - Adicionar aos Favoritos
**Headers:**
- `X-Usuario-Id`: ID do usuário (string temporária)

**Response 201 Created:**
```json
{
  "id": "uuid-do-favorito",
  "usuarioId": "user123",
  "imovel": {
    "id": "uuid-do-imovel",
    "numeroLeilao": "LEI-2024-001",
    "descricao": "Casa com 3 quartos...",
    "valorAvaliacao": 450000.00,
    // ... outros campos do imóvel
  },
  "createdAt": "2026-02-02T15:30:00"
}
```

**Validações:**
- ✅ Imóvel deve existir
- ✅ Imóvel deve estar ativo
- ✅ Não permite duplicatas (mesmo usuário + mesmo imóvel)

---

#### 2. **DELETE** `/{imovelId}` - Remover dos Favoritos
**Headers:**
- `X-Usuario-Id`: ID do usuário

**Response:** `204 No Content`

**Exceções:**
- `404 Not Found`: Favorito não encontrado

---

#### 3. **GET** `/` - Listar Favoritos
**Headers:**
- `X-Usuario-Id`: ID do usuário

**Response 200 OK:**
```json
[
  {
    "id": "favorito-uuid-1",
    "usuarioId": "user123",
    "imovel": { /* dados completos */ },
    "createdAt": "2026-02-02T15:30:00"
  },
  {
    "id": "favorito-uuid-2",
    "usuarioId": "user123",
    "imovel": { /* dados completos */ },
    "createdAt": "2026-02-01T10:15:00"
  }
]
```

**Ordenação:** Mais recentes primeiro (createdAt DESC)

---

#### 4. **GET** `/{imovelId}/status` - Verificar se é Favorito
**Headers:**
- `X-Usuario-Id`: ID do usuário

**Response 200 OK:**
```json
{
  "favorito": true
}
```

**Uso:** Útil para exibir ícone de coração preenchido/vazio na UI

---

#### 5. **GET** `/count` - Contar Favoritos
**Headers:**
- `X-Usuario-Id`: ID do usuário

**Response 200 OK:**
```json
{
  "total": 12
}
```

**Uso:** Badge de contador na navbar, ex: "♥ (12)"

---

## 🗄️ Migration V005

**Arquivo:** `V005__criar_tabela_favoritos.sql`

**Estrutura:**
```sql
CREATE TABLE favorito (
    id UUID PRIMARY KEY,
    usuario_id VARCHAR(100) NOT NULL,
    imovel_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_favorito_usuario_imovel UNIQUE (usuario_id, imovel_id),
    CONSTRAINT fk_favorito_imovel FOREIGN KEY (imovel_id) 
        REFERENCES imovel(id) ON DELETE CASCADE
);

-- Índices otimizados
CREATE INDEX idx_favorito_usuario_id ON favorito(usuario_id);
CREATE INDEX idx_favorito_imovel_id ON favorito(imovel_id);
CREATE INDEX idx_favorito_created_at ON favorito(created_at DESC);
```

---

## 🎯 Funcionalidades Implementadas

### Service Layer (`FavoritoService.java`)

✅ **adicionarFavorito()**
- Valida existência e status ativo do imóvel
- Previne duplicatas
- Retorna favorito completo com dados do imóvel

✅ **removerFavorito()**
- Validação de existência
- Remoção transacional

✅ **listarFavoritos()**
- Retorna todos os favoritos do usuário
- Ordenado por data (mais recentes primeiro)
- Inclui dados completos do imóvel

✅ **isFavorito()**
- Verificação rápida de status
- Otimizado para UI (não carrega dados completos)

✅ **contarFavoritos()**
- Contagem eficiente (COUNT query)
- Útil para badges e estatísticas

---

## 🧪 Exemplos de Teste

### PowerShell (Windows)

#### 1. Adicionar Favorito
```powershell
$headers = @{
    "X-Usuario-Id" = "user123"
    "Content-Type" = "application/json"
}
$imovelId = "c9aac607-d803-412b-ab08-8ab009e8987a"
Invoke-RestMethod -Uri "http://localhost:8080/api/favoritos/$imovelId" `
    -Method POST -Headers $headers | ConvertTo-Json -Depth 5
```

#### 2. Listar Favoritos
```powershell
$headers = @{ "X-Usuario-Id" = "user123" }
Invoke-RestMethod -Uri "http://localhost:8080/api/favoritos" `
    -Method GET -Headers $headers | ConvertTo-Json -Depth 5
```

#### 3. Verificar Status
```powershell
$headers = @{ "X-Usuario-Id" = "user123" }
$imovelId = "c9aac607-d803-412b-ab08-8ab009e8987a"
Invoke-RestMethod -Uri "http://localhost:8080/api/favoritos/$imovelId/status" `
    -Method GET -Headers $headers | ConvertTo-Json
```

#### 4. Contar Favoritos
```powershell
$headers = @{ "X-Usuario-Id" = "user123" }
Invoke-RestMethod -Uri "http://localhost:8080/api/favoritos/count" `
    -Method GET -Headers $headers | ConvertTo-Json
```

#### 5. Remover Favorito
```powershell
$headers = @{ "X-Usuario-Id" = "user123" }
$imovelId = "c9aac607-d803-412b-ab08-8ab009e8987a"
Invoke-RestMethod -Uri "http://localhost:8080/api/favoritos/$imovelId" `
    -Method DELETE -Headers $headers
```

---

### cURL (Linux/Mac)

```bash
# Adicionar
curl -X POST "http://localhost:8080/api/favoritos/{imovelId}" \
  -H "X-Usuario-Id: user123"

# Listar
curl -X GET "http://localhost:8080/api/favoritos" \
  -H "X-Usuario-Id: user123"

# Verificar status
curl -X GET "http://localhost:8080/api/favoritos/{imovelId}/status" \
  -H "X-Usuario-Id: user123"

# Contar
curl -X GET "http://localhost:8080/api/favoritos/count" \
  -H "X-Usuario-Id: user123"

# Remover
curl -X DELETE "http://localhost:8080/api/favoritos/{imovelId}" \
  -H "X-Usuario-Id: user123"
```

---

## ⚠️ Tratamento de Exceções

### `EntityNotFoundException` (404)
- Imóvel não existe
- Imóvel está inativo
- Favorito não encontrado ao tentar remover

**Response:**
```json
{
  "timestamp": "2026-02-02T15:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Imóvel não encontrado ou inativo: uuid",
  "path": "/api/favoritos/uuid"
}
```

### `IllegalStateException` (409)
- Tentar adicionar favorito duplicado

**Response:**
```json
{
  "timestamp": "2026-02-02T15:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Imóvel já está nos favoritos",
  "path": "/api/favoritos/uuid"
}
```

---

## 📊 Queries Otimizadas

### Repository Methods

```java
// Busca ordenada com JOIN FETCH para evitar N+1
findByUsuarioIdOrderByCreatedAtDesc(String usuarioId)

// Verificação rápida (EXISTS query)
existsByUsuarioIdAndImovelId(String usuarioId, UUID imovelId)

// Contagem eficiente
countByUsuarioId(String usuarioId)

// Busca específica
findByUsuarioIdAndImovelId(String usuarioId, UUID imovelId)
```

---

## 🔄 Integração com Frontend

### Fluxo Típico de Uso

1. **Listagem de Imóveis**
   - Para cada card, fazer `GET /api/favoritos/{id}/status`
   - Exibir ícone de coração preenchido/vazio

2. **Toggle Favorito**
   - Se não é favorito: `POST /api/favoritos/{id}`
   - Se é favorito: `DELETE /api/favoritos/{id}`

3. **Página de Favoritos**
   - `GET /api/favoritos`
   - Renderizar grid/lista com imóveis salvos

4. **Badge no Header**
   - `GET /api/favoritos/count`
   - Exibir: `♥ (12)`

---

## 🚀 Próximos Passos (Fase 2)

### Melhorias Planejadas:

1. **Autenticação JWT**
   - Substituir `X-Usuario-Id` header por token JWT
   - Extrair userId automaticamente do token
   - Remover necessidade de passar header manualmente

2. **Entidade Usuario**
   - Criar relacionamento `@ManyToOne` com `Usuario`
   - Migrar de `String usuarioId` para `Usuario usuario`

3. **Endpoints Adicionais**
   - `GET /api/favoritos/estatisticas` - Estatísticas de favoritos
   - `POST /api/favoritos/compartilhar` - Compartilhar lista
   - `GET /api/favoritos/exportar` - Exportar para PDF

4. **Notificações**
   - Alertar quando imóvel favoritado tiver mudança de preço
   - Notificar quando leilão está próximo

---

## ✅ Checklist de Validação

- [x] Entidade `Favorito` criada
- [x] Repository com queries customizadas
- [x] Service com validações completas
- [x] Controller com 5 endpoints
- [x] Migration V005 aplicada
- [x] Constraint única (usuario + imovel)
- [x] Cascade delete configurado
- [x] Índices otimizados
- [x] Tratamento de exceções
- [x] Logs estruturados
- [x] Compilação bem-sucedida ✅
- [x] Documentação completa ✅

---

## 📦 Arquivos Criados

```
src/main/java/com/leilao/arremateai/
├── domain/
│   └── Favorito.java                    ✅ Entidade
├── repository/
│   └── FavoritoRepository.java          ✅ Repository com queries
├── service/
│   └── FavoritoService.java             ✅ Lógica de negócio
├── controller/
│   └── FavoritoController.java          ✅ 5 endpoints REST
└── dto/
    └── FavoritoResponse.java            ✅ DTO de resposta

src/main/resources/db/migration/
└── V005__criar_tabela_favoritos.sql     ✅ Migration
```

---

## 🎓 Aprendizados e Decisões Técnicas

### Por que String usuarioId?
- Decisão temporária para permitir testes sem autenticação
- Facilita desenvolvimento e testes da funcionalidade
- Será substituído por relação JPA com `Usuario` na Fase 2
- Header customizado `X-Usuario-Id` simula autenticação

### Cascade DELETE
- Ao remover um imóvel, favoritos são removidos automaticamente
- Evita registros órfãos no banco
- Mantém integridade referencial

### Unique Constraint
- Garante no banco que não há duplicatas
- Complementa validação da aplicação
- Proteção contra race conditions

### Ordenação por created_at DESC
- Favoritos mais recentes aparecem primeiro
- UX melhor: usuário vê o que adicionou por último
- Índice criado para otimizar esta query

---

**🎯 Fase 1.5 - Sistema de Favoritos: CONCLUÍDA COM SUCESSO! ✅**

**Próximo passo:** Fase 2 - Autenticação e Usuários (JWT + Spring Security)

# 📊 Fase 1.2 - Expansão do Modelo de Dados - CONCLUÍDA ✅

## 🎯 Objetivo
Enriquecer o modelo de dados dos imóveis para competir com plataformas como QuintoAndar, OLX e Airbnb, adicionando informações essenciais e sistema de imagens.

## ✨ Novos Campos Implementados

### Informações Essenciais
| Campo | Tipo | Descrição |
|-------|------|-----------|
| `quartos` | Integer | Número de quartos |
| `banheiros` | Integer | Número de banheiros |
| `vagas` | Integer | Vagas de garagem |
| `endereco` | String(500) | Endereço completo |
| `cep` | String(10) | CEP (formato: 12345-678) |
| `latitude` | Decimal(10,8) | Coordenadas GPS para mapa |
| `longitude` | Decimal(11,8) | Coordenadas GPS para mapa |

### Informações Adicionais
| Campo | Tipo | Descrição | Valores Aceitos |
|-------|------|-----------|-----------------|
| `condicao` | String(50) | Condição do imóvel | NOVO, USADO, REFORMADO |
| `aceitaFinanciamento` | Boolean | Aceita financiamento bancário | true/false (padrão: false) |
| `observacoes` | String(2000) | Observações gerais | Texto livre |
| `status` | String(20) | Status do imóvel | DISPONIVEL, VENDIDO, SUSPENSO (padrão: DISPONIVEL) |

### Sistema de Imagens
Nova entidade `ImagemImovel` com relacionamento 1:N com Imovel:

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | Long | ID único da imagem |
| `imovelId` | Long | Referência ao imóvel |
| `url` | String(1000) | URL da imagem |
| `legenda` | String(500) | Descrição/legenda |
| `principal` | Boolean | Se é a imagem de capa |
| `ordem` | Integer | Ordem de exibição |
| `createdAt` | Timestamp | Data de criação |

## 📝 Arquivos Modificados

### 1. Entidades
- ✅ `Imovel.java` - Adicionados 13 novos campos + relacionamento com imagens
- ✅ `ImagemImovel.java` - Nova entidade criada

### 2. DTOs
- ✅ `ImovelRequest.java` - Validações para novos campos
- ✅ `ImovelResponse.java` - Retorno com todos os campos + lista de imagens
- ✅ `ImagemResponse.java` - Novo DTO criado

### 3. Repository
- ✅ `ImagemImovelRepository.java` - Novo repository criado com queries customizadas

### 4. Mapper
- ✅ `ImovelMapper.java` - Mapeamento bidirecional completo incluindo imagens

### 5. Service
- ✅ `ImovelService.java` - Métodos `atualizarImovel` e `atualizarParcial` atualizados

### 6. Migration
- ✅ `V003__expandir_modelo_imovel.sql` - Script SQL criado

## 🗄️ Migration SQL

```sql
-- Novos campos adicionados à tabela imovel
ALTER TABLE imovel ADD COLUMN quartos INTEGER;
ALTER TABLE imovel ADD COLUMN banheiros INTEGER;
ALTER TABLE imovel ADD COLUMN vagas INTEGER;
ALTER TABLE imovel ADD COLUMN endereco VARCHAR(500);
ALTER TABLE imovel ADD COLUMN cep VARCHAR(10);
ALTER TABLE imovel ADD COLUMN latitude DECIMAL(10, 8);
ALTER TABLE imovel ADD COLUMN longitude DECIMAL(11, 8);
ALTER TABLE imovel ADD COLUMN condicao VARCHAR(50);
ALTER TABLE imovel ADD COLUMN aceita_financiamento BOOLEAN DEFAULT false;
ALTER TABLE imovel ADD COLUMN observacoes VARCHAR(2000);
ALTER TABLE imovel ADD COLUMN status VARCHAR(20) DEFAULT 'DISPONIVEL';

-- Nova tabela de imagens
CREATE TABLE imagem_imovel (
    id BIGSERIAL PRIMARY KEY,
    imovel_id BIGINT NOT NULL,
    url VARCHAR(1000) NOT NULL,
    legenda VARCHAR(500),
    principal BOOLEAN DEFAULT false,
    ordem INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_imagem_imovel FOREIGN KEY (imovel_id) 
        REFERENCES imovel(id) ON DELETE CASCADE
);

-- Índices para performance
CREATE INDEX idx_imagem_imovel_id ON imagem_imovel(imovel_id);
CREATE INDEX idx_imagem_principal ON imagem_imovel(principal);
CREATE INDEX idx_imovel_quartos ON imovel(quartos);
CREATE INDEX idx_imovel_status ON imovel(status);
```

## 📊 Exemplo de Request Completo (POST)

```json
{
  "numeroLeilao": "TESTE-SP-2026-300",
  "descricao": "Apartamento luxuoso com vista panorâmica",
  "valorAvaliacao": 850000.00,
  "dataLeilao": "2026-08-15",
  "uf": "SP",
  "instituicao": "Banco Exemplo",
  "linkEdital": "https://exemplo.com/edital/300",
  "cidade": "São Paulo",
  "bairro": "Jardins",
  "areaTotal": 120.50,
  "tipoImovel": "Apartamento",
  "quartos": 3,
  "banheiros": 2,
  "vagas": 2,
  "endereco": "Rua das Flores, 123 - Apt 45",
  "cep": "01234-567",
  "latitude": -23.561684,
  "longitude": -46.656139,
  "condicao": "REFORMADO",
  "aceitaFinanciamento": true,
  "observacoes": "Imóvel em excelente estado, recém reformado com acabamento de primeira linha",
  "status": "DISPONIVEL"
}
```

## 📊 Exemplo de Response Completo

```json
{
  "id": 25,
  "numeroLeilao": "TESTE-SP-2026-300",
  "descricao": "Apartamento luxuoso com vista panorâmica",
  "valorAvaliacao": 850000.00,
  "dataLeilao": "2026-08-15",
  "uf": "SP",
  "instituicao": "Banco Exemplo",
  "linkEdital": "https://exemplo.com/edital/300",
  "cidade": "São Paulo",
  "bairro": "Jardins",
  "areaTotal": 120.50,
  "tipoImovel": "Apartamento",
  "quartos": 3,
  "banheiros": 2,
  "vagas": 2,
  "endereco": "Rua das Flores, 123 - Apt 45",
  "cep": "01234-567",
  "latitude": -23.561684,
  "longitude": -46.656139,
  "condicao": "REFORMADO",
  "aceitaFinanciamento": true,
  "observacoes": "Imóvel em excelente estado, recém reformado com acabamento de primeira linha",
  "status": "DISPONIVEL",
  "imagens": [
    {
      "id": 1,
      "url": "https://exemplo.com/images/imovel-300-1.jpg",
      "legenda": "Fachada do prédio",
      "principal": true,
      "ordem": 1
    },
    {
      "id": 2,
      "url": "https://exemplo.com/images/imovel-300-2.jpg",
      "legenda": "Sala de estar",
      "principal": false,
      "ordem": 2
    }
  ],
  "imagemPrincipal": "https://exemplo.com/images/imovel-300-1.jpg"
}
```

## ✅ Validações Implementadas

### Campos Numéricos
- `quartos`, `banheiros`, `vagas`: Não podem ser negativos (min=0)
- `latitude`: Entre -90 e 90
- `longitude`: Entre -180 e 180

### Campos de Texto
- `cep`: Formato 12345-678 ou 12345678 (regex)
- `endereco`: Máximo 500 caracteres
- `observacoes`: Máximo 2000 caracteres

### Enumerações
- `condicao`: Apenas NOVO, USADO ou REFORMADO
- `status`: Apenas DISPONIVEL, VENDIDO ou SUSPENSO

## 🎨 Recursos Adicionais

### Imagem Principal Automática
- O campo `imagemPrincipal` no response retorna automaticamente a URL da primeira imagem marcada como `principal: true`
- Facilita renderização de cards/listagens no frontend

### Ordenação de Imagens
- Campo `ordem` permite controlar a sequência de exibição na galeria
- Imagens retornadas ordenadas por `ordem ASC` automaticamente

### Cascade Delete
- Ao deletar um imóvel (soft delete), as imagens associadas são mantidas
- Ao fazer hard delete (se implementado futuramente), imagens são removidas automaticamente

## 🔄 Compatibilidade Retroativa

✅ **Endpoints existentes continuam funcionando**
- Todos os novos campos são opcionais
- Requests antigos sem os novos campos são aceitos
- Responses incluem `null` para campos não preenchidos

## 📈 Próximos Passos (Fase 1.3)

- [ ] Sistema de Upload de Imagens
- [ ] Integração com Amazon S3 / Azure Blob Storage
- [ ] Geração automática de thumbnails
- [ ] Endpoints CRUD para gerenciar imagens
- [ ] Limite de 20 imagens por imóvel
- [ ] Validação de formato (jpg, png, webp)
- [ ] Validação de tamanho máximo (5MB por imagem)

---

**Data de Conclusão:** 2 de Fevereiro de 2026
**Desenvolvedor:** GitHub Copilot + Gabriel

# 🎯 FASE 1.3 - Sistema de Upload de Imagens

## ✅ Implementação Completa

### 📋 Resumo
Sistema de upload múltiplo de imagens para imóveis com validação, armazenamento local e gerenciamento completo (upload, listagem, atualização, remoção e definição de imagem principal).

---

## 🏗️ Arquitetura Implementada

### 1. **Configuração de Upload** (`application.properties`)
```properties
# File Upload Configuration
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=20MB
spring.servlet.multipart.file-size-threshold=2KB

# Storage Configuration
storage.location=uploads
```

**Características:**
- ✅ Limite de 5MB por arquivo
- ✅ Limite de 20MB por requisição (múltiplos arquivos)
- ✅ Armazenamento em diretório `uploads/`
- ✅ Criação automática do diretório

---

### 2. **Configuração de Recursos Estáticos** (`FileStorageConfig.java`)
```java
@Configuration
public class FileStorageConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPathStr);
    }
}
```

**Características:**
- ✅ Serve imagens via endpoint `/uploads/{filename}`
- ✅ Acesso público às imagens
- ✅ URLs completas geradas automaticamente

---

### 3. **Service de Upload** (`ImagemService.java`)

#### Métodos Implementados:

**a) Upload Múltiplo**
```java
uploadImagens(UUID imovelId, List<MultipartFile> files)
```
- ✅ Validação de formato (jpg, jpeg, png, webp)
- ✅ Validação de tamanho (máx 5MB)
- ✅ Validação de conteúdo (verifica se é imagem real)
- ✅ Geração de UUID para nome único
- ✅ Define primeira imagem como principal automaticamente
- ✅ Controle automático de ordem de exibição

**b) Listar Imagens**
```java
listarImagens(UUID imovelId)
```
- ✅ Retorna todas as imagens de um imóvel
- ✅ Ordenadas por campo `ordem`
- ✅ Converte para DTO `ImagemResponse`

**c) Atualizar Imagem**
```java
atualizarImagem(UUID imagemId, String legenda, Integer ordem)
```
- ✅ Atualiza legenda da imagem
- ✅ Atualiza ordem de exibição
- ✅ Parâmetros opcionais (atualização parcial)

**d) Definir Imagem Principal**
```java
definirImagemPrincipal(UUID imagemId)
```
- ✅ Remove flag `principal` das outras imagens
- ✅ Define nova imagem como principal
- ✅ Automático ao listar imóveis (campo `imagemPrincipal` no response)

**e) Remover Imagem**
```java
removerImagem(UUID imagemId)
```
- ✅ Remove arquivo físico do disco
- ✅ Remove registro do banco de dados
- ✅ Se era principal, define outra como principal automaticamente

---

### 4. **Controller REST** (`ImagemController.java`)

#### Endpoints Implementados:

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/imoveis/{imovelId}/imagens` | Upload múltiplo de imagens |
| `GET` | `/api/imoveis/{imovelId}/imagens` | Listar imagens do imóvel |
| `PUT` | `/api/imoveis/imagens/{imagemId}` | Atualizar legenda/ordem |
| `PATCH` | `/api/imoveis/imagens/{imagemId}/principal` | Definir como principal |
| `DELETE` | `/api/imoveis/imagens/{imagemId}` | Remover imagem |

---

## 🧪 Testes e Uso

### 1. **Upload de Imagens**

**PowerShell:**
```powershell
# Criar imóvel primeiro
$body = @{
    numeroLeilao = "2026-001"
    descricao = "Apartamento com Vista Mar"
    valorAvaliacao = 450000
    dataLeilao = "2026-03-15"
    uf = "RJ"
    cidade = "Rio de Janeiro"
    instituicao = "Caixa Econômica Federal"
    tipoImovel = "Apartamento"
    quartos = 3
    banheiros = 2
    vagas = 2
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:8080/api/imoveis" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body

$imovelId = ($response.Content | ConvertFrom-Json).id
Write-Host "Imóvel criado: $imovelId"

# Upload de múltiplas imagens
$files = @(
    "C:\caminho\para\imagem1.jpg",
    "C:\caminho\para\imagem2.jpg",
    "C:\caminho\para\imagem3.png"
)

$formData = @{}
$files | ForEach-Object {
    $formData.Add("files", (Get-Item $_))
}

Invoke-WebRequest -Uri "http://localhost:8080/api/imoveis/$imovelId/imagens" `
    -Method POST `
    -Form $formData
```

**cURL (Linux/Mac/Git Bash):**
```bash
# Upload múltiplo
curl -X POST "http://localhost:8080/api/imoveis/{imovelId}/imagens" \
  -F "files=@imagem1.jpg" \
  -F "files=@imagem2.jpg" \
  -F "files=@imagem3.png"
```

**Resposta Esperada:**
```json
[
  {
    "id": "uuid-1",
    "url": "http://localhost:8080/uploads/uuid-arquivo-1.jpg",
    "legenda": null,
    "principal": true,
    "ordem": 1
  },
  {
    "id": "uuid-2",
    "url": "http://localhost:8080/uploads/uuid-arquivo-2.jpg",
    "legenda": null,
    "principal": false,
    "ordem": 2
  },
  {
    "id": "uuid-3",
    "url": "http://localhost:8080/uploads/uuid-arquivo-3.png",
    "legenda": null,
    "principal": false,
    "ordem": 3
  }
]
```

---

### 2. **Listar Imagens do Imóvel**

```bash
curl http://localhost:8080/api/imoveis/{imovelId}/imagens
```

---

### 3. **Atualizar Legenda e Ordem**

**PowerShell:**
```powershell
Invoke-WebRequest -Uri "http://localhost:8080/api/imoveis/imagens/{imagemId}?legenda=Vista frontal&ordem=1" `
    -Method PUT
```

**cURL:**
```bash
curl -X PUT "http://localhost:8080/api/imoveis/imagens/{imagemId}?legenda=Vista%20frontal&ordem=1"
```

---

### 4. **Definir Imagem Principal**

```bash
curl -X PATCH "http://localhost:8080/api/imoveis/imagens/{imagemId}/principal"
```

---

### 5. **Remover Imagem**

```bash
curl -X DELETE "http://localhost:8080/api/imoveis/imagens/{imagemId}"
```

---

## 📊 Validações Implementadas

### Validações de Arquivo:
- ✅ **Formato:** Apenas jpg, jpeg, png, webp
- ✅ **Tamanho:** Máximo 5MB por arquivo
- ✅ **Conteúdo:** Verifica se é imagem real (ImageIO)
- ✅ **Arquivo vazio:** Rejeita arquivos sem conteúdo

### Validações de Negócio:
- ✅ **Imóvel existe:** Verifica antes de adicionar imagens
- ✅ **Imóvel ativo:** Não permite upload em imóveis inativos
- ✅ **Imagem principal automática:** Primeira imagem sempre é principal
- ✅ **Remoção inteligente:** Se remover a principal, outra vira principal

---

## 🗂️ Estrutura de Arquivos

```
uploads/
  ├── uuid-1.jpg
  ├── uuid-2.jpg
  ├── uuid-3.png
  └── ...
```

**Características:**
- ✅ Nomes únicos (UUID)
- ✅ Preserva extensão original
- ✅ Armazenamento local (desenvolvimento)
- ✅ Fácil migração para S3/Azure no futuro

---

## 🔒 Segurança e Boas Práticas

- ✅ Validação rigorosa de formato
- ✅ Limite de tamanho (DoS protection)
- ✅ Verificação de conteúdo (não aceita qualquer arquivo renomeado)
- ✅ UUID para nomes (evita path traversal)
- ✅ Tratamento de exceções customizado
- ✅ Logging detalhado para auditoria

---

## 🚀 Próximos Passos (Melhorias Futuras)

### Fase 2 (Opcional):
- [ ] Geração de thumbnails automáticos
- [ ] Compressão de imagens para economizar espaço
- [ ] Migração para AWS S3 ou Azure Blob Storage
- [ ] CDN para servir imagens com alta performance
- [ ] Detecção de imagens duplicadas (hash MD5)
- [ ] Marca d'água automática
- [ ] Processamento assíncrono de imagens grandes

---

## 📝 Checklist de Conclusão

- [x] Configuração de multipart upload
- [x] Service de upload com validações
- [x] Controller REST com todos os endpoints
- [x] Servir arquivos estáticos
- [x] Tratamento de exceções
- [x] Logging adequado
- [x] Compilação bem-sucedida
- [x] Documentação completa
- [x] Testes manuais (curl/PowerShell)

---

## 🎯 Status: **CONCLUÍDA** ✅

**Data:** 02/02/2026  
**Build:** `mvn clean compile -DskipTests` - **SUCCESS**  
**Próxima Fase:** 1.4 - Endpoints de Estatísticas e Dashboard

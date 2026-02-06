# Tratamento de Exceções - ArremateAI

## Resumo das Melhorias

Este documento descreve todas as exceções que agora são tratadas adequadamente na aplicação, garantindo que:
1. **Código não quebra em tempo de execução**
2. **Usuário recebe mensagens claras e compreensíveis**

---

## Backend - GlobalExceptionHandler

### 1. Exceções de Validação

#### `MethodArgumentNotValidException` (400 BAD REQUEST)
- **Quando ocorre**: Campos de formulário inválidos (validação @Valid)
- **Mensagem**: "Erro de validação nos dados enviados" + detalhes por campo
- **Exemplo**: Email inválido, senha muito curta, campos obrigatórios vazios

#### `IllegalArgumentException` (400 BAD REQUEST)
- **Quando ocorre**: Argumentos inválidos em métodos
- **Mensagem**: Mensagem específica do erro (ex: "Email já cadastrado", "CNPJ inválido")
- **Exemplo**: CNPJ duplicado, email já em uso

#### `MethodArgumentTypeMismatchException` (400 BAD REQUEST)
- **Quando ocorre**: Tipo de parâmetro incorreto na URL
- **Mensagem**: "Parâmetro 'X' deve ser do tipo Y"
- **Exemplo**: Passar texto onde deveria ser número (UUID inválido)

#### `MissingServletRequestParameterException` (400 BAD REQUEST)
- **Quando ocorre**: Parâmetro obrigatório ausente na query
- **Mensagem**: "Parâmetro obrigatório 'X' está ausente"
- **Exemplo**: Faltou parâmetro `?page=1` em rota paginada

#### `HttpMessageNotReadableException` (400 BAD REQUEST)
- **Quando ocorre**: JSON malformado ou campo com tipo errado
- **Mensagem**: "Formato de dados inválido. Verifique o JSON enviado."
- **Exemplo**: JSON com sintaxe incorreta, campo string onde deveria ser número

---

### 2. Exceções de Autenticação e Autorização

#### `UsernameNotFoundException` (401 UNAUTHORIZED)
- **Quando ocorre**: Tentativa de login com email não cadastrado
- **Mensagem**: "Usuário não cadastrado. Verifique seu email ou cadastre-se."
- **Exemplo**: Login com email inexistente

#### `BadCredentialsException` (401 UNAUTHORIZED)
- **Quando ocorre**: Senha incorreta no login
- **Mensagem**: "Email ou senha incorretos"
- **Exemplo**: Login com senha errada

#### `AuthenticationException` (401 UNAUTHORIZED)
- **Quando ocorre**: Outros erros de autenticação
- **Mensagem**: "Falha na autenticação. Verifique suas credenciais."
- **Exemplo**: Token inválido ou expirado

#### `AccessDeniedException` (403 FORBIDDEN)
- **Quando ocorre**: Usuário sem permissão para acessar recurso
- **Mensagem**: "Você não tem permissão para acessar este recurso"
- **Exemplo**: Comprador tentando acessar rota de admin

---

### 3. Exceções de Recursos Não Encontrados

#### `ResourceNotFoundException` (404 NOT FOUND) - CUSTOMIZADA
- **Quando ocorre**: Recurso específico não encontrado
- **Mensagem**: "Recurso não encontrado com ID: X" (mensagem customizável)
- **Exemplo**: Imóvel não encontrado, vendedor não encontrado

#### `EntityNotFoundException` (404 NOT FOUND)
- **Quando ocorre**: Entidade JPA não encontrada
- **Mensagem**: "Recurso não encontrado"
- **Exemplo**: Busca no banco retornou vazio

#### `NoHandlerFoundException` (404 NOT FOUND)
- **Quando ocorre**: Rota inexistente
- **Mensagem**: "Rota não encontrada: GET /api/exemplo"
- **Exemplo**: URL digitada incorretamente

---

### 4. Exceções de Estado e Conflito

#### `IllegalStateException` (409 CONFLICT)
- **Quando ocorre**: Operação inválida para o estado atual
- **Mensagem**: Mensagem específica do erro
- **Exemplo**: "Vendedor não pode enviar documentos neste momento", "Imóvel já está ativo"

#### `DataIntegrityViolationException` (409 CONFLICT)
- **Quando ocorre**: Violação de regras do banco de dados
- **Mensagens inteligentes**:
  - Unique constraint: "Já existe um registro com estes dados. Verifique campos únicos como email, CPF ou CNPJ."
  - Foreign key: "Não é possível realizar esta operação pois existem registros relacionados."
  - Not-null: "Campo obrigatório não foi preenchido."
- **Exemplo**: Deletar vendedor que tem imóveis cadastrados

#### `BusinessException` (422 UNPROCESSABLE ENTITY) - CUSTOMIZADA
- **Quando ocorre**: Regra de negócio violada
- **Mensagem**: Mensagem customizada da regra de negócio
- **Exemplo**: "Vendedor deve ser aprovado antes de anunciar imóveis"

---

### 5. Exceções de Upload de Arquivos

#### `MaxUploadSizeExceededException` (413 PAYLOAD TOO LARGE)
- **Quando ocorre**: Arquivo maior que o limite permitido
- **Mensagem**: "Arquivo muito grande. O tamanho máximo permitido é de 10MB."
- **Exemplo**: Tentar fazer upload de imagem de 15MB

#### `FileStorageException` (500 INTERNAL SERVER ERROR) - CUSTOMIZADA
- **Quando ocorre**: Erro ao salvar arquivo no disco
- **Mensagem**: Mensagem específica do erro (ou "Erro ao processar arquivo")
- **Exemplo**: Erro de permissão, disco cheio

#### `IOException` (500 INTERNAL SERVER ERROR)
- **Quando ocorre**: Erro de I/O genérico
- **Mensagem**: "Erro ao processar arquivo ou operação de entrada/saída. Tente novamente."
- **Exemplo**: Erro ao ler arquivo, erro ao escrever no disco

---

### 6. Exceções de Método HTTP

#### `HttpRequestMethodNotSupportedException` (405 METHOD NOT ALLOWED)
- **Quando ocorre**: Método HTTP incorreto para a rota
- **Mensagem**: "Método HTTP 'POST' não é suportado para esta rota. Métodos permitidos: GET, PUT"
- **Exemplo**: Fazer POST em rota que só aceita GET

---

### 7. Exceções Genéricas

#### `RuntimeException` (500 INTERNAL SERVER ERROR)
- **Quando ocorre**: Erro em tempo de execução não categorizado
- **Mensagem**: Usa a mensagem do erro se disponível, senão "Erro ao processar solicitação"
- **Exemplo**: Erros de serviços que lançam RuntimeException genérica

#### `Exception` (500 INTERNAL SERVER ERROR)
- **Quando ocorre**: Qualquer erro não tratado pelos handlers acima
- **Mensagem**: "Erro interno do servidor. Por favor, tente novamente mais tarde."
- **Exemplo**: Erros inesperados, NPE, etc.

---

## Frontend - Interceptor Axios

### Tratamento de Erros HTTP

1. **401 Unauthorized**
   - Se for tentativa de login/cadastro: Mantém na página e exibe erro
   - Se for rota protegida: Redireciona para `/login` e limpa credenciais
   - Evita redirect loops

2. **Outros Status Codes**
   - Propaga erro para o componente (via catch)
   - Componente decide como exibir (toast, box de erro, etc)

### Logs de Debug

Todos os requests e responses são logados no console:
- URL completa, método HTTP, dados enviados
- Status code, dados recebidos
- Mensagens de erro detalhadas

---

## Exceções Customizadas Criadas

### 1. `ResourceNotFoundException`
```java
// Uso simples
throw new ResourceNotFoundException("Imóvel não encontrado");

// Uso com campos
throw new ResourceNotFoundException("Imóvel", "id", imovelId);
// Mensagem: "Imóvel não encontrado(a) com id: abc-123"
```

### 2. `BusinessException`
```java
throw new BusinessException("Vendedor deve estar aprovado para anunciar imóveis");
```

### 3. `FileStorageException` (já existia)
```java
throw new FileStorageException("Erro ao salvar arquivo: imagem.jpg");
```

---

## Configurações Adicionadas

### application.properties
```properties
# Lança exceção quando rota não é encontrada (em vez de 404 padrão)
spring.mvc.throw-exception-if-no-handler-found=true
spring.web.resources.add-mappings=false

# Inclui mensagens detalhadas de erro nas respostas
server.error.include-message=always
server.error.include-binding-errors=always
server.error.include-stacktrace=on_param
server.error.include-exception=false

# Tamanho máximo de upload
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=20MB
```

---

## Benefícios

### 1. **Para o Usuário**
✅ Mensagens claras e em português  
✅ Sabe exatamente o que deu errado  
✅ Instruções de como resolver (quando aplicável)  
✅ Sem mensagens técnicas confusas  

### 2. **Para o Desenvolvedor**
✅ Logs detalhados no console  
✅ Stack trace disponível com parâmetro `?trace=true`  
✅ Exceções bem categorizadas  
✅ Fácil adicionar novos handlers  

### 3. **Para a Aplicação**
✅ Não quebra em tempo de execução  
✅ Respostas HTTP semânticas (status codes corretos)  
✅ Frontend recebe estrutura de erro consistente  
✅ Fácil debugging e monitoramento  

---

## Estrutura da Resposta de Erro

### Erro Simples
```json
{
  "status": 404,
  "message": "Imóvel não encontrado com id: abc-123",
  "timestamp": "2026-02-06T17:45:00"
}
```

### Erro de Validação
```json
{
  "status": 400,
  "message": "Erro de validação nos dados enviados",
  "errors": {
    "email": "Email inválido",
    "senha": "Senha deve ter no mínimo 6 caracteres",
    "nome": "Nome é obrigatório"
  },
  "timestamp": "2026-02-06T17:45:00"
}
```

---

## Próximos Passos Recomendados

1. **Monitoramento**: Integrar com Sentry ou similar para tracking de exceções em produção
2. **Testes**: Criar testes unitários para cada exception handler
3. **I18n**: Adicionar internacionalização nas mensagens de erro
4. **Rate Limiting**: Adicionar handler para exceções de rate limit
5. **Circuit Breaker**: Adicionar handlers para falhas de serviços externos (ReceitaWS, etc)

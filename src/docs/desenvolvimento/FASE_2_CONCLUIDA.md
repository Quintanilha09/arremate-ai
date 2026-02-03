# Fase 2 - Autenticação e Autorização - CONCLUÍDA ✅

## Resumo

Sistema completo de autenticação JWT e autorização por roles implementado com Spring Security.

## Data de Conclusão

02/02/2026

## Componentes Implementados

### 2.1 - Usuários e Autenticação

#### Entidades e Enums
- **TipoUsuario** (enum): COMPRADOR, VENDEDOR, ADMIN
- **Usuario** (entity):
  - Implementa `UserDetails` para integração com Spring Security
  - Campos: id (UUID), nome, email, senha (BCrypt), telefone, cpf, tipo, ativo, timestamps
  - Método `getAuthorities()` retorna role com prefixo `ROLE_`

#### Banco de Dados
- **Migration V006**: Criação da tabela `usuarios`
  - Constraint UNIQUE em email
  - Índices: email, tipo, ativo
  - Usuário admin pré-criado:
    - Email: admin@arremateai.com
    - Senha: admin123 (BCrypt hash)
    - Tipo: ADMIN

- **Migration V007**: Atualização da tabela `favoritos`
  - Substituição de `usuario_id` (String) por relacionamento ManyToOne com Usuario
  - Foreign key com CASCADE DELETE
  - Índice para performance

#### Repositórios
- **UsuarioRepository**:
  - `findByEmail()` - Busca por email (usado na autenticação)
  - `existsByEmail()` - Validação de email único
  - `existsByCpf()` - Validação de CPF único
  - `findByTipo()` - Listagem por tipo de usuário
  - `countByTipo()` - Contagem por tipo

#### DTOs
- **UsuarioRequest**: Cadastro/atualização com validações (@NotBlank, @Email, @Size, @Pattern)
- **UsuarioResponse**: Resposta sem senha (id, nome, email, telefone, cpf, tipo, ativo, createdAt)
- **LoginRequest**: Email e senha para autenticação
- **AuthResponse**: Token JWT, tipo "Bearer", e dados do usuário

#### Services
- **UsuarioService**:
  - Implementa `UserDetailsService` para Spring Security
  - `loadUserByUsername()` - Carrega usuário por email
  - `cadastrar()` - Criptografa senha com BCrypt, valida email/CPF únicos
  - `buscarPorId()` / `buscarPorEmail()` - Consultas
  - `atualizar()` - Atualiza dados, re-criptografa senha se alterada
  - `desativar()` - Soft delete (ativo = false)
  - `converterParaResponse()` - Converte entidade para DTO (público)

### 2.2 - JWT (JSON Web Tokens)

#### Dependências (pom.xml)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
```

#### Configuração (application.properties)
```properties
# JWT Configuration
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000
```

#### JwtService
- **Algoritmo**: HS256 (HMAC with SHA-256)
- **Expiração**: 24 horas (86.400.000 ms)
- **Secret Key**: Base64 encoded (configurável via properties)
- **Métodos**:
  - `generateToken(UserDetails)` - Gera token JWT
  - `extractUsername(token)` - Extrai email (subject) do token
  - `isTokenValid(token, UserDetails)` - Valida token e verifica expiração
  - `extractClaim(token, resolver)` - Extração genérica de claims

#### JwtAuthenticationFilter
- Estende `OncePerRequestFilter`
- Intercepta todas as requisições
- Extrai token do header `Authorization: Bearer <token>`
- Valida token usando `JwtService`
- Carrega usuário usando `UserDetailsService`
- Seta `Authentication` no `SecurityContext`

#### SecurityConfig
- **@Configuration @EnableWebSecurity @EnableMethodSecurity**
- **CSRF**: Desabilitado (API REST stateless)
- **CORS**: Configurado para localhost:3000 e localhost:4200
- **Session Management**: STATELESS (sem sessão HTTP)
- **Endpoints Públicos**:
  - `/api/auth/**` - Registro, login, logout, refresh
  - `GET /api/imoveis/**` - Listagem pública de imóveis
  - `GET /api/produtos/**` - Listagem pública de produtos
  - `/api/health` - Health check
  - `/uploads/**` - Arquivos estáticos
- **Endpoints Protegidos**: Todos os demais requerem autenticação
- **Beans**:
  - `SecurityFilterChain` - Configuração do filtro de segurança
  - `AuthenticationManager` - Gerenciador de autenticação
  - `AuthenticationProvider` - DaoAuthenticationProvider com BCrypt
  - `PasswordEncoder` - BCryptPasswordEncoder
  - `CorsConfigurationSource` - Configuração de CORS

#### AuthController
- **POST /api/auth/register**:
  - Cadastra novo usuário
  - Gera token JWT automaticamente
  - Retorna `AuthResponse` com token e dados do usuário
  
- **POST /api/auth/login**:
  - Autentica usuário (email + senha)
  - Usa `AuthenticationManager` para validar credenciais
  - Gera token JWT
  - Retorna `AuthResponse` com token e dados do usuário

- **GET /api/auth/me**:
  - Retorna dados do usuário autenticado
  - Extrai `Usuario` do `SecurityContext`
  - Requer token JWT válido

- **POST /api/auth/logout**:
  - Endpoint stateless (logout no client-side)
  - Remove token do localStorage no frontend
  - Retorna 204 No Content

- **POST /api/auth/refresh**:
  - Renova token JWT
  - Extrai usuário autenticado do `SecurityContext`
  - Gera novo token
  - Retorna `AuthResponse` com novo token

### 2.3 - Autorização por Roles

#### ImovelController
- **POST /api/imoveis**: `@PreAuthorize("hasRole('ADMIN')")`
- **PUT /api/imoveis/{id}**: `@PreAuthorize("hasRole('ADMIN')")`
- **PATCH /api/imoveis/{id}**: `@PreAuthorize("hasRole('ADMIN')")`
- **DELETE /api/imoveis/{id}**: `@PreAuthorize("hasRole('ADMIN')")`
- **GET** endpoints: Públicos (sem autenticação)

#### FavoritoController
- **Todos os endpoints**: `@PreAuthorize("isAuthenticated()")` (nível de classe)
- Migração de `X-Usuario-Id` header para `Authentication authentication`
- Extração de usuário: `(Usuario) authentication.getPrincipal()`
- Endpoints:
  - POST /{imovelId}
  - DELETE /{imovelId}
  - GET / (lista)
  - GET /{imovelId}/status
  - GET /count

#### Favorito (Entidade)
- **Antes**: `String usuarioId` (temporário)
- **Depois**: `@ManyToOne Usuario usuario`
- Migration V007 criada para atualizar banco de dados

#### FavoritoRepository
- **Antes**: `findByUsuarioIdOrderByCreatedAtDesc(String)`
- **Depois**: `findByUsuarioOrderByCreatedAtDesc(Usuario)`
- Todos os métodos atualizados para usar objeto `Usuario`

#### FavoritoService
- **Antes**: Recebia `String usuarioId`
- **Depois**: Recebe `Usuario usuario`
- Logs atualizados para usar `usuario.getId()`
- Validações mantidas (imóvel existe, não está favoritado)

## Testes Manuais Sugeridos

### 1. Registro de Usuário
```bash
POST /api/auth/register
{
  "nome": "João Silva",
  "email": "joao@example.com",
  "senha": "senha123",
  "telefone": "11999999999",
  "cpf": "12345678901",
  "tipo": "COMPRADOR"
}
```

### 2. Login
```bash
POST /api/auth/login
{
  "email": "joao@example.com",
  "senha": "senha123"
}
```
**Resposta**: `{ "token": "eyJhbGc...", "tipo": "Bearer", "usuario": {...} }`

### 3. Acesso a Endpoint Protegido
```bash
GET /api/favoritos
Authorization: Bearer eyJhbGc...
```

### 4. Criar Imóvel (requer ADMIN)
```bash
POST /api/imoveis
Authorization: Bearer <token-admin>
```

### 5. Adicionar Favorito
```bash
POST /api/favoritos/{imovelId}
Authorization: Bearer <token-usuario>
```

## Compilação

```bash
mvn clean compile
```

**Resultado**: ✅ BUILD SUCCESS

## Próximos Passos

- [ ] Criar collection Postman para Fase 2
- [ ] Implementar refresh token com blacklist (Redis)
- [ ] Adicionar validação de ownership (vendedor só edita seus imóveis)
- [ ] Implementar rate limiting
- [ ] Adicionar logs de auditoria (login, logout, falhas)
- [ ] Testes unitários para SecurityConfig
- [ ] Testes de integração para fluxo de autenticação

## Observações Técnicas

- **BCrypt Rounds**: Padrão (10 rounds)
- **Token Expiration**: 24 horas (configurável)
- **CORS**: Configurado para desenvolvimento (ajustar em produção)
- **Secret Key**: Deve ser alterada em produção e armazenada em variável de ambiente
- **Logout**: Implementação stateless (client-side). Para blacklist de tokens, usar Redis
- **Password Encoder**: BCryptPasswordEncoder com salt automático

## Segurança

✅ Senhas criptografadas com BCrypt  
✅ Tokens JWT assinados com HS256  
✅ CSRF desabilitado (API REST)  
✅ CORS configurado  
✅ Autorização por roles implementada  
✅ Endpoints públicos/protegidos configurados  
✅ Validação de tokens em todas as requisições autenticadas  
✅ Soft delete para usuários (desativar ao invés de deletar)  
✅ Unique constraints (email, CPF)  

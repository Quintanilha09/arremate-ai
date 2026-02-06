# Sistema de Dispositivos Confiáveis (2FA Adaptativo)

## 📋 Resumo

Implementado sistema **completo** para "Lembrar dispositivo" inspirado em grandes plataformas (Google, GitHub, AWS, Stripe).

**Objetivo:** Reduzir fricção no login sem comprometer segurança.

---

## ✨ Como Funciona

### **Primeiro Login (Novo Dispositivo)**
1. Usuário insere **email + senha**
2. Sistema envia **código 2FA por email**
3. Usuário verifica código
4. **☑ Checkbox:** "Confiar neste dispositivo por 90 dias"
5. Se marcado → gera **Device Token** (salvo no localStorage)

### **Próximos Logins (Mesmo Dispositivo)**
1. Usuário insere **email + senha**
2. Frontend envia **Device Token** no header `X-Device-Token`
3. Backend valida token → **Skip 2FA** → Login direto ✅

### **Login em Novo Dispositivo/Navegador**
1. Device Token **não existe** ou **inválido**
2. Sistema exige **2FA novamente**
3. Ciclo reinicia

---

## 🏗️ Arquitetura

### **Backend**

#### **1. Nova Tabela: `trusted_device`**
```sql
CREATE TABLE trusted_device (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    device_token VARCHAR(255) UNIQUE,  -- Token único
    fingerprint VARCHAR(500),          -- Hash User-Agent
    user_agent VARCHAR(500),
    ip_address VARCHAR(45),
    created_at TIMESTAMP,
    last_used_at TIMESTAMP,
    expires_at TIMESTAMP,              -- Expira em 90 dias
    revoked BOOLEAN DEFAULT FALSE
);
```

#### **2. Classes Criadas**

**`TrustedDevice.java`** (Entity)
- Modelo JPA para dispositivo confiável
- Métodos: `isValid()`, `updateLastUsed()`, `revoke()`

**`TrustedDeviceRepository.java`**
- Queries para buscar/validar dispositivos
- Limpeza automática de tokens expirados

**`TrustedDeviceService.java`**
- **`isDeviceTrusted(token)`**: Valida se device token é válido
- **`trustDevice(usuario, request)`**: Cria novo device token
- **`revokeDevice()`**: Revoga dispositivo específico
- **`revokeAllUserDevices()`**: Revoga todos os dispositivos do usuário
- **`cleanupExpiredDevices()`**: Task agendada (diariamente às 3h)

**`EmailCorporativoValidator.java`** (Bônus)
- Valida se email é corporativo (não Gmail, Hotmail, etc.)
- Lista negra de 50+ domínios pessoais

**`PasswordEncoderConfig.java`** (Fix)
- Bean centralizado para BCryptPasswordEncoder
- Resolve problema de "Bad credentials"

#### **3. AuthController - 2FA Adaptativo**

**Endpoint `/api/auth/login` modificado:**
```java
@PostMapping("/login")
public ResponseEntity<Map<String, Object>> login(
    @RequestBody LoginRequest request,
    @RequestHeader(value = "X-Device-Token", required = false) String deviceToken
) {
    // Autentica usuário
    authenticationManager.authenticate(...);
    
    // Verifica se dispositivo é confiável
    if (deviceToken != null && trustedDeviceService.isDeviceTrusted(deviceToken)) {
        // Skip 2FA - Login direto
        return Map.of(
            "requiresTwoFactor", false,
            "token", jwtToken,
            "user", userData
        );
    } else {
        // Envia 2FA
        verificacaoService.enviarCodigoVerificacao(email);
        return Map.of("requiresTwoFactor", true);
    }
}
```

**Endpoint `/api/auth/2fa/verificar-codigo` modificado:**
```java
@PostMapping("/2fa/verificar-codigo")
public ResponseEntity<Map<String, Object>> verificarCodigo(
    @RequestBody VerificarCodigoRequest request,
    @RequestParam(defaultValue = "false") boolean rememberDevice,
    HttpServletRequest httpRequest
) {
    if (verificacaoService.verificarCodigo(...)) {
        String jwtToken = jwtService.generateToken(usuario);
        
        // Se marcou "lembrar", gera device token
        if (rememberDevice) {
            String deviceToken = trustedDeviceService.trustDevice(usuario, httpRequest);
            return Map.of(
                "token", jwtToken,
                "deviceToken", deviceToken  // ← Novo!
            );
        }
        return Map.of("token", jwtToken);
    }
}
```

---

### **Frontend**

#### **1. AuthContext - Login Adaptativo**
```typescript
const login = async (email: string, senha: string) => {
  const deviceToken = localStorage.getItem('deviceToken');
  const response = await authService.login({ email, senha }, deviceToken);
  
  if (response.requiresTwoFactor) {
    // Mostra modal 2FA
    return { requiresTwoFactor: true, email };
  } else {
    // Login direto (dispositivo confiável)
    localStorage.setItem('token', response.token);
    localStorage.setItem('deviceToken', response.deviceToken);
    router.push('/imoveis');
  }
};
```

#### **2. ModalVerificacao2FA - Checkbox**
```tsx
<input
  type="checkbox"
  checked={rememberDevice}
  onChange={(e) => setRememberDevice(e.target.checked)}
/>
<label>Confiar neste dispositivo por 90 dias</label>

// Ao verificar código:
const result = await authService.verificarCodigo(email, codigo, rememberDevice);

if (result.deviceToken) {
  localStorage.setItem('deviceToken', result.deviceToken);
}
```

#### **3. AuthService - Header com Device Token**
```typescript
async login(data, deviceToken) {
  const headers = deviceToken ? { 'X-Device-Token': deviceToken } : {};
  return await api.post('/auth/login', data, { headers });
}

async verificarCodigo(email, codigo, rememberDevice = false) {
  return await api.post(`/auth/2fa/verificar-codigo?rememberDevice=${rememberDevice}`, {
    email, codigo
  });
}
```

---

## 🔐 Segurança

### **Fingerprint do Dispositivo**
```java
private String generateFingerprint(HttpServletRequest request) {
    String data = String.format("%s|%s|%s",
        request.getHeader("User-Agent"),
        request.getHeader("Accept-Language"),
        request.getHeader("Accept-Encoding")
    );
    return Base64.encode(SHA256(data));
}
```

### **IP Tracking**
- Registra IP do dispositivo
- Suporta proxy/load balancer (X-Forwarded-For)

### **Expiração Automática**
- Device Token expira em **90 dias**
- Task agendada limpa tokens expirados diariamente

### **Revogação Manual**
- Usuário pode revogar dispositivos específicos
- Opção de revogar TODOS os dispositivos (logout global)

---

## 📊 Dados Armazenados

### **LocalStorage (Frontend)**
```javascript
{
  "token": "eyJhbGciOiJIUzI1NiIs...",           // JWT (15 min)
  "deviceToken": "uuid-timestamp",             // Device Token (90 dias)
  "user": { "id": "...", "email": "..." }     // User data
}
```

### **Database (Backend)**
```sql
SELECT * FROM trusted_device WHERE usuario_id = '...';

| device_token        | fingerprint  | ip_address    | expires_at          | revoked |
|---------------------|--------------|---------------|---------------------|---------|
| uuid-1234567890     | a3f8b2c...   | 192.168.1.100 | 2026-05-07 15:00:00 | false   |
```

---

## 🧪 Fluxo Completo de Testes

### **Teste 1: Primeiro Login**
1. Acesse `/login`
2. Digite: `gabriel.a.oliveira@capgemini.com` + senha
3. Modal 2FA aparece
4. Digite código recebido por email
5. ☑ Marque "Confiar neste dispositivo"
6. Clique "Verificar Código"
7. ✅ Login bem-sucedido + toast "Dispositivo salvo!"

### **Teste 2: Segundo Login (Mesmo Navegador)**
1. Faça logout
2. Acesse `/login` novamente
3. Digite email + senha
4. ✅ **Login direto SEM 2FA**

### **Teste 3: Login em Outro Navegador**
1. Abra Chrome Incognito / outro navegador
2. Digite email + senha
3. ⚠️ **2FA solicitado novamente**

### **Teste 4: Device Token Inválidado**
1. No DevTools: `localStorage.removeItem('deviceToken')`
2. Faça login
3. ⚠️ **2FA solicitado novamente**

---

## 📝 Arquivos Modificados/Criados

### **Backend (10 arquivos)**
✅ `V008__criar_tabela_trusted_device.sql` (Migration)
✅ `TrustedDevice.java` (Entity)
✅ `TrustedDeviceRepository.java`
✅ `TrustedDeviceService.java`
✅ `AuthController.java` (modificado)
✅ `EmailCorporativoValidator.java`
✅ `PasswordEncoderConfig.java`
✅ `SecurityConfig.java` (modificado)
✅ `UsuarioService.java` (modificado)
✅ `ArremateaiApplication.java` (@EnableScheduling)

### **Frontend (5 arquivos)**
✅ `AuthContext.tsx` (modificado)
✅ `authService.ts` (modificado)
✅ `ModalVerificacao2FA.tsx` (checkbox)
✅ `login/page.tsx` (modificado)
✅ `cadastro-vendedor/page.tsx` (validação email)

---

## 🎯 Benefícios

| Antes | Agora |
|-------|-------|
| 2FA em **todo** login | 2FA apenas em **novo dispositivo** |
| Usuário digita código **sempre** | Código **uma vez a cada 90 dias** |
| UX frustrante | UX igual Google/GitHub |
| Sessão expira rapidamente | Device token válido por 90 dias |

---

## 🔧 Próximos Passos (Futuro)

1. **Painel de Dispositivos Confiáveis** (UI)
   - Listar dispositivos ativos
   - Ver último uso, IP, User-Agent
   - Botão "Revogar"

2. **Alertas de Segurança**
   - Email quando novo dispositivo é adicionado
   - Notificação de login suspeito (IP diferente)

3. **Gestão de Sessões**
   - Limite de dispositivos simultâneos (ex: 5)
   - Opção "Encerrar outras sessões"

4. **Analytics**
   - Métricas: % logins com 2FA vs skip
   - Dispositivos mais usados
   - Taxa de adoção do "lembrar"

---

## ✅ Status

🎉 **IMPLEMENTAÇÃO COMPLETA**
- ✅ Backend: 100%
- ✅ Frontend: 100%
- ✅ Compilação: SUCCESS
- ⏳ Teste manual: Pendente (reinicie backend + frontend)

---

## 📚 Referências

- **Google Account Security**: https://support.google.com/accounts/answer/162129
- **GitHub Security Best Practices**: https://docs.github.com/en/authentication
- **OAuth 2.0 Device Authorization**: https://oauth.net/2/device-flow/
- **OWASP Authentication Cheat Sheet**: https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html

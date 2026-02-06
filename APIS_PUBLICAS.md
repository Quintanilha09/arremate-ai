# APIs Públicas Utilizadas

## 1. ViaCEP
- **URL:** `https://viacep.com.br/ws/{cep}/json/`
- **Motivo:** Buscar endereço completo a partir do CEP ao cadastrar imóveis
- **Localização:** `arremateai-front/services/cepService.ts`

## 2. Google OAuth2
- **URLs:** 
  - `https://oauth2.googleapis.com/token`
  - `https://www.googleapis.com/oauth2/v2/userinfo`
- **Motivo:** Autenticação via Google (login social)
- **Localização:** `arremateai/src/main/java/.../OAuth2Service.java`

## 3. Brasil API (Opcional)
- **URL:** `https://brasilapi.com.br/api/banks/v1`
- **Motivo:** Buscar nomes reais de bancos para tornar dados MOCKADOS de leilões mais realistas
- **Localização:** `arremateai/src/main/java/.../LeilaoPublicoClient.java`
- **Observação:** Se falhar, usa nomes hardcoded. NÃO busca leilões reais.

---

**Total:** 3 APIs públicas gratuitas, sem necessidade de pagamento.

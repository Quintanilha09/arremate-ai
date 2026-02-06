# Análise: Validação de CNPJ e Aprovação de Vendedores

## APIs Disponíveis para Validação de CNPJ

### 1. ReceitaWS (Recomendada para MVP)
- **URL**: `https://www.receitaws.com.br/v1/cnpj/{cnpj}`
- **Custo**: Gratuita com limite de 3 requisições/minuto
- **Dados retornados**: Razão social, nome fantasia, situação cadastral, atividade principal, endereço completo
- **Vantagens**: Simples, sem necessidade de credenciais
- **Desvantagens**: Rate limit baixo, não é API oficial

### 2. Brasil API
- **URL**: `https://brasilapi.com.br/api/cnpj/v1/{cnpj}`
- **Custo**: Gratuita
- **Dados retornados**: Similar ao ReceitaWS
- **Vantagens**: Open source, mantida pela comunidade
- **Desvantagens**: Depende de scraping da Receita Federal

### 3. Serpro (Oficial - Governo Federal)
- **URL**: `https://gateway.apiserpro.serpro.gov.br/consulta-cnpj/v2`
- **Custo**: 
  - Grátis até 500 consultas/mês
  - R$ 0,07 por consulta acima de 500
- **Vantagens**: Dados oficiais, atualizados em tempo real
- **Desvantagens**: Requer credenciamento gov.br, processo burocrático

### 4. API da Receita Federal (Não existe API pública)
- Não há API REST oficial
- Apenas consulta via site (captcha)

## Recomendação

### Fase 1 (MVP - Implementar agora):
**Validação Híbrida: ReceitaWS + Aprovação Manual**

1. **Automática no cadastro**:
   - Validar CNPJ via ReceitaWS
   - Verificar se CNPJ está ativo
   - Pré-preencher dados da empresa
   - Status inicial: `PENDENTE_APROVACAO`

2. **Manual pelo Admin**:
   - Painel admin lista vendedores pendentes
   - Admin revisa dados e aprova/rejeita
   - E-mail enviado ao vendedor com resultado

3. **Notificações**:
   - Admin recebe e-mail quando vendedor se cadastra
   - Vendedor recebe e-mail quando é aprovado/rejeitado

### Fase 2 (Futuro - Produção):

#### 1. Serpro API (Oficial - Governo)
- **Custo**: R$ 0,07 por consulta acima de 500/mês
- **Estimativa**: 100 vendedores/mês = R$ 0,00 (grátis até 500)
- **Vantagem**: Dados oficiais da Receita Federal

#### 2. Upload de Documentos (Armazenamento)
- **AWS S3**: 
  - Primeiros 5GB: R$ 0,11/GB/mês
  - Transfer OUT: R$ 0,39/GB
  - **Estimativa**: 1000 documentos (50MB cada) = 50GB = ~R$ 5,50/mês
- **Cloudflare R2** (alternativa mais barata):
  - 10GB grátis/mês
  - Acima: R$ 0,08/GB
  - **Estimativa**: 50GB = R$ 3,20/mês

#### 3. Validação de Documentos via IA/OCR
- **Google Cloud Vision API**:
  - Primeiros 1.000 documentos/mês: GRÁTIS
  - 1.001 - 5.000.000: US$ 1,50 por 1.000 (~R$ 7,50)
  - **Estimativa**: 100 vendedores = R$ 0,00 (dentro do free tier)
  
- **AWS Textract**:
  - Primeiras 1.000 páginas/mês: GRÁTIS
  - Acima: US$ 1,50 por 1.000 páginas (~R$ 7,50)
  - **Estimativa**: 100 vendedores (2 docs cada) = 200 páginas = R$ 0,00

- **Azure Form Recognizer**:
  - Primeiras 500 páginas/mês: GRÁTIS
  - Acima: US$ 10 por 1.000 páginas (~R$ 50,00)
  - **Estimativa**: 100 vendedores = R$ 0,00

#### 4. Serasa Score de Crédito
- **Serasa Experian API**:
  - Consulta PJ básica: R$ 5,00 - R$ 15,00 por consulta
  - Score completo: R$ 25,00 - R$ 40,00 por consulta
  - **Estimativa**: 100 vendedores/mês x R$ 10,00 = **R$ 1.000,00/mês** ❌ ALTO CUSTO
  
- **Alternativa - Boa Vista SCPC**:
  - Consulta básica: R$ 3,50 - R$ 8,00
  - **Estimativa**: 100 vendedores x R$ 5,00 = **R$ 500,00/mês** ⚠️ CUSTO MÉDIO

- **Recomendação**: Fazer consulta Serasa **apenas para vendedores grandes** (CNPJ com faturamento > R$ 1M) ou vendedores com histórico de problemas

### 💰 Resumo de Custos - Fase 2

| Item | Custo Mensal (100 vendedores) |
|------|------------------------------|
| Serpro API | R$ 0,00 (free tier) |
| Storage AWS S3 | R$ 5,50 |
| OCR Google Vision | R$ 0,00 (free tier) |
| Serasa (OPCIONAL) | R$ 500,00 - R$ 1.000,00 ⚠️ |
| **Total SEM Serasa** | **R$ 5,50/mês** ✅ |
| **Total COM Serasa** | **R$ 505,50 - R$ 1.005,50/mês** ⚠️ |

**Conclusão**: A Fase 2 é viável com **custo baixo (R$ 5-6/mês)** se não usar Serasa. A consulta de crédito deve ser implementada apenas se houver forte justificativa de negócio (ex: marketplace com garantia de pagamento).

## Implementação Proposta

### Backend (Java/Spring):
1. `CnpjValidationService` - Integração com ReceitaWS
2. `AdminNotificationService` - Envio de e-mails para admin
3. `AdminController` - Endpoints para aprovar/rejeitar vendedores
4. Atualizar `VendedorService` para chamar validação de CNPJ

### Frontend (Next.js):
1. `/admin/vendedores` - Listagem de vendedores pendentes
2. Modal de aprovação/rejeição com histórico
3. Dashboard com estatísticas de aprovação

### Fluxo Completo:
```
Vendedor cadastra → Valida CNPJ (ReceitaWS) → Status: PENDENTE_APROVACAO
    ↓
E-mail enviado para admin@arremateai.com
    ↓
Admin acessa /admin/vendedores → Revisa cadastro → Aprova/Rejeita
    ↓
E-mail enviado ao vendedor → Status: APROVADO ou REJEITADO
    ↓
Vendedor APROVADO pode anunciar imóveis
```

## Custos Estimados

| Solução | Custo Mensal | Limite |
|---------|-------------|--------|
| ReceitaWS | R$ 0,00 | 3 req/min |
| Brasil API | R$ 0,00 | Sem limite oficial |
| Serpro | R$ 0,00 - R$ 35,00 | 500 grátis + R$ 0,07/extra |

**Para MVP com até 100 vendedores/mês**: ReceitaWS é suficiente e gratuito.

# Listagem de APIs Públicas e Plataformas de Integração - QuinBid

Este documento mapeia as principais leiloeiras e plataformas brasileiras que disponibilizam APIs ou infraestrutura de integração para acesso a dados de leilões.

---

## 1. Superbid Exchange (Infraestrutura ITF-sbX)

### Descrição
A Superbid é a plataforma tecnologicamente mais aberta para parcerias B2B no mercado brasileiro. Utiliza a **ITF-sbX (Infraestrutura Tecnológica Financeira Superbid Exchange)**, desenvolvida especificamente para conectar sistemas e automatizar fluxos de negociação.

### O que oferece
- Módulos de integração para agentes de negócios e parceiros
- Gestão de transações em tempo real
- Acesso a dados de lotes via Terminal Gestor
- Credenciamento como "Agente de Negócios" para parceiros tecnológicos

### Foco de Atuação
- Leilões industriais
- Frotas e veículos
- Leilões judiciais
- Bens de consumo (incluindo eletrônicos)

### Modelo de Acesso
- Requer credenciamento como parceiro B2B
- Infraestrutura robusta para integração empresarial

### Prioridade para MVP
**ALTA** - Maior leiloeira do Brasil, infraestrutura consolidada para parceiros

---

## 2. Mega Leilões (Ecossistema Mega Cloud)

### Descrição
A Mega Leilões possui uma estrutura de APIs documentada para integração com seus sistemas de gestão e portais de vendas, baseada em ambiente Cloud.

### O que oferece
- Endpoints REST e SOAP para diversas operações
- Sistema "Alexandria" para consulta de dados
- Integração com módulos ERP
- Integração de propostas e consulta de lotes
- Documentação técnica voltada para desenvolvedores

### Foco de Atuação
- Diversas categorias (veículos, imóveis, móveis, etc.)
- Leilões judiciais e extrajudiciais

### Modelo de Acesso
- API REST/SOAP documentada
- Possível necessidade de credenciamento

### Prioridade para MVP
**MÉDIA** - Boa documentação técnica, mas menor volume que Superbid

---

## 3. PNCP (Portal Nacional de Contratações Públicas)

### Descrição
Fonte de dados **mais aberta e amigável para desenvolvedores** no Brasil, operando sob a nova Lei de Licitações (14.133/2021).

### O que oferece
- **API Pública REST/JSON completa**
- Documentação Swagger disponível
- Consulta de editais de leilões eletrônicos
- Itens de contratação e alienação de bens móveis
- Acesso público sem necessidade de credenciamento inicial

### Foco de Atuação
- Leilões governamentais
- Órgãos públicos federais, estaduais e municipais
- Alienação de bens públicos

### Modelo de Acesso
- **API totalmente pública**
- Documentação: [PNCP API Docs](https://pncp.gov.br/api)

### Prioridade para MVP
**MUITO ALTA** - API pública, documentada, sem barreiras de entrada. **Ideal para início do projeto.**

---

## 4. SELIC Conecta (Banco Central)

### Descrição
Portal oficial do Banco Central para dados de leilões de títulos e resultados públicos do sistema financeiro.

### O que oferece
- APIs REST para resultados de leilões
- Ambientes de homologação e produção
- Dados públicos em formato JSON ou XML
- Resultados oficiais em tempo real

### Foco de Atuação
- Leilões de títulos públicos
- Resultados oficiais de leilões financeiros
- Dados do mercado financeiro

### Modelo de Acesso
- API pública do Banco Central
- Documentação oficial disponível

### Prioridade para MVP
**BAIXA** - Foco em leilões financeiros, fora do escopo inicial de bens físicos

---

## 5. APIs de Scraping - Apify (Bancos)

### Descrição
Como grandes bancos (Caixa e Santander) raramente abrem APIs para o público geral, a plataforma **Apify** comercializa APIs "prontas" que realizam scraping e entregam dados estruturados.

### O que oferece

#### Caixa Leilões API
- JSON com valor de avaliação
- Descontos aplicados
- Datas de leilão
- Links para editais

#### Santander Imóveis API
- Dados estruturados de ativos bancários
- Informações de imóveis retomados

### Foco de Atuação
- Ativos retomados por instituições financeiras
- Imóveis em leilão bancário
- Veículos retomados

### Modelo de Acesso
- **Serviço pago** via plataforma Apify
- APIs prontas para consumo
- Alternativa ao scraping próprio

### Prioridade para MVP
**MÉDIA-BAIXA** - Considerar apenas se necessário acesso rápido a bancos sem desenvolver scraping próprio. Avaliar custo-benefício.

---

## 6. Plataformas SaaS White Label (Provedores)

### Descrição
Em vez de integrar com cada leiloeira individualmente, é possível buscar parcerias com **provedores de software** que atendem dezenas de leiloeiros oficiais. Eles possuem APIs padronizadas para seus clientes.

### Principais Provedores

#### Sua Plataforma de Leilão
- Integração via API para escalar operações
- Aumentar visibilidade dos leilões
- Múltiplos clientes (leiloeiras) usando a mesma infraestrutura

#### Suporte Leilões
- ERP com módulos de integração
- Monitoramento de lances em tempo real
- Gestão de bens centralizada

### O que oferece
- **Acesso a múltiplas leiloeiras** através de uma única API
- Padronização de dados entre diferentes leiloeiras
- Redução da complexidade de integração

### Foco de Atuação
- Leiloeiras pequenas e médias que usam estas plataformas
- Diversas categorias de produtos

### Modelo de Acesso
- Parcerias B2B com os provedores
- APIs padronizadas

### Prioridade para MVP
**ALTA** - Estratégia eficiente para escalar rapidamente o número de leiloeiras integradas

---

## Estratégia de Implementação Recomendada

### ⚠️ DECISÃO DE ESCOPO INICIAL

**Foco Exclusivo em Imóveis**: O MVP iniciará apenas com leilões de imóveis, devido à maior confiabilidade e disponibilidade de APIs públicas para esta categoria.

**Justificativa:**
- APIs públicas documentadas e acessíveis (PNCP)
- Dados estruturados e padronizados
- Mercado consolidado e confiável
- Menor complexidade de integração inicial
- Permite validar arquitetura antes de expandir

### Fase 1: MVP - PNCP Imóveis (Mês 1-2)

**Prioridade Máxima:**
1. **PNCP - Imóveis** - API pública para alienação de bens imóveis em leilões governamentais
   - Endpoint inicial: Buscar todos os imóveis e valores
   - Sem autenticação necessária
   - Dados: Valor de avaliação, localização, descrição, data do leilão

**Objetivo:** Validar integração com dados reais de imóveis em leilão público

**Status Atual:**
- [x] Definição de escopo focado em imóveis
- [ ] Endpoint GET para buscar imóveis do PNCP
- [ ] Validação de estrutura de dados
- [ ] Testes de integração

### Fase 2: Expansão (Mês 3-4)

3. **Mega Leilões** - Integrar API REST/SOAP
4. **Plataforma White Label** (1 provedor) - Avaliar parceria com "Sua Plataforma de Leilão" ou "Suporte Leilões"

**Objetivo:** Ampliar cobertura de categorias e volume de produtos

### Fase 3: Bancos e Diversificação (Mês 5-6)

5. **Scraping Próprio** para Caixa e Santander (avaliar Apify como alternativa)
6. **Segunda plataforma White Label** - Ampliar cobertura

**Objetivo:** Incluir grandes bancos e atingir massa crítica de produtos

---

## Requisitos Técnicos por Integração

| Plataforma | Tipo | Autenticação | Formato | Documentação | Credenciamento |
|------------|------|--------------|---------|--------------|----------------|
| PNCP | API REST | Pública | JSON | Swagger | Não |
| Superbid Exchange | API B2B | OAuth/API Key | JSON | Privada | Sim |
| Mega Leilões | REST/SOAP | API Key | JSON/XML | Disponível | Provável |
| SELIC Conecta | API REST | Pública | JSON/XML | Oficial | Não |
| Apify (Bancos) | API Scraping | API Key (pago) | JSON | Sim | Não |
| White Label | API REST | OAuth/API Key | JSON | Parceria | Sim |

---

## Considerações Legais e Técnicas

### Conformidade LGPD
- Dados públicos de leilões geralmente não contêm informações pessoais sensíveis
- Atenção especial a dados de arrematantes/proponentes
- Implementar política de privacidade clara

### Termos de Uso
- ✅ PNCP: Dados públicos, uso livre
- ⚠️ Superbid: Requer parceria formal
- ⚠️ Bancos: Verificar termos (scraping pode violar ToS)
- ⚠️ White Label: Parceria B2B necessária

### Rate Limiting
- Implementar respeito a limites de requisições
- Cache inteligente para reduzir chamadas
- Sincronização agendada (não em tempo real para MVP)

---

## Próximos Passos

### Ações Imediatas
- [ ] Testar API do PNCP e documentar estrutura de dados
- [ ] Entrar em contato com Superbid para processo de credenciamento
- [ ] Pesquisar requisitos técnicos da Mega Leilões
- [ ] Avaliar custo/benefício do Apify para bancos

### Pesquisa Complementar
- [ ] Identificar outras leiloeiras regionais relevantes
- [ ] Mapear leiloeiras sem API para futura implementação de scraping
- [ ] Levantar volume aproximado de leilões/produtos por plataforma
- [ ] Definir SLA de atualização de dados (diário, semanal?)

### Desenvolvimento
- [ ] Criar módulo de abstração para múltiplas APIs
- [ ] Implementar adapter pattern para normalizar dados de diferentes fontes
- [ ] Desenvolver sistema de sincronização e ETL
- [ ] Preparar infraestrutura de cache (Redis)

---

## Recursos e Links Úteis

- **PNCP API**: https://pncp.gov.br/api
- **Superbid**: https://www.superbid.net (contato comercial para ITF-sbX)
- **Mega Leilões**: https://www.megaleiloes.com.br
- **SELIC Conecta**: https://www.bcb.gov.br/acessoinformacao/legado?url=https://www.bcb.gov.br/pom/demab/selic/conecta
- **Apify**: https://apify.com/store
- **Lei 14.133/2021**: http://www.planalto.gov.br/ccivil_03/_ato2019-2022/2021/lei/L14133.htm

---

*Documento criado em: Janeiro/2026*  
*Última atualização: 29/01/2026*

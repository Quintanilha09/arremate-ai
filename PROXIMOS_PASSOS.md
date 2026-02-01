# Próximos Passos - ArremateAI

## Status Atual
✅ 20 imóveis cadastrados no banco de dados  
✅ Endpoint GET /api/imoveis (listar todos)  
✅ Endpoint POST /api/imoveis (cadastrar)  
✅ Integração com Brasil API (nomes de bancos reais)  

---

## 1. Funcionalidades de Busca ⭐ **EM ANDAMENTO**
**Prioridade:** Alta  
**Objetivo:** Permitir filtros e pesquisas avançadas nos imóveis

### Tarefas:
- [ ] Filtrar por UF
- [ ] Filtrar por cidade
- [ ] Filtrar por instituição
- [ ] Filtrar por faixa de valor (min/max)
- [ ] Filtrar por tipo de imóvel
- [ ] Ordenação (valor, data, área)
- [ ] Paginação (Page/Pageable)
- [ ] Busca por texto (descrição)

### Endpoints:
```
GET /api/imoveis?uf=SP&valorMin=300000&valorMax=800000&page=0&size=10
GET /api/imoveis?tipoImovel=Apartamento&cidade=São Paulo
GET /api/imoveis?instituicao=Caixa&sortBy=valorAvaliacao&direction=ASC
```

---

## 2. CRUD Completo
**Prioridade:** Média  
**Objetivo:** Completar operações básicas de gerenciamento

### Tarefas:
- [x] GET (listar todos) - **CONCLUÍDO**
- [x] POST (cadastrar) - **CONCLUÍDO**
- [ ] GET /{id} (buscar por ID)
- [ ] PUT /{id} (atualizar)
- [ ] DELETE /{id} (remover)

---

## 3. Integração com Entidades Existentes
**Prioridade:** Média-Baixa  
**Objetivo:** Consolidar arquitetura do domínio

### Tarefas:
- [ ] Analisar relacionamento Imovel x Leilao
- [ ] Analisar relacionamento Imovel x Leiloeira
- [ ] Migrar/consolidar entidade Produto
- [ ] Definir estratégia de dados (separar ou unificar)
- [ ] Ajustar repositories e services

---

## 4. Documentação e Qualidade
**Prioridade:** Média  
**Objetivo:** Melhorar qualidade e documentação do código

### Tarefas:
- [ ] Configurar Swagger/OpenAPI
- [ ] Adicionar Bean Validation (@NotNull, @Size, etc)
- [ ] Implementar tratamento global de exceções (@ControllerAdvice)
- [ ] Criar testes unitários (Service)
- [ ] Criar testes de integração (Controller)
- [ ] Documentar DTOs e endpoints

---

## 5. Features Avançadas
**Prioridade:** Baixa  
**Objetivo:** Funcionalidades de valor agregado

### Possíveis Features:
- [ ] Sistema de favoritos (usuário salva imóveis)
- [ ] Alertas/notificações de novos leilões
- [ ] Dashboard com estatísticas (total por UF, média de valores)
- [ ] Relatórios em PDF/Excel
- [ ] Histórico de alterações de preços
- [ ] Comparador de imóveis

---

## 6. Frontend
**Prioridade:** Futura  
**Objetivo:** Interface visual para usuários

### Tecnologias Sugeridas:
- React + TypeScript
- Next.js
- Tailwind CSS

### Páginas:
- [ ] Listagem de imóveis com filtros
- [ ] Detalhes do imóvel
- [ ] Cadastro/edição (admin)
- [ ] Dashboard

---

## 7. Infraestrutura e Deploy
**Prioridade:** Futura

### Tarefas:
- [ ] Configurar CI/CD
- [ ] Containerização completa (Docker Compose)
- [ ] Deploy em cloud (AWS/Azure/GCP)
- [ ] Configurar monitoramento
- [ ] Backup automatizado do banco

---

## 8. Segurança e Autenticação
**Prioridade:** Futura

### Tarefas:
- [ ] Implementar Spring Security
- [ ] JWT para autenticação
- [ ] Roles (admin, user)
- [ ] Proteção de endpoints sensíveis (POST, PUT, DELETE)

---

**Última atualização:** 30/01/2026

-- Migration: Adiciona campos updated_at e ativo para soft delete e auditoria
-- Autor: Sistema
-- Data: 2026-02-02

ALTER TABLE imovel
ADD COLUMN updated_at TIMESTAMP,
ADD COLUMN ativo BOOLEAN NOT NULL DEFAULT true;

-- Criar índice para facilitar buscas por imóveis ativos
CREATE INDEX idx_imovel_ativo ON imovel(ativo);

-- Comentários nas colunas
COMMENT ON COLUMN imovel.updated_at IS 'Data e hora da última atualização do registro';
COMMENT ON COLUMN imovel.ativo IS 'Indica se o imóvel está ativo (true) ou foi removido logicamente (false)';

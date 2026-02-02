-- Migration V003: Expandir modelo de dados de Imovel
-- Adiciona campos detalhados (quartos, banheiros, vagas, coordenadas, etc.)

-- Adicionar novos campos à tabela imovel
ALTER TABLE imovel 
    ADD COLUMN IF NOT EXISTS quartos INTEGER,
    ADD COLUMN IF NOT EXISTS banheiros INTEGER,
    ADD COLUMN IF NOT EXISTS vagas INTEGER,
    ADD COLUMN IF NOT EXISTS endereco VARCHAR(500),
    ADD COLUMN IF NOT EXISTS cep VARCHAR(10),
    ADD COLUMN IF NOT EXISTS latitude DECIMAL(10, 8),
    ADD COLUMN IF NOT EXISTS longitude DECIMAL(11, 8),
    ADD COLUMN IF NOT EXISTS condicao VARCHAR(50),
    ADD COLUMN IF NOT EXISTS aceita_financiamento BOOLEAN DEFAULT false,
    ADD COLUMN IF NOT EXISTS observacoes VARCHAR(2000),
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'DISPONIVEL';

-- Criar tabela de imagens
CREATE TABLE IF NOT EXISTS imagem_imovel (
    id BIGSERIAL PRIMARY KEY,
    imovel_id BIGINT NOT NULL,
    url VARCHAR(1000) NOT NULL,
    legenda VARCHAR(500),
    principal BOOLEAN DEFAULT false,
    ordem INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_imagem_imovel FOREIGN KEY (imovel_id) REFERENCES imovel(id) ON DELETE CASCADE
);

-- Criar índices para performance
CREATE INDEX IF NOT EXISTS idx_imagem_imovel_id ON imagem_imovel(imovel_id);
CREATE INDEX IF NOT EXISTS idx_imagem_principal ON imagem_imovel(principal);
CREATE INDEX IF NOT EXISTS idx_imovel_quartos ON imovel(quartos);
CREATE INDEX IF NOT EXISTS idx_imovel_status ON imovel(status);
CREATE INDEX IF NOT EXISTS idx_imovel_condicao ON imovel(condicao);

-- Atualizar registros existentes com valores padrão
UPDATE imovel SET status = 'DISPONIVEL' WHERE status IS NULL;
UPDATE imovel SET aceita_financiamento = false WHERE aceita_financiamento IS NULL;

-- Comentários nas colunas
COMMENT ON COLUMN imovel.quartos IS 'Número de quartos do imóvel';
COMMENT ON COLUMN imovel.banheiros IS 'Número de banheiros do imóvel';
COMMENT ON COLUMN imovel.vagas IS 'Número de vagas de garagem';
COMMENT ON COLUMN imovel.condicao IS 'Condição do imóvel: NOVO, USADO, REFORMADO';
COMMENT ON COLUMN imovel.status IS 'Status do imóvel: DISPONIVEL, VENDIDO, SUSPENSO';
COMMENT ON COLUMN imagem_imovel.principal IS 'Indica se é a imagem principal (capa) do imóvel';
COMMENT ON COLUMN imagem_imovel.ordem IS 'Ordem de exibição da imagem na galeria';

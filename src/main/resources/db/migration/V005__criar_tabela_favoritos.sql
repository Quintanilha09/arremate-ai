-- Migration V005: Criar tabela de favoritos (wishlist)
-- Fase 1.5 - Sistema de Favoritos

CREATE TABLE favorito (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id VARCHAR(100) NOT NULL,
    imovel_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraint para evitar duplicatas
    CONSTRAINT uk_favorito_usuario_imovel UNIQUE (usuario_id, imovel_id),
    
    -- Foreign key para imovel
    CONSTRAINT fk_favorito_imovel FOREIGN KEY (imovel_id) 
        REFERENCES imovel(id) ON DELETE CASCADE
);

-- Índices para otimizar queries
CREATE INDEX idx_favorito_usuario_id ON favorito(usuario_id);
CREATE INDEX idx_favorito_imovel_id ON favorito(imovel_id);
CREATE INDEX idx_favorito_created_at ON favorito(created_at DESC);

-- Comentários
COMMENT ON TABLE favorito IS 'Armazena os imóveis favoritos de cada usuário (wishlist)';
COMMENT ON COLUMN favorito.usuario_id IS 'ID temporário do usuário como string até implementar autenticação JWT na Fase 2';
COMMENT ON COLUMN favorito.imovel_id IS 'Referência ao imóvel favoritado';
COMMENT ON COLUMN favorito.created_at IS 'Data e hora em que o favorito foi adicionado';

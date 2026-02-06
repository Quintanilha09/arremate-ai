-- V006: Adicionar relacionamento entre Imovel e Usuario
-- Data: 06/02/2026
-- Descrição: Adiciona coluna usuario_id na tabela imovel para rastrear o criador

-- Adicionar coluna usuario_id
ALTER TABLE imovel
ADD COLUMN usuario_id UUID;

-- Adicionar foreign key
ALTER TABLE imovel
ADD CONSTRAINT fk_imovel_usuario 
FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE SET NULL;

-- Criar índice para performance
CREATE INDEX idx_imovel_usuario ON imovel(usuario_id);

-- Comentário
COMMENT ON COLUMN imovel.usuario_id IS 'ID do usuário que criou o imóvel (VENDEDOR ou ADMIN)';

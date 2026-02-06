-- Adiciona campo de avatar e melhora informações do perfil

-- Adiciona coluna avatar_url na tabela usuario
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS avatar_url VARCHAR(500);

-- Adiciona índice para consultas por CPF (se existir)
CREATE INDEX IF NOT EXISTS idx_usuario_cpf ON usuario(cpf);

-- Comentários
COMMENT ON COLUMN usuario.avatar_url IS 'URL da foto de perfil/avatar do usuário';

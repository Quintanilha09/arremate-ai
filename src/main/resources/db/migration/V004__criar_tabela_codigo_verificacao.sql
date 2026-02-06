-- Criação da tabela de códigos de verificação (2FA)

CREATE TABLE IF NOT EXISTS codigo_verificacao (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL,
    codigo VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    verificado BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    verified_at TIMESTAMP
);

-- Índices para melhorar performance
CREATE INDEX idx_codigo_verificacao_email ON codigo_verificacao(email);
CREATE INDEX idx_codigo_verificacao_expires_at ON codigo_verificacao(expires_at);
CREATE INDEX idx_codigo_verificacao_email_codigo ON codigo_verificacao(email, codigo, verificado);

-- Comentários
COMMENT ON TABLE codigo_verificacao IS 'Armazena códigos de verificação para autenticação de 2 fatores';
COMMENT ON COLUMN codigo_verificacao.codigo IS 'Código de 6 dígitos enviado por email';
COMMENT ON COLUMN codigo_verificacao.expires_at IS 'Data e hora de expiração do código (10 minutos)';
COMMENT ON COLUMN codigo_verificacao.verificado IS 'Indica se o código já foi utilizado';

-- V008: Tabela de dispositivos confiáveis para 2FA adaptativo

CREATE TABLE IF NOT EXISTS trusted_device (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL,
    device_token VARCHAR(255) NOT NULL UNIQUE,
    fingerprint VARCHAR(500),
    user_agent VARCHAR(500),
    ip_address VARCHAR(45),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    
    CONSTRAINT fk_trusted_device_usuario FOREIGN KEY (usuario_id) 
        REFERENCES usuario(id) ON DELETE CASCADE
);

-- Índices para otimizar consultas
CREATE INDEX idx_trusted_device_usuario_id ON trusted_device(usuario_id);
CREATE INDEX idx_trusted_device_token ON trusted_device(device_token);
CREATE INDEX idx_trusted_device_expires_at ON trusted_device(expires_at);

-- Comentários
COMMENT ON TABLE trusted_device IS 'Dispositivos confiáveis para skip de 2FA';
COMMENT ON COLUMN trusted_device.device_token IS 'Token único do dispositivo (JWT ou UUID)';
COMMENT ON COLUMN trusted_device.fingerprint IS 'Hash do User-Agent + outros dados para identificação';
COMMENT ON COLUMN trusted_device.expires_at IS 'Data de expiração do trust (padrão 90 dias)';
COMMENT ON COLUMN trusted_device.revoked IS 'Se o dispositivo foi revogado manualmente';

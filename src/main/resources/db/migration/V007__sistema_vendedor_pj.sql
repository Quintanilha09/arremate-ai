-- V007: Sistema de Vendedores Pessoa Jurídica
-- Data: 06/02/2026
-- Descrição: Adiciona campos PJ ao usuário, status de vendedor, documentos e restrição de CNPJ único

-- =====================================================
-- Adicionar campos PJ ao Usuario
-- =====================================================

ALTER TABLE usuario
ADD COLUMN cnpj VARCHAR(18) UNIQUE,
ADD COLUMN razao_social VARCHAR(300),
ADD COLUMN nome_fantasia VARCHAR(300),
ADD COLUMN inscricao_estadual VARCHAR(20),
ADD COLUMN email_corporativo VARCHAR(200),
ADD COLUMN email_corporativo_verificado BOOLEAN DEFAULT false,
ADD COLUMN status_vendedor VARCHAR(30) DEFAULT 'PENDENTE_DOCUMENTOS',
ADD COLUMN motivo_rejeicao TEXT,
ADD COLUMN aprovado_por UUID,
ADD COLUMN aprovado_em TIMESTAMP;

-- Comentários
COMMENT ON COLUMN usuario.cnpj IS 'CNPJ da empresa vendedora (único por empresa)';
COMMENT ON COLUMN usuario.razao_social IS 'Razão social da empresa';
COMMENT ON COLUMN usuario.nome_fantasia IS 'Nome fantasia da empresa';
COMMENT ON COLUMN usuario.email_corporativo IS 'Email corporativo (@dominio-empresa.com.br) para verificação';
COMMENT ON COLUMN usuario.email_corporativo_verificado IS 'Se o email corporativo foi verificado';
COMMENT ON COLUMN usuario.status_vendedor IS 'Status: PENDENTE_DOCUMENTOS, PENDENTE_APROVACAO, APROVADO, REJEITADO, SUSPENSO';
COMMENT ON COLUMN usuario.motivo_rejeicao IS 'Motivo da rejeição (se status = REJEITADO)';

-- Índices
CREATE INDEX idx_usuario_cnpj ON usuario(cnpj);
CREATE INDEX idx_usuario_status_vendedor ON usuario(status_vendedor);

-- =====================================================
-- Tabela: documento_vendedor
-- =====================================================

CREATE TABLE documento_vendedor (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    nome_arquivo VARCHAR(500) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    tamanho_bytes BIGINT,
    mime_type VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    motivo_rejeicao TEXT,
    analisado_por UUID,
    analisado_em TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_documento_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_documento_analisado_por FOREIGN KEY (analisado_por) REFERENCES usuario(id) ON DELETE SET NULL,
    CONSTRAINT chk_documento_tipo CHECK (tipo IN ('CNPJ_RECEITA', 'CONTRATO_SOCIAL', 'MEI', 'RG_RESPONSAVEL', 'CNH_RESPONSAVEL', 'COMPROVANTE_ENDERECO', 'CRECI')),
    CONSTRAINT chk_documento_status CHECK (status IN ('PENDENTE', 'APROVADO', 'REJEITADO'))
);

-- Comentários
COMMENT ON TABLE documento_vendedor IS 'Documentos enviados por vendedores para aprovação';
COMMENT ON COLUMN documento_vendedor.tipo IS 'Tipo do documento: CNPJ_RECEITA, CONTRATO_SOCIAL, MEI, RG_RESPONSAVEL, CNH_RESPONSAVEL, COMPROVANTE_ENDERECO, CRECI';
COMMENT ON COLUMN documento_vendedor.status IS 'Status da análise: PENDENTE, APROVADO, REJEITADO';

-- Índices
CREATE INDEX idx_documento_usuario_id ON documento_vendedor(usuario_id);
CREATE INDEX idx_documento_status ON documento_vendedor(status);
CREATE INDEX idx_documento_tipo ON documento_vendedor(tipo);

-- =====================================================
-- Tabela: historico_status_vendedor
-- =====================================================

CREATE TABLE historico_status_vendedor (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL,
    status_anterior VARCHAR(30),
    status_novo VARCHAR(30) NOT NULL,
    motivo TEXT,
    alterado_por UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_historico_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_historico_alterado_por FOREIGN KEY (alterado_por) REFERENCES usuario(id) ON DELETE SET NULL
);

-- Comentários
COMMENT ON TABLE historico_status_vendedor IS 'Auditoria de mudanças de status de vendedores';

-- Índices
CREATE INDEX idx_historico_usuario_id ON historico_status_vendedor(usuario_id);
CREATE INDEX idx_historico_created_at ON historico_status_vendedor(created_at);

-- =====================================================
-- Constraint: CNPJ único e obrigatório para VENDEDOREs
-- =====================================================

-- Será validado pela aplicação, mas adicionamos índice único
CREATE UNIQUE INDEX idx_usuario_cnpj_unique ON usuario(cnpj) WHERE cnpj IS NOT NULL;

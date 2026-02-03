-- Migration V001: Criar schema completo do banco de dados
-- Autor: Sistema
-- Data: 2026-02-02
-- Descrição: Cria todas as tabelas necessárias baseadas nas entidades JPA

-- =====================================================
-- TABELA: imovel
-- =====================================================
CREATE TABLE imovel (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero_leilao VARCHAR(100) NOT NULL UNIQUE,
    descricao VARCHAR(1000) NOT NULL,
    valor_avaliacao DECIMAL(15, 2) NOT NULL,
    data_leilao DATE NOT NULL,
    uf VARCHAR(2) NOT NULL,
    instituicao VARCHAR(300) NOT NULL,
    link_edital VARCHAR(1000),
    cidade VARCHAR(100),
    bairro VARCHAR(200),
    area_total DECIMAL(10, 2),
    tipo_imovel VARCHAR(50),
    quartos INTEGER,
    banheiros INTEGER,
    vagas INTEGER,
    endereco VARCHAR(500),
    cep VARCHAR(10),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    condicao VARCHAR(50),
    aceita_financiamento BOOLEAN DEFAULT false,
    observacoes VARCHAR(2000),
    status VARCHAR(20) DEFAULT 'DISPONIVEL',
    ativo BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Índices para tabela imovel
CREATE INDEX idx_imovel_uf ON imovel(uf);
CREATE INDEX idx_imovel_data_leilao ON imovel(data_leilao);
CREATE INDEX idx_imovel_valor ON imovel(valor_avaliacao);
CREATE INDEX idx_imovel_status ON imovel(status);

-- =====================================================
-- TABELA: imagem_imovel
-- =====================================================
CREATE TABLE imagem_imovel (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    imovel_id UUID NOT NULL,
    url VARCHAR(1000) NOT NULL,
    legenda VARCHAR(500),
    principal BOOLEAN DEFAULT false,
    ordem INTEGER DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_imagem_imovel FOREIGN KEY (imovel_id) REFERENCES imovel(id) ON DELETE CASCADE
);

-- Índices para tabela imagem_imovel
CREATE INDEX idx_imagem_imovel_id ON imagem_imovel(imovel_id);
CREATE INDEX idx_imagem_principal ON imagem_imovel(principal);

-- =====================================================
-- TABELA: usuario
-- =====================================================
CREATE TABLE usuario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nome VARCHAR(200) NOT NULL,
    email VARCHAR(200) NOT NULL UNIQUE,
    senha VARCHAR(500) NOT NULL,
    telefone VARCHAR(20),
    cpf VARCHAR(14),
    tipo VARCHAR(20) NOT NULL DEFAULT 'COMPRADOR',
    ativo BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT chk_usuario_tipo CHECK (tipo IN ('COMPRADOR', 'VENDEDOR', 'ADMIN'))
);

-- Índices para tabela usuario
CREATE UNIQUE INDEX idx_usuario_email ON usuario(email);

-- =====================================================
-- TABELA: favorito
-- =====================================================
CREATE TABLE favorito (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL,
    imovel_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_favorito_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_favorito_imovel FOREIGN KEY (imovel_id) REFERENCES imovel(id) ON DELETE CASCADE,
    CONSTRAINT uk_favorito_usuario_imovel UNIQUE (usuario_id, imovel_id)
);

-- Índices para tabela favorito
CREATE INDEX idx_favorito_usuario_id ON favorito(usuario_id);
CREATE INDEX idx_favorito_imovel_id ON favorito(imovel_id);

-- =====================================================
-- TABELA: leiloeira
-- =====================================================
CREATE TABLE leiloeira (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    url VARCHAR(500) NOT NULL,
    tipo_integracao VARCHAR(50) NOT NULL,
    logo_url TEXT,
    configuracao_json JSONB,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVA',
    ultima_sincronizacao TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT chk_leiloeira_tipo CHECK (tipo_integracao IN ('API', 'SCRAPING', 'WHITE_LABEL')),
    CONSTRAINT chk_leiloeira_status CHECK (status IN ('ATIVA', 'INATIVA', 'MANUTENCAO'))
);

-- =====================================================
-- TABELA: leilao
-- =====================================================
CREATE TABLE leilao (
    id BIGSERIAL PRIMARY KEY,
    leiloeira_id BIGINT NOT NULL,
    titulo VARCHAR(500) NOT NULL,
    descricao TEXT,
    data_inicio TIMESTAMP,
    data_encerramento TIMESTAMP,
    localizacao VARCHAR(200),
    status VARCHAR(30) NOT NULL DEFAULT 'AGENDADO',
    url_edital VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_leilao_leiloeira FOREIGN KEY (leiloeira_id) REFERENCES leiloeira(id) ON DELETE CASCADE,
    CONSTRAINT chk_leilao_status CHECK (status IN ('AGENDADO', 'EM_ANDAMENTO', 'ENCERRADO', 'CANCELADO'))
);

-- Índices para tabela leilao
CREATE INDEX idx_leilao_leiloeira ON leilao(leiloeira_id);
CREATE INDEX idx_leilao_status ON leilao(status);

-- =====================================================
-- TABELA: produto
-- =====================================================
CREATE TABLE produto (
    id BIGSERIAL PRIMARY KEY,
    leilao_id BIGINT NOT NULL,
    leiloeira_id BIGINT NOT NULL,
    titulo VARCHAR(500) NOT NULL,
    descricao TEXT,
    categoria VARCHAR(100),
    subcategoria VARCHAR(100),
    condicao VARCHAR(30),
    valor_avaliacao DECIMAL(15, 2),
    lance_minimo DECIMAL(15, 2),
    lance_atual DECIMAL(15, 2),
    fotos_urls TEXT[],
    especificacoes JSONB,
    localizacao VARCHAR(200),
    data_limite TIMESTAMP,
    status VARCHAR(30) NOT NULL DEFAULT 'DISPONIVEL',
    payload_original JSONB,
    url_original VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_produto_leilao FOREIGN KEY (leilao_id) REFERENCES leilao(id) ON DELETE CASCADE,
    CONSTRAINT fk_produto_leiloeira FOREIGN KEY (leiloeira_id) REFERENCES leiloeira(id) ON DELETE CASCADE,
    CONSTRAINT chk_produto_condicao CHECK (condicao IN ('NOVO', 'USADO', 'SEMINOVO', 'NAO_INFORMADO')),
    CONSTRAINT chk_produto_status CHECK (status IN ('DISPONIVEL', 'VENDIDO', 'CANCELADO', 'REMOVIDO'))
);

-- Índices para tabela produto
CREATE INDEX idx_produto_categoria ON produto(categoria);
CREATE INDEX idx_produto_status ON produto(status);
CREATE INDEX idx_produto_leilao ON produto(leilao_id);

-- =====================================================
-- COMENTÁRIOS
-- =====================================================
COMMENT ON TABLE imovel IS 'Tabela principal de imóveis em leilão';
COMMENT ON TABLE imagem_imovel IS 'Imagens relacionadas aos imóveis';
COMMENT ON TABLE usuario IS 'Usuários do sistema com autenticação';
COMMENT ON TABLE favorito IS 'Favoritos dos usuários (imóveis marcados)';
COMMENT ON TABLE leiloeira IS 'Leiloeiras/casas de leilão integradas';
COMMENT ON TABLE leilao IS 'Leilões cadastrados';
COMMENT ON TABLE produto IS 'Produtos/itens dos leilões';

-- =====================================================
-- DADOS INICIAIS
-- =====================================================

-- Inserir usuário admin padrão (senha: admin123)
-- BCrypt hash de "admin123": $2a$10$N9qo8uLOickgx2ZMRZoMye8IFdCdg2q8pMbV0q8/BXlBgQQZ3QJKG
INSERT INTO usuario (nome, email, senha, tipo, ativo, created_at)
VALUES ('Administrador', 'admin@arremateai.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye8IFdCdg2q8pMbV0q8/BXlBgQQZ3QJKG', 'ADMIN', true, CURRENT_TIMESTAMP);

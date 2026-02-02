-- Migration V004: Converter IDs para UUID e deletar dados antigos
-- Autor: Sistema
-- Data: 2025
-- Descrição: Remove todos os registros antigos e converte as colunas de ID para UUID

-- 1. Deletar todos os registros existentes
DELETE FROM imagem_imovel;
DELETE FROM imovel;

-- 2. Remover todas as constraints de chave estrangeira
ALTER TABLE imagem_imovel DROP CONSTRAINT IF EXISTS fk_imagem_imovel_imovel;
ALTER TABLE imagem_imovel DROP CONSTRAINT IF EXISTS fkemnwkm9alvkyram825s3kgbtr;

-- 3. Remover chaves primárias
ALTER TABLE imagem_imovel DROP CONSTRAINT IF EXISTS imagem_imovel_pkey;
ALTER TABLE imovel DROP CONSTRAINT IF EXISTS imovel_pkey CASCADE;

-- 4. Remover defaults e sequences
ALTER TABLE imovel ALTER COLUMN id DROP DEFAULT;
ALTER TABLE imagem_imovel ALTER COLUMN id DROP DEFAULT;
DROP SEQUENCE IF EXISTS imovel_seq;
DROP SEQUENCE IF EXISTS imagem_imovel_seq;

-- 5. Converter colunas para UUID
ALTER TABLE imovel ALTER COLUMN id TYPE UUID USING gen_random_uuid();
ALTER TABLE imagem_imovel ALTER COLUMN id TYPE UUID USING gen_random_uuid();
ALTER TABLE imagem_imovel ALTER COLUMN imovel_id TYPE UUID USING gen_random_uuid();

-- 6. Adicionar defaults UUID
ALTER TABLE imovel ALTER COLUMN id SET DEFAULT gen_random_uuid();
ALTER TABLE imagem_imovel ALTER COLUMN id SET DEFAULT gen_random_uuid();

-- 7. Recriar chaves primárias
ALTER TABLE imovel ADD PRIMARY KEY (id);
ALTER TABLE imagem_imovel ADD PRIMARY KEY (id);

-- 8. Recriar constraint de chave estrangeira
ALTER TABLE imagem_imovel 
    ADD CONSTRAINT fk_imagem_imovel_imovel 
    FOREIGN KEY (imovel_id) 
    REFERENCES imovel(id) 
    ON DELETE CASCADE;

-- 9. Comentários
COMMENT ON COLUMN imovel.id IS 'Identificador único do imóvel (UUID)';
COMMENT ON COLUMN imagem_imovel.id IS 'Identificador único da imagem (UUID)';
COMMENT ON COLUMN imagem_imovel.imovel_id IS 'Referência para o imóvel (UUID)';

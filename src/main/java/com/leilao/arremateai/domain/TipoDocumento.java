package com.leilao.arremateai.domain;

/**
 * Tipos de documentos aceitos para cadastro de vendedor
 */
public enum TipoDocumento {
    
    /**
     * Comprovante de inscrição no CNPJ (Receita Federal)
     */
    CNPJ_RECEITA("Comprovante CNPJ"),
    
    /**
     * Contrato Social da empresa (SA, LTDA)
     */
    CONTRATO_SOCIAL("Contrato Social"),
    
    /**
     * Certificado MEI (Microempreendedor Individual)
     */
    MEI("Certificado MEI"),
    
    /**
     * RG do responsável legal
     */
    RG_RESPONSAVEL("RG do Responsável"),
    
    /**
     * CNH do responsável legal
     */
    CNH_RESPONSAVEL("CNH do Responsável"),
    
    /**
     * Comprovante de endereço comercial (conta de luz, água, etc)
     */
    COMPROVANTE_ENDERECO("Comprovante de Endereço"),
    
    /**
     * CRECI (opcional, para corretores)
     */
    CRECI("Registro CRECI");
    
    private final String descricao;
    
    TipoDocumento(String descricao) {
        this.descricao = descricao;
    }
    
    public String getDescricao() {
        return descricao;
    }
}

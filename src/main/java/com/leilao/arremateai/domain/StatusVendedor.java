package com.leilao.arremateai.domain;

/**
 * Status do processo de aprovação do vendedor
 */
public enum StatusVendedor {
    
    /**
     * Vendedor registrado mas ainda não enviou todos os documentos
     */
    PENDENTE_DOCUMENTOS,
    
    /**
     * Documentos enviados, aguardando análise do admin
     */
    PENDENTE_APROVACAO,
    
    /**
     * Vendedor aprovado, pode criar anúncios
     */
    APROVADO,
    
    /**
     * Vendedor rejeitado, não pode criar anúncios
     */
    REJEITADO,
    
    /**
     * Vendedor suspenso temporariamente
     */
    SUSPENSO
}

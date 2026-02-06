package com.leilao.arremateai.domain;

/**
 * Status da análise de um documento enviado
 */
public enum StatusDocumento {
    
    /**
     * Documento enviado, aguardando análise
     */
    PENDENTE,
    
    /**
     * Documento aprovado pelo admin
     */
    APROVADO,
    
    /**
     * Documento rejeitado (ilegível, inválido, etc)
     */
    REJEITADO
}

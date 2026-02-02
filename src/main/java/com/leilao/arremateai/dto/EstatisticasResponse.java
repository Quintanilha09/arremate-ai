package com.leilao.arremateai.dto;

import java.math.BigDecimal;
import java.util.Map;

public record EstatisticasResponse(
    // Totais
    Long totalImoveis,
    Long totalAtivos,
    Long totalInativos,
    
    // Agrupamentos
    Map<String, Long> totalPorUf,
    Map<String, Long> totalPorCidade,
    Map<String, Long> totalPorTipo,
    Map<String, Long> totalPorInstituicao,
    Map<String, Long> totalPorStatus,
    
    // Valores
    BigDecimal valorMedio,
    BigDecimal valorMinimo,
    BigDecimal valorMaximo,
    BigDecimal valorTotal,
    
    // Estatísticas de imagens
    Long imoveisComImagem,
    Long imoveisSemImagem,
    Double percentualComImagem
) {}

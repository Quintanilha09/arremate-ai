package com.leilao.arremateai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImovelResponse {
    
    private String numeroLeilao;
    private String descricao;
    private BigDecimal valorAvaliacao;
    private String dataLeilao;
    private String uf;
    private String instituicao;
    private String linkEdital;
}

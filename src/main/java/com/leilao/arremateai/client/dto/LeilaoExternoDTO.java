package com.leilao.arremateai.client.dto;

import java.math.BigDecimal;

public record LeilaoExternoDTO(
    String numeroLeilao,
    String descricao,
    BigDecimal valorAvaliacao,
    String dataLeilao,
    String uf,
    String instituicao,
    String linkEdital
) {}

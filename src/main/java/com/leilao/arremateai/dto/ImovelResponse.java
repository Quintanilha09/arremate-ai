package com.leilao.arremateai.dto;

import java.math.BigDecimal;

public record ImovelResponse(
    String numeroCompra,
    String descricao,
    BigDecimal valorEstimado,
    String dataPublicacao,
    String uf,
    String nomeOrgao,
    String link
) {}

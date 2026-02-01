package com.leilao.arremateai.dto;

import java.math.BigDecimal;

public record ImovelRequest(
    String numeroLeilao,
    String descricao,
    BigDecimal valorAvaliacao,
    String dataLeilao,
    String uf,
    String instituicao,
    String linkEdital,
    String cidade,
    String bairro,
    BigDecimal areaTotal,
    String tipoImovel
) {}

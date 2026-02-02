package com.leilao.arremateai.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FavoritoResponse(
    UUID id,
    String usuarioId,
    ImovelResponse imovel,
    LocalDateTime createdAt
) {}

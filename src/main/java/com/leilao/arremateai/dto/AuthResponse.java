package com.leilao.arremateai.dto;

public record AuthResponse(
    String token,
    String tipo,
    UsuarioResponse usuario
) {}

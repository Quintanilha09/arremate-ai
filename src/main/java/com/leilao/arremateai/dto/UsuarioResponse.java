package com.leilao.arremateai.dto;

import com.leilao.arremateai.domain.TipoUsuario;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioResponse(
    UUID id,
    String nome,
    String email,
    String telefone,
    String cpf,
    TipoUsuario tipo,
    Boolean ativo,
    LocalDateTime createdAt
) {}

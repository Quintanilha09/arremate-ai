package com.leilao.arremateai.dto;

import com.leilao.arremateai.domain.TipoUsuario;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para resposta de dados do perfil do usuário
 * Inclui informações completas do perfil, incluindo avatar
 */
public record PerfilResponse(
    UUID id,
    String nome,
    String email,
    String telefone,
    String cpf,
    TipoUsuario tipo,
    String avatarUrl,
    Boolean ativo,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

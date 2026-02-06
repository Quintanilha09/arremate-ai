package com.leilao.arremateai.controller;

import com.leilao.arremateai.dto.AlterarSenhaRequest;
import com.leilao.arremateai.dto.AtualizarPerfilRequest;
import com.leilao.arremateai.dto.PerfilResponse;
import com.leilao.arremateai.service.PerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Controller REST para operações de perfil do usuário
 * Endpoints protegidos por autenticação JWT
 */
@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
@Slf4j
public class PerfilController {

    private final PerfilService perfilService;

    /**
     * GET /api/perfil - Busca dados do perfil do usuário autenticado
     */
    @GetMapping
    public ResponseEntity<PerfilResponse> buscarPerfil(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("Requisição para buscar perfil do usuário: {}", userDetails.getUsername());
        PerfilResponse perfil = perfilService.buscarPerfil(userDetails.getUsername());
        return ResponseEntity.ok(perfil);
    }

    /**
     * PUT /api/perfil - Atualiza dados do perfil do usuário
     */
    @PutMapping
    public ResponseEntity<PerfilResponse> atualizarPerfil(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AtualizarPerfilRequest request) {
        
        log.info("Requisição para atualizar perfil do usuário: {}", userDetails.getUsername());
        PerfilResponse perfil = perfilService.atualizarPerfil(userDetails.getUsername(), request);
        return ResponseEntity.ok(perfil);
    }

    /**
     * PUT /api/perfil/senha - Altera senha do usuário
     */
    @PutMapping("/senha")
    public ResponseEntity<Void> alterarSenha(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AlterarSenhaRequest request) {
        
        log.info("Requisição para alterar senha do usuário: {}", userDetails.getUsername());
        perfilService.alterarSenha(userDetails.getUsername(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/perfil/avatar - Upload de avatar do usuário
     * Content-Type: multipart/form-data
     */
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PerfilResponse> uploadAvatar(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("avatar") MultipartFile file) throws IOException {
        
        log.info("Requisição para upload de avatar do usuário: {}", userDetails.getUsername());
        PerfilResponse perfil = perfilService.uploadAvatar(userDetails.getUsername(), file);
        return ResponseEntity.ok(perfil);
    }

    /**
     * DELETE /api/perfil/avatar - Remove avatar do usuário
     */
    @DeleteMapping("/avatar")
    public ResponseEntity<PerfilResponse> removerAvatar(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("Requisição para remover avatar do usuário: {}", userDetails.getUsername());
        PerfilResponse perfil = perfilService.removerAvatar(userDetails.getUsername());
        return ResponseEntity.ok(perfil);
    }
}

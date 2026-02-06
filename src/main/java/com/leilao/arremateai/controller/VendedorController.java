package com.leilao.arremateai.controller;

import com.leilao.arremateai.domain.TipoDocumento;
import com.leilao.arremateai.dto.CadastroVendedorRequest;
import com.leilao.arremateai.dto.DocumentoVendedorResponse;
import com.leilao.arremateai.dto.VendedorResponse;
import com.leilao.arremateai.service.VendedorService;
import com.leilao.arremateai.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Controller para registro e gestão de vendedores
 */
@RestController
@RequestMapping("/api/vendedores")
@RequiredArgsConstructor
public class VendedorController {
    
    private final VendedorService vendedorService;
    private final UsuarioRepository usuarioRepository;
    
    /**
     * Registro de novo vendedor PJ (público)
     */
    @PostMapping("/registrar")
    public ResponseEntity<VendedorResponse> registrarVendedor(
            @Valid @RequestBody CadastroVendedorRequest request) {
        VendedorResponse response = vendedorService.registrarVendedor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Busca status do vendedor logado
     */
    @GetMapping("/meu-status")
    @PreAuthorize("hasRole('VENDEDOR')")
    public ResponseEntity<VendedorResponse> buscarMeuStatus(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"))
                .getId();
        VendedorResponse response = vendedorService.buscarMeuStatus(userId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Lista documentos do vendedor logado
     */
    @GetMapping("/meus-documentos")
    @PreAuthorize("hasRole('VENDEDOR')")
    public ResponseEntity<List<DocumentoVendedorResponse>> listarMeusDocumentos(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"))
                .getId();
        List<DocumentoVendedorResponse> documentos = vendedorService.listarMeusDocumentos(userId);
        return ResponseEntity.ok(documentos);
    }
    
    /**
     * Upload de documento do vendedor
     */
    @PostMapping("/documentos")
    @PreAuthorize("hasRole('VENDEDOR')")
    public ResponseEntity<DocumentoVendedorResponse> uploadDocumento(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("tipo") TipoDocumento tipo,
            @RequestParam("arquivo") MultipartFile arquivo) {
        
        // Validar arquivo
        if (arquivo.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não pode estar vazio");
        }
        
        // Validar tipo do arquivo (aceita PDF, JPG, PNG)
        String contentType = arquivo.getContentType();
        if (contentType == null || !contentType.matches("application/pdf|image/jpeg|image/png")) {
            throw new IllegalArgumentException("Tipo de arquivo não permitido. Envie PDF, JPG ou PNG");
        }
        
        // Validar tamanho (max 5MB)
        if (arquivo.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("Arquivo deve ter no máximo 5MB");
        }
        
        UUID userId = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"))
                .getId();
        
        DocumentoVendedorResponse response = vendedorService.uploadDocumento(
                userId, tipo, arquivo);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

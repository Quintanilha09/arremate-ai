package com.leilao.arremateai.controller;

import com.leilao.arremateai.dto.*;
import com.leilao.arremateai.service.VendedorService;
import com.leilao.arremateai.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller para administração de vendedores (ADMIN only)
 */
@RestController
@RequestMapping( "/api/admin/vendedores")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminVendedorController {
    
    private final VendedorService vendedorService;
    private final UsuarioRepository usuarioRepository;
    
    /**
     * Lista vendedores pendentes de aprovação
     */
    @GetMapping("/pendentes")
    public ResponseEntity<Page<VendedorResponse>> listarVendedoresPendentes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort.Direction dir = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));
        
        Page<VendedorResponse> vendedores = vendedorService.listarVendedoresPendentes(pageable);
        return ResponseEntity.ok(vendedores);
    }
    
    /**
     * Busca detalhes de um vendedor específico
     */
    @GetMapping("/{vendedorId}")
    public ResponseEntity<VendedorResponse> buscarVendedor(@PathVariable UUID vendedorId) {
        VendedorResponse vendedor = vendedorService.buscarVendedor(vendedorId);
        return ResponseEntity.ok(vendedor);
    }
    
    /**
     * Lista documentos de um vendedor
     */
    @GetMapping("/{vendedorId}/documentos")
    public ResponseEntity<List<DocumentoVendedorResponse>> listarDocumentosVendedor(
            @PathVariable UUID vendedorId) {
        List<DocumentoVendedorResponse> documentos = vendedorService.listarDocumentosVendedor(vendedorId);
        return ResponseEntity.ok(documentos);
    }
    
    /**
     * Aprova um vendedor
     */
    @PatchMapping("/{vendedorId}/aprovar")
    public ResponseEntity<VendedorResponse> aprovarVendedor(
            @PathVariable UUID vendedorId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody(required = false) AprovarVendedorRequest request) {
        
        if (request == null) {
            request = new AprovarVendedorRequest();
        }
        
        UUID adminId = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"))
                .getId();
        
        VendedorResponse vendedor = vendedorService.aprovarVendedor(
                vendedorId, adminId, request);
        
        return ResponseEntity.ok(vendedor);
    }
    
    /**
     * Rejeita um vendedor
     */
    @PatchMapping("/{vendedorId}/rejeitar")
    public ResponseEntity<VendedorResponse> rejeitarVendedor(
            @PathVariable UUID vendedorId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody RejeitarVendedorRequest request) {
        
        UUID adminId = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"))
                .getId();
        
        VendedorResponse vendedor = vendedorService.rejeitarVendedor(
                vendedorId, adminId, request);
        
        return ResponseEntity.ok(vendedor);
    }
    
    /**
     * Atualiza status de um documento específico
     */
    @PatchMapping("/documentos/{documentoId}/status")
    public ResponseEntity<DocumentoVendedorResponse> atualizarStatusDocumento(
            @PathVariable UUID documentoId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AtualizarStatusDocumentoRequest request) {
        
        UUID adminId = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"))
                .getId();
        
        DocumentoVendedorResponse documento = vendedorService.atualizarStatusDocumento(
                documentoId, adminId, request);
        
        return ResponseEntity.ok(documento);
    }
}

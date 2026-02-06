package com.leilao.arremateai.controller;

import com.leilao.arremateai.dto.AprovarVendedorRequest;
import com.leilao.arremateai.dto.RejeitarVendedorRequest;
import com.leilao.arremateai.dto.VendedorResponse;
import com.leilao.arremateai.service.AdminVendedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/vendedores")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final AdminVendedorService adminVendedorService;
    
    /**
     * Lista todos os vendedores pendentes de aprovação
     */
    @GetMapping("/pendentes")
    public ResponseEntity<List<VendedorResponse>> listarVendedoresPendentes() {
        return ResponseEntity.ok(adminVendedorService.listarVendedoresPendentes());
    }
    
    /**
     * Lista todos os vendedores (com filtros opcionais)
     */
    @GetMapping
    public ResponseEntity<List<VendedorResponse>> listarTodosVendedores(
        @RequestParam(required = false) String status
    ) {
        if (status != null) {
            return ResponseEntity.ok(adminVendedorService.listarVendedoresPorStatus(status));
        }
        return ResponseEntity.ok(adminVendedorService.listarTodosVendedores());
    }
    
    /**
     * Busca vendedor por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<VendedorResponse> buscarVendedorPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(adminVendedorService.buscarVendedorPorId(id));
    }
    
    /**
     * Aprova vendedor
     */
    @PostMapping("/{id}/aprovar")
    public ResponseEntity<VendedorResponse> aprovarVendedor(
        @PathVariable UUID id,
        @Valid @RequestBody(required = false) AprovarVendedorRequest request
    ) {
        String comentario = request != null ? request.getComentario() : null;
        return ResponseEntity.ok(adminVendedorService.aprovarVendedor(id, comentario));
    }
    
    /**
     * Rejeita vendedor
     */
    @PostMapping("/{id}/rejeitar")
    public ResponseEntity<VendedorResponse> rejeitarVendedor(
        @PathVariable UUID id,
        @Valid @RequestBody RejeitarVendedorRequest request
    ) {
        return ResponseEntity.ok(adminVendedorService.rejeitarVendedor(id, request.getMotivo()));
    }
    
    /**
     * Suspende vendedor
     */
    @PostMapping("/{id}/suspender")
    public ResponseEntity<VendedorResponse> suspenderVendedor(
        @PathVariable UUID id,
        @Valid @RequestBody RejeitarVendedorRequest request
    ) {
        return ResponseEntity.ok(adminVendedorService.suspenderVendedor(id, request.getMotivo()));
    }
    
    /**
     * Estatísticas de vendedores
     */
    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas() {
        return ResponseEntity.ok(adminVendedorService.obterEstatisticas());
    }
}

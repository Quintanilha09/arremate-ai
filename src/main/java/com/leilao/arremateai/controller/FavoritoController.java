package com.leilao.arremateai.controller;

import com.leilao.arremateai.dto.FavoritoResponse;
import com.leilao.arremateai.service.FavoritoService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    /**
     * Adiciona um imóvel aos favoritos
     * 
     * Nota: usuarioId é passado como header até implementar autenticação JWT na Fase 2
     */
    @PostMapping("/{imovelId}")
    public ResponseEntity<FavoritoResponse> adicionarFavorito(
            @RequestHeader("X-Usuario-Id") @NotBlank String usuarioId,
            @PathVariable UUID imovelId
    ) {
        FavoritoResponse favorito = favoritoService.adicionarFavorito(usuarioId, imovelId);
        return ResponseEntity.status(HttpStatus.CREATED).body(favorito);
    }

    /**
     * Remove um imóvel dos favoritos
     */
    @DeleteMapping("/{imovelId}")
    public ResponseEntity<Void> removerFavorito(
            @RequestHeader("X-Usuario-Id") @NotBlank String usuarioId,
            @PathVariable UUID imovelId
    ) {
        favoritoService.removerFavorito(usuarioId, imovelId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista todos os favoritos do usuário
     */
    @GetMapping
    public ResponseEntity<List<FavoritoResponse>> listarFavoritos(
            @RequestHeader("X-Usuario-Id") @NotBlank String usuarioId
    ) {
        List<FavoritoResponse> favoritos = favoritoService.listarFavoritos(usuarioId);
        return ResponseEntity.ok(favoritos);
    }

    /**
     * Verifica se um imóvel está nos favoritos
     */
    @GetMapping("/{imovelId}/status")
    public ResponseEntity<Map<String, Boolean>> verificarFavorito(
            @RequestHeader("X-Usuario-Id") @NotBlank String usuarioId,
            @PathVariable UUID imovelId
    ) {
        boolean isFavorito = favoritoService.isFavorito(usuarioId, imovelId);
        return ResponseEntity.ok(Map.of("favorito", isFavorito));
    }

    /**
     * Retorna a contagem de favoritos do usuário
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> contarFavoritos(
            @RequestHeader("X-Usuario-Id") @NotBlank String usuarioId
    ) {
        long count = favoritoService.contarFavoritos(usuarioId);
        return ResponseEntity.ok(Map.of("total", count));
    }
}

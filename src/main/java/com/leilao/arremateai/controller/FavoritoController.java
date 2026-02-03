package com.leilao.arremateai.controller;

import com.leilao.arremateai.domain.Usuario;
import com.leilao.arremateai.dto.FavoritoResponse;
import com.leilao.arremateai.service.FavoritoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/favoritos")
@PreAuthorize("isAuthenticated()")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    /**
     * Adiciona um imóvel aos favoritos
     */
    @PostMapping("/{imovelId}")
    public ResponseEntity<FavoritoResponse> adicionarFavorito(
            Authentication authentication,
            @PathVariable UUID imovelId
    ) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        FavoritoResponse favorito = favoritoService.adicionarFavorito(usuario, imovelId);
        return ResponseEntity.status(HttpStatus.CREATED).body(favorito);
    }

    /**
     * Remove um imóvel dos favoritos
     */
    @DeleteMapping("/{imovelId}")
    public ResponseEntity<Void> removerFavorito(
            Authentication authentication,
            @PathVariable UUID imovelId
    ) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        favoritoService.removerFavorito(usuario, imovelId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lista todos os favoritos do usuário
     */
    @GetMapping
    public ResponseEntity<List<FavoritoResponse>> listarFavoritos(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        List<FavoritoResponse> favoritos = favoritoService.listarFavoritos(usuario);
        return ResponseEntity.ok(favoritos);
    }

    /**
     * Verifica se um imóvel está nos favoritos
     */
    @GetMapping("/{imovelId}/status")
    public ResponseEntity<Map<String, Boolean>> verificarFavorito(
            Authentication authentication,
            @PathVariable UUID imovelId
    ) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        boolean isFavorito = favoritoService.isFavorito(usuario, imovelId);
        return ResponseEntity.ok(Map.of("favorito", isFavorito));
    }

    /**
     * Retorna a contagem de favoritos do usuário
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> contarFavoritos(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();
        long count = favoritoService.contarFavoritos(usuario);
        return ResponseEntity.ok(Map.of("total", count));
    }
}

package com.leilao.arremateai.controller;

import com.leilao.arremateai.domain.Produto;
import com.leilao.arremateai.dto.ProdutoResponse;
import com.leilao.arremateai.service.ProdutoService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@Slf4j
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    public ResponseEntity<Page<ProdutoResponse>> listar(
        @RequestParam(required = false) String categoria,
        @RequestParam(required = false) BigDecimal valorMin,
        @RequestParam(required = false) BigDecimal valorMax,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "DESC") String direction
    ) {
        log.info("GET /api/produtos - categoria: {}, valorMin: {}, valorMax: {}, page: {}, size: {}", 
                 categoria, valorMin, valorMax, page, size);

        Sort.Direction sortDirection = "ASC".equalsIgnoreCase(direction) 
            ? Sort.Direction.ASC 
            : Sort.Direction.DESC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<Produto> produtos = produtoService.buscarProdutos(categoria, valorMin, valorMax, pageable);
        Page<ProdutoResponse> response = produtos.map(ProdutoResponse::fromEntity);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
        log.info("GET /api/produtos/{}", id);
        
        Produto produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(ProdutoResponse.fromEntity(produto));
    }
}

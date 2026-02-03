package com.leilao.arremateai.controller;

import com.leilao.arremateai.dto.ImovelRequest;
import com.leilao.arremateai.dto.ImovelResponse;
import com.leilao.arremateai.service.ImovelService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/imoveis")
public class ImovelController {

    private final ImovelService imovelService;

    public ImovelController(ImovelService imovelService) {
        this.imovelService = imovelService;
    }

    @GetMapping
    public ResponseEntity<?> buscarImoveis(
            @RequestParam(required = false) String uf,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String tipoImovel,
            @RequestParam(required = false) String instituicao,
            @RequestParam(required = false) BigDecimal valorMin,
            @RequestParam(required = false) BigDecimal valorMax,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) Integer quartosMin,
            @RequestParam(required = false) Integer banheirosMin,
            @RequestParam(required = false) Integer vagasMin,
            @RequestParam(required = false) BigDecimal areaMin,
            @RequestParam(required = false) BigDecimal areaMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataLeilao") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        boolean hasFilters = uf != null || cidade != null || tipoImovel != null || 
                           instituicao != null || valorMin != null || valorMax != null || 
                           busca != null || quartosMin != null || banheirosMin != null || 
                           vagasMin != null || areaMin != null || areaMax != null ||
                           page > 0 || size != 20 || 
                           !"dataLeilao".equals(sortBy) || !"ASC".equals(direction);

        if (!hasFilters) {
            List<ImovelResponse> imoveis = imovelService.buscarTodosImoveis();
            return ResponseEntity.ok(imoveis);
        }

        Sort.Direction sortDirection = "DESC".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<ImovelResponse> resultado = imovelService.buscarComFiltros(
            uf, cidade, tipoImovel, instituicao, valorMin, valorMax, busca, 
            quartosMin, banheirosMin, vagasMin, areaMin, areaMax, pageable
        );
        
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImovelResponse> buscarPorId(@PathVariable UUID id) {
        ImovelResponse imovel = imovelService.buscarPorId(id);
        return ResponseEntity.ok(imovel);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImovelResponse> cadastrarImovel(@Valid @RequestBody ImovelRequest request) {
        ImovelResponse imovel = imovelService.cadastrarImovel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(imovel);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImovelResponse> atualizarImovel(
            @PathVariable UUID id,
            @Valid @RequestBody ImovelRequest request) {
        ImovelResponse imovel = imovelService.atualizarImovel(id, request);
        return ResponseEntity.ok(imovel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<ImovelResponse> atualizarParcial(
            @PathVariable UUID id,
            @RequestBody ImovelRequest request) {
        ImovelResponse imovel = imovelService.atualizarParcial(id, request);
        return ResponseEntity.ok(imovel);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerImovel(@PathVariable UUID id) {
        imovelService.removerImovel(id);
        return ResponseEntity.noContent().build();
    }
}

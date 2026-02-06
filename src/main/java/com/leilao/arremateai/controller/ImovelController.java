package com.leilao.arremateai.controller;

import com.leilao.arremateai.dto.AtualizarStatusRequest;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
        // Validar campo de ordenação - mapear nomes legados para nomes corretos
        String validatedSortBy = sortBy;
        if ("dataCadastro".equals(sortBy)) {
            validatedSortBy = "createdAt";
        } else if (!isValidSortField(sortBy)) {
            validatedSortBy = "dataLeilao"; // fallback to default
        }
        
        boolean hasFilters = uf != null || cidade != null || tipoImovel != null || 
                           instituicao != null || valorMin != null || valorMax != null || 
                           busca != null || quartosMin != null || banheirosMin != null || 
                           vagasMin != null || areaMin != null || areaMax != null ||
                           page > 0 || size != 20 || 
                           !"dataLeilao".equals(validatedSortBy) || !"ASC".equals(direction);

        if (!hasFilters) {
            List<ImovelResponse> imoveis = imovelService.buscarTodosImoveis();
            return ResponseEntity.ok(imoveis);
        }

        Sort.Direction sortDirection = "DESC".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, validatedSortBy));
        
        Page<ImovelResponse> resultado = imovelService.buscarComFiltros(
            uf, cidade, tipoImovel, instituicao, valorMin, valorMax, busca, 
            quartosMin, banheirosMin, vagasMin, areaMin, areaMax, pageable
        );
        
        return ResponseEntity.ok(resultado);
    }
    
    private boolean isValidSortField(String field) {
        // Lista de campos válidos para ordenação na entidade Imovel
        return field != null && (
            field.equals("dataLeilao") ||
            field.equals("valorAvaliacao") ||
            field.equals("createdAt") ||
            field.equals("cidade") ||
            field.equals("uf") ||
            field.equals("tipoImovel")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImovelResponse> buscarPorId(@PathVariable UUID id) {
        ImovelResponse imovel = imovelService.buscarPorId(id);
        return ResponseEntity.ok(imovel);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public ResponseEntity<ImovelResponse> cadastrarImovel(
            @Valid @RequestBody ImovelRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ImovelResponse imovel = imovelService.cadastrarImovel(request, userDetails.getUsername());
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

    @GetMapping("/{id}/validar")
    public ResponseEntity<?> validarImovel(@PathVariable UUID id) {
        return ResponseEntity.ok(imovelService.validarImovel(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public ResponseEntity<ImovelResponse> atualizarStatus(
            @PathVariable UUID id,
            @Valid @RequestBody AtualizarStatusRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ImovelResponse imovel = imovelService.atualizarStatus(
            id, request.getStatus(), userDetails.getUsername()
        );
        return ResponseEntity.ok(imovel);
    }

    @GetMapping("/meus")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN')")
    public ResponseEntity<?> buscarMeusImoveis(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        Sort.Direction sortDirection = "DESC".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<ImovelResponse> resultado = imovelService.buscarImoveisPorUsuario(
            userDetails.getUsername(), status, pageable
        );
        
        return ResponseEntity.ok(resultado);
    }
}

package com.leilao.arremateai.controller;

import com.leilao.arremateai.dto.ImovelRequest;
import com.leilao.arremateai.dto.ImovelResponse;
import com.leilao.arremateai.service.ImovelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataLeilao") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        boolean hasFilters = uf != null || cidade != null || tipoImovel != null || 
                           instituicao != null || valorMin != null || valorMax != null || 
                           page > 0 || size != 20 || !"dataLeilao".equals(sortBy) || !"ASC".equals(direction);

        if (!hasFilters) {
            List<ImovelResponse> imoveis = imovelService.buscarTodosImoveis();
            return ResponseEntity.ok(imoveis);
        }

        Sort.Direction sortDirection = "DESC".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<ImovelResponse> resultado = imovelService.buscarComFiltros(
            uf, cidade, tipoImovel, instituicao, valorMin, valorMax, pageable
        );
        
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public ResponseEntity<ImovelResponse> cadastrarImovel(@RequestBody ImovelRequest request) {
        ImovelResponse imovel = imovelService.cadastrarImovel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(imovel);
    }
}

package com.leilao.arremateai.controller;

import com.leilao.arremateai.dto.EstatisticasResponse;
import com.leilao.arremateai.dto.ImovelResponse;
import com.leilao.arremateai.service.EstatisticasService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/imoveis")
public class EstatisticasController {

    private final EstatisticasService estatisticasService;

    public EstatisticasController(EstatisticasService estatisticasService) {
        this.estatisticasService = estatisticasService;
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<EstatisticasResponse> obterEstatisticas() {
        EstatisticasResponse estatisticas = estatisticasService.obterEstatisticas();
        return ResponseEntity.ok(estatisticas);
    }

    @GetMapping("/destaques")
    public ResponseEntity<List<ImovelResponse>> obterDestaques(
            @RequestParam(required = false) Integer limite
    ) {
        List<ImovelResponse> destaques = estatisticasService.obterDestaques(limite);
        return ResponseEntity.ok(destaques);
    }

    @GetMapping("/recentes")
    public ResponseEntity<List<ImovelResponse>> obterRecentes(
            @RequestParam(required = false) Integer limite
    ) {
        List<ImovelResponse> recentes = estatisticasService.obterRecentes(limite);
        return ResponseEntity.ok(recentes);
    }

    @GetMapping("/mais-procurados")
    public ResponseEntity<List<ImovelResponse>> obterMaisProcurados(
            @RequestParam(required = false) Integer limite
    ) {
        List<ImovelResponse> maisProcurados = estatisticasService.obterMaisProcurados(limite);
        return ResponseEntity.ok(maisProcurados);
    }
}

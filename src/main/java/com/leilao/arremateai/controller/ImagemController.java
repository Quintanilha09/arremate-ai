package com.leilao.arremateai.controller;

import com.leilao.arremateai.dto.ImagemResponse;
import com.leilao.arremateai.service.ImagemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/imoveis")
public class ImagemController {

    private final ImagemService imagemService;

    public ImagemController(ImagemService imagemService) {
        this.imagemService = imagemService;
    }

    @PostMapping(value = "/{imovelId}/imagens", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ImagemResponse>> uploadImagens(
            @PathVariable UUID imovelId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        List<ImagemResponse> imagens = imagemService.uploadImagens(imovelId, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(imagens);
    }

    @GetMapping("/{imovelId}/imagens")
    public ResponseEntity<List<ImagemResponse>> listarImagens(@PathVariable UUID imovelId) {
        List<ImagemResponse> imagens = imagemService.listarImagens(imovelId);
        return ResponseEntity.ok(imagens);
    }

    @PutMapping("/imagens/{imagemId}")
    public ResponseEntity<ImagemResponse> atualizarImagem(
            @PathVariable UUID imagemId,
            @RequestParam(required = false) String legenda,
            @RequestParam(required = false) Integer ordem
    ) {
        ImagemResponse imagem = imagemService.atualizarImagem(imagemId, legenda, ordem);
        return ResponseEntity.ok(imagem);
    }

    @PatchMapping("/imagens/{imagemId}/principal")
    public ResponseEntity<Void> definirImagemPrincipal(@PathVariable UUID imagemId) {
        imagemService.definirImagemPrincipal(imagemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/imagens/{imagemId}")
    public ResponseEntity<Void> removerImagem(@PathVariable UUID imagemId) {
        imagemService.removerImagem(imagemId);
        return ResponseEntity.noContent().build();
    }
}

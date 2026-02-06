package com.leilao.arremateai.service;

import com.leilao.arremateai.domain.ImagemImovel;
import com.leilao.arremateai.domain.Imovel;
import com.leilao.arremateai.dto.ImagemResponse;
import com.leilao.arremateai.exception.BusinessException;
import com.leilao.arremateai.exception.FileStorageException;
import com.leilao.arremateai.exception.ResourceNotFoundException;
import com.leilao.arremateai.mapper.ImovelMapper;
import com.leilao.arremateai.repository.ImagemImovelRepository;
import com.leilao.arremateai.repository.ImovelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ImagemService {

    private static final Logger log = LoggerFactory.getLogger(ImagemService.class);
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private final Path fileStorageLocation;
    private final ImovelRepository imovelRepository;
    private final ImagemImovelRepository imagemRepository;
    private final ImovelMapper imovelMapper;

    public ImagemService(
            @Value("${storage.location}") String storageLocation,
            ImovelRepository imovelRepository,
            ImagemImovelRepository imagemRepository,
            ImovelMapper imovelMapper
    ) {
        this.imovelRepository = imovelRepository;
        this.imagemRepository = imagemRepository;
        this.imovelMapper = imovelMapper;
        
        this.fileStorageLocation = Paths.get(storageLocation)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("Diretório de uploads criado em: {}", this.fileStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Não foi possível criar o diretório de upload", ex);
        }
    }

    @Transactional
    public List<ImagemResponse> uploadImagens(UUID imovelId, List<MultipartFile> files) {
        log.info("Iniciando upload de {} imagens para imóvel ID: {}", files.size(), imovelId);

        Imovel imovel = imovelRepository.findById(imovelId)
                .orElseThrow(() -> new ResourceNotFoundException("Imóvel", "id", imovelId));

        if (!imovel.getAtivo()) {
            throw new BusinessException("Não é possível adicionar imagens a um imóvel inativo");
        }

        // Pegar a maior ordem atual
        int maxOrdemAtual = imagemRepository.findByImovelIdOrderByOrdemAsc(imovelId)
                .stream()
                .map(ImagemImovel::getOrdem)
                .max(Integer::compareTo)
                .orElse(0);

        final int[] ordemCounter = {maxOrdemAtual};

        List<ImagemImovel> novasImagens = files.stream()
                .map(file -> {
                    validarArquivo(file);
                    String nomeArquivo = salvarArquivo(file);
                    String url = gerarUrl(nomeArquivo);
                    
                    ordemCounter[0]++;
                    
                    ImagemImovel imagem = new ImagemImovel();
                    imagem.setImovel(imovel);
                    imagem.setUrl(url);
                    imagem.setOrdem(ordemCounter[0]);
                    imagem.setPrincipal(false);
                    imagem.setCreatedAt(LocalDateTime.now());
                    
                    return imagemRepository.save(imagem);
                })
                .toList();

        // Se não há imagem principal, define a primeira como principal
        if (imagemRepository.findByImovelIdAndPrincipalTrue(imovelId).isEmpty() && !novasImagens.isEmpty()) {
            ImagemImovel primeira = novasImagens.get(0);
            primeira.setPrincipal(true);
            imagemRepository.save(primeira);
        }

        log.info("Upload concluído: {} imagens salvas", novasImagens.size());
        return novasImagens.stream().map(imovelMapper::paraImagemResponse).toList();
    }

    public List<ImagemResponse> listarImagens(UUID imovelId) {
        log.info("Listando imagens do imóvel ID: {}", imovelId);
        
        if (!imovelRepository.existsById(imovelId)) {
            throw new ResourceNotFoundException("Imóvel", "id", imovelId);
        }

        return imagemRepository.findByImovelIdOrderByOrdemAsc(imovelId)
                .stream()
                .map(imovelMapper::paraImagemResponse)
                .toList();
    }

    @Transactional
    public ImagemResponse atualizarImagem(UUID imagemId, String legenda, Integer ordem) {
        log.info("Atualizando imagem ID: {}", imagemId);

        ImagemImovel imagem = imagemRepository.findById(imagemId)
                .orElseThrow(() -> new ResourceNotFoundException("Imagem", "id", imagemId));

        if (legenda != null) {
            imagem.setLegenda(legenda);
        }

        if (ordem != null && ordem > 0) {
            imagem.setOrdem(ordem);
        }

        ImagemImovel atualizada = imagemRepository.save(imagem);
        return imovelMapper.paraImagemResponse(atualizada);
    }

    @Transactional
    public void definirImagemPrincipal(UUID imagemId) {
        log.info("Definindo imagem ID {} como principal", imagemId);

        ImagemImovel imagem = imagemRepository.findById(imagemId)
                .orElseThrow(() -> new ResourceNotFoundException("Imagem", "id", imagemId));

        UUID imovelId = imagem.getImovel().getId();

        // Remove principal de todas as outras imagens do mesmo imóvel
        imagemRepository.findByImovelIdOrderByOrdemAsc(imovelId).forEach(img -> {
            if (img.getPrincipal()) {
                img.setPrincipal(false);
                imagemRepository.save(img);
            }
        });

        // Define a nova principal
        imagem.setPrincipal(true);
        imagemRepository.save(imagem);
    }

    @Transactional
    public void removerImagem(UUID imagemId) {
        log.info("Removendo imagem ID: {}", imagemId);

        ImagemImovel imagem = imagemRepository.findById(imagemId)
                .orElseThrow(() -> new ResourceNotFoundException("Imagem", "id", imagemId));

        // Validar se não é a última imagem do imóvel
        UUID imovelId = imagem.getImovel().getId();
        long totalImagens = imagemRepository.findByImovelIdOrderByOrdemAsc(imovelId).size();
        
        if (totalImagens <= 1) {
            throw new BusinessException("Não é possível remover a última imagem do imóvel. Cada imóvel deve ter pelo menos uma imagem.");
        }

        // Remove arquivo físico
        try {
            String filename = extrairNomeArquivo(imagem.getUrl());
            Path filePath = fileStorageLocation.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
            log.info("Arquivo removido: {}", filename);
        } catch (IOException ex) {
            log.warn("Erro ao remover arquivo físico: {}", ex.getMessage());
        }

        // Remove do banco
        imagemRepository.delete(imagem);

        // Se era a principal, define outra como principal
        if (imagem.getPrincipal()) {
            imagemRepository.findByImovelIdOrderByOrdemAsc(imovelId)
                    .stream()
                    .findFirst()
                    .ifPresent(primeira -> {
                        primeira.setPrincipal(true);
                        imagemRepository.save(primeira);
                    });
        }
    }

    private void validarArquivo(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("Arquivo vazio");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(
                    String.format("Arquivo muito grande: %d bytes. Máximo: 5MB", file.getSize())
            );
        }

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getFileExtension(filename).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(
                    String.format("Formato não permitido: %s. Permitidos: %s", extension, ALLOWED_EXTENSIONS)
            );
        }

        // Validar que é realmente uma imagem
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new BusinessException("Arquivo não é uma imagem válida");
            }
        } catch (IOException ex) {
            throw new FileStorageException("Erro ao validar imagem", ex);
        }
    }

    private String salvarArquivo(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getFileExtension(originalFilename);
        String newFilename = UUID.randomUUID().toString() + "." + extension;

        try {
            Path targetLocation = fileStorageLocation.resolve(newFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Arquivo salvo: {}", newFilename);
            return newFilename;
        } catch (IOException ex) {
            throw new FileStorageException("Erro ao salvar arquivo: " + newFilename, ex);
        }
    }

    private String gerarUrl(String filename) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(filename)
                .toUriString();
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot == -1 ? "" : filename.substring(lastDot + 1);
    }

    private String extrairNomeArquivo(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }
}

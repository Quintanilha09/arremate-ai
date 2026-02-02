package com.leilao.arremateai.service;

import com.leilao.arremateai.domain.Favorito;
import com.leilao.arremateai.domain.Imovel;
import com.leilao.arremateai.dto.FavoritoResponse;
import com.leilao.arremateai.mapper.ImovelMapper;
import com.leilao.arremateai.repository.FavoritoRepository;
import com.leilao.arremateai.repository.ImovelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FavoritoService {

    private static final Logger log = LoggerFactory.getLogger(FavoritoService.class);
    
    private final FavoritoRepository favoritoRepository;
    private final ImovelRepository imovelRepository;
    private final ImovelMapper imovelMapper;

    public FavoritoService(
            FavoritoRepository favoritoRepository,
            ImovelRepository imovelRepository,
            ImovelMapper imovelMapper
    ) {
        this.favoritoRepository = favoritoRepository;
        this.imovelRepository = imovelRepository;
        this.imovelMapper = imovelMapper;
    }

    public FavoritoResponse adicionarFavorito(String usuarioId, UUID imovelId) {
        log.info("Adicionando imóvel {} aos favoritos do usuário {}", imovelId, usuarioId);

        // Validar se o imóvel existe e está ativo
        Imovel imovel = imovelRepository.findById(imovelId)
                .filter(Imovel::getAtivo)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Imóvel não encontrado ou inativo: " + imovelId
                ));

        // Validar se já não está nos favoritos
        if (favoritoRepository.existsByUsuarioIdAndImovelId(usuarioId, imovelId)) {
            throw new IllegalStateException("Imóvel já está nos favoritos");
        }

        // Criar favorito
        Favorito favorito = new Favorito();
        favorito.setUsuarioId(usuarioId);
        favorito.setImovel(imovel);

        Favorito favoritoSalvo = favoritoRepository.save(favorito);
        log.info("Favorito criado com sucesso: {}", favoritoSalvo.getId());

        return converterParaResponse(favoritoSalvo);
    }

    public void removerFavorito(String usuarioId, UUID imovelId) {
        log.info("Removendo imóvel {} dos favoritos do usuário {}", imovelId, usuarioId);

        // Validar se o favorito existe
        Favorito favorito = favoritoRepository.findByUsuarioIdAndImovelId(usuarioId, imovelId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Favorito não encontrado para o usuário " + usuarioId + " e imóvel " + imovelId
                ));

        favoritoRepository.delete(favorito);
        log.info("Favorito removido com sucesso");
    }

    @Transactional(readOnly = true)
    public List<FavoritoResponse> listarFavoritos(String usuarioId) {
        log.info("Listando favoritos do usuário {}", usuarioId);

        List<Favorito> favoritos = favoritoRepository.findByUsuarioIdOrderByCreatedAtDesc(usuarioId);
        
        return favoritos.stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean isFavorito(String usuarioId, UUID imovelId) {
        return favoritoRepository.existsByUsuarioIdAndImovelId(usuarioId, imovelId);
    }

    @Transactional(readOnly = true)
    public long contarFavoritos(String usuarioId) {
        return favoritoRepository.countByUsuarioId(usuarioId);
    }

    private FavoritoResponse converterParaResponse(Favorito favorito) {
        return new FavoritoResponse(
                favorito.getId(),
                favorito.getUsuarioId(),
                imovelMapper.paraResponse(favorito.getImovel()),
                favorito.getCreatedAt()
        );
    }
}

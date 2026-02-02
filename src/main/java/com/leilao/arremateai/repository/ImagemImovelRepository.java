package com.leilao.arremateai.repository;

import com.leilao.arremateai.domain.ImagemImovel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImagemImovelRepository extends JpaRepository<ImagemImovel, UUID> {
    
    List<ImagemImovel> findByImovelIdOrderByOrdemAsc(UUID imovelId);
    
    Optional<ImagemImovel> findByImovelIdAndPrincipalTrue(UUID imovelId);
    
    List<ImagemImovel> findByImovelId(UUID imovelId);
    
    void deleteByImovelId(UUID imovelId);
}

package com.leilao.arremateai.repository;

import com.leilao.arremateai.domain.Favorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, UUID> {
    
    List<Favorito> findByUsuarioIdOrderByCreatedAtDesc(String usuarioId);
    
    Optional<Favorito> findByUsuarioIdAndImovelId(String usuarioId, UUID imovelId);
    
    boolean existsByUsuarioIdAndImovelId(String usuarioId, UUID imovelId);
    
    void deleteByUsuarioIdAndImovelId(String usuarioId, UUID imovelId);
    
    long countByUsuarioId(String usuarioId);
}

package com.leilao.arremateai.repository;

import com.leilao.arremateai.domain.Favorito;
import com.leilao.arremateai.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FavoritoRepository extends JpaRepository<Favorito, UUID> {
    
    List<Favorito> findByUsuarioOrderByCreatedAtDesc(Usuario usuario);
    
    Optional<Favorito> findByUsuarioAndImovelId(Usuario usuario, UUID imovelId);
    
    boolean existsByUsuarioAndImovelId(Usuario usuario, UUID imovelId);
    
    void deleteByUsuarioAndImovelId(Usuario usuario, UUID imovelId);
    
    long countByUsuario(Usuario usuario);
}

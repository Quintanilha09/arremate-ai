package com.leilao.arremateai.repository;

import com.leilao.arremateai.domain.Imovel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImovelRepository extends JpaRepository<Imovel, UUID>, JpaSpecificationExecutor<Imovel> {
    
    Optional<Imovel> findByNumeroLeilao(String numeroLeilao);
    
    boolean existsByNumeroLeilao(String numeroLeilao);

    // Buscar imóveis por email do usuário
    @Query("SELECT i FROM Imovel i WHERE i.usuario.email = :email AND i.ativo = true")
    Page<Imovel> findByUsuarioEmail(@Param("email") String email, Pageable pageable);

    // Buscar imóveis por email do usuário e status
    @Query("SELECT i FROM Imovel i WHERE i.usuario.email = :email AND i.status = :status AND i.ativo = true")
    Page<Imovel> findByUsuarioEmailAndStatus(@Param("email") String email, @Param("status") String status, Pageable pageable);
}

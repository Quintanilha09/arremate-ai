package com.leilao.arremateai.repository;

import com.leilao.arremateai.domain.Imovel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImovelRepository extends JpaRepository<Imovel, UUID>, JpaSpecificationExecutor<Imovel> {
    
    Optional<Imovel> findByNumeroLeilao(String numeroLeilao);
    
    boolean existsByNumeroLeilao(String numeroLeilao);
}

package com.leilao.arremateai.repository;

import com.leilao.arremateai.domain.HistoricoStatusVendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HistoricoStatusVendedorRepository extends JpaRepository<HistoricoStatusVendedor, UUID> {
    
    /**
     * Busca histórico de alterações de status de um vendedor
     */
    List<HistoricoStatusVendedor> findByUsuarioIdOrderByCreatedAtDesc(UUID usuarioId);
}

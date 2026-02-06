package com.leilao.arremateai.repository;

import com.leilao.arremateai.domain.DocumentoVendedor;
import com.leilao.arremateai.domain.StatusDocumento;
import com.leilao.arremateai.domain.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentoVendedorRepository extends JpaRepository<DocumentoVendedor, UUID> {
    
    /**
     * Busca todos os documentos de um vendedor
     */
    List<DocumentoVendedor> findByUsuarioId(UUID usuarioId);
    
    /**
     * Busca documentos de um vendedor por tipo
     */
    List<DocumentoVendedor> findByUsuarioIdAndTipo(UUID usuarioId, TipoDocumento tipo);
    
    /**
     * Busca documentos de um vendedor por status
     */
    List<DocumentoVendedor> findByUsuarioIdAndStatus(UUID usuarioId, StatusDocumento status);
    
    /**
     * Verifica se existe documento de um tipo específico para um vendedor
     */
    boolean existsByUsuarioIdAndTipo(UUID usuarioId, TipoDocumento tipo);
    
    /**
     * Conta documentos aprovados do vendedor
     */
    long countByUsuarioIdAndStatus(UUID usuarioId, StatusDocumento status);
    
    /**
     * Busca documento específico do vendedor por tipo
     */
    Optional<DocumentoVendedor> findFirstByUsuarioIdAndTipoOrderByCreatedAtDesc(UUID usuarioId, TipoDocumento tipo);
}

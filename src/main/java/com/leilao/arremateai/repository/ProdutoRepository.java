package com.leilao.arremateai.repository;

import com.leilao.arremateai.domain.Produto;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Page<Produto> findByStatus(Produto.StatusProduto status, Pageable pageable);

    Page<Produto> findByCategoria(String categoria, Pageable pageable);

    @Query("SELECT p FROM Produto p WHERE p.status = :status " +
           "AND (:categoria IS NULL OR p.categoria = :categoria) " +
           "AND (:valorMin IS NULL OR p.valorAvaliacao >= :valorMin) " +
           "AND (:valorMax IS NULL OR p.valorAvaliacao <= :valorMax)")
    Page<Produto> buscarComFiltros(
        @Param("status") Produto.StatusProduto status,
        @Param("categoria") String categoria,
        @Param("valorMin") BigDecimal valorMin,
        @Param("valorMax") BigDecimal valorMax,
        Pageable pageable
    );
}

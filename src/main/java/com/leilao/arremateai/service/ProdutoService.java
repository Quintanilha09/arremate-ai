package com.leilao.arremateai.service;

import com.leilao.arremateai.domain.Produto;
import com.leilao.arremateai.repository.ProdutoRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    public Page<Produto> buscarProdutos(
        String categoria,
        BigDecimal valorMin,
        BigDecimal valorMax,
        Pageable pageable
    ) {
        log.debug("Buscando produtos - categoria: {}, valorMin: {}, valorMax: {}", 
                  categoria, valorMin, valorMax);
        
        return produtoRepository.buscarComFiltros(
            Produto.StatusProduto.DISPONIVEL,
            categoria,
            valorMin,
            valorMax,
            pageable
        );
    }

    @Transactional(readOnly = true)
    public Produto buscarPorId(Long id) {
        log.debug("Buscando produto por ID: {}", id);
        return produtoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + id));
    }
}

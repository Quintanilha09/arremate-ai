package com.leilao.arremateai.dto;

import com.leilao.arremateai.domain.Produto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoResponse {

    private Long id;
    private String titulo;
    private String descricao;
    private String categoria;
    private String subcategoria;
    private String condicao;
    private BigDecimal valorAvaliacao;
    private BigDecimal lanceMinimo;
    private BigDecimal lanceAtual;
    private String[] fotosUrls;
    private String localizacao;
    private LocalDateTime dataLimite;
    private String status;
    private String urlOriginal;
    private LeiloeiraSimples leiloeira;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeiloeiraSimples {
        private Long id;
        private String nome;
        private String logoUrl;
    }

    public static ProdutoResponse fromEntity(Produto produto) {
        ProdutoResponse response = new ProdutoResponse();
        response.setId(produto.getId());
        response.setTitulo(produto.getTitulo());
        response.setDescricao(produto.getDescricao());
        response.setCategoria(produto.getCategoria());
        response.setSubcategoria(produto.getSubcategoria());
        response.setCondicao(produto.getCondicao() != null ? produto.getCondicao().name() : null);
        response.setValorAvaliacao(produto.getValorAvaliacao());
        response.setLanceMinimo(produto.getLanceMinimo());
        response.setLanceAtual(produto.getLanceAtual());
        response.setFotosUrls(produto.getFotosUrls());
        response.setLocalizacao(produto.getLocalizacao());
        response.setDataLimite(produto.getDataLimite());
        response.setStatus(produto.getStatus() != null ? produto.getStatus().name() : null);
        response.setUrlOriginal(produto.getUrlOriginal());

        if (produto.getLeiloeira() != null) {
            LeiloeiraSimples leiloeira = new LeiloeiraSimples();
            leiloeira.setId(produto.getLeiloeira().getId());
            leiloeira.setNome(produto.getLeiloeira().getNome());
            leiloeira.setLogoUrl(produto.getLeiloeira().getLogoUrl());
            response.setLeiloeira(leiloeira);
        }

        return response;
    }
}

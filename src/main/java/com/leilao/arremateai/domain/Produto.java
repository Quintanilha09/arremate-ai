package com.leilao.arremateai.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "produto", indexes = {
    @Index(name = "idx_produto_categoria", columnList = "categoria"),
    @Index(name = "idx_produto_status", columnList = "status"),
    @Index(name = "idx_produto_leilao", columnList = "leilao_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leilao_id", nullable = false)
    private Leilao leilao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leiloeira_id", nullable = false)
    private Leiloeira leiloeira;

    @Column(nullable = false, length = 500)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(length = 100)
    private String categoria;

    @Column(length = 100)
    private String subcategoria;

    @Column(length = 30)
    @Enumerated(EnumType.STRING)
    private Condicao condicao;

    @Column(name = "valor_avaliacao", precision = 15, scale = 2)
    private BigDecimal valorAvaliacao;

    @Column(name = "lance_minimo", precision = 15, scale = 2)
    private BigDecimal lanceMinimo;

    @Column(name = "lance_atual", precision = 15, scale = 2)
    private BigDecimal lanceAtual;

    @Column(name = "fotos_urls", columnDefinition = "TEXT[]")
    private String[] fotosUrls;

    @Column(name = "especificacoes", columnDefinition = "JSONB")
    private String especificacoes;

    @Column(length = 200)
    private String localizacao;

    @Column(name = "data_limite")
    private LocalDateTime dataLimite;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private StatusProduto status = StatusProduto.DISPONIVEL;

    @Column(name = "payload_original", columnDefinition = "JSONB")
    private String payloadOriginal;

    @Column(name = "url_original", length = 1000)
    private String urlOriginal;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Condicao {
        NOVO, USADO, SEMINOVO, NAO_INFORMADO
    }

    public enum StatusProduto {
        DISPONIVEL, VENDIDO, CANCELADO, REMOVIDO
    }
}

package com.leilao.arremateai.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "imovel", indexes = {
    @Index(name = "idx_imovel_uf", columnList = "uf"),
    @Index(name = "idx_imovel_data_leilao", columnList = "data_leilao"),
    @Index(name = "idx_imovel_valor", columnList = "valor_avaliacao")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Imovel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_leilao", nullable = false, unique = true, length = 100)
    private String numeroLeilao;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(name = "valor_avaliacao", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorAvaliacao;

    @Column(name = "data_leilao", nullable = false)
    private LocalDate dataLeilao;

    @Column(length = 2, nullable = false)
    private String uf;

    @Column(name = "instituicao", nullable = false, length = 300)
    private String instituicao;

    @Column(name = "link_edital", length = 1000)
    private String linkEdital;

    @Column(length = 100)
    private String cidade;

    @Column(length = 200)
    private String bairro;

    @Column(name = "area_total", precision = 10, scale = 2)
    private BigDecimal areaTotal;

    @Column(name = "tipo_imovel", length = 50)
    private String tipoImovel;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

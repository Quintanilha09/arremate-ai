package com.leilao.arremateai.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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

    // Informações essenciais
    @Column(name = "quartos")
    private Integer quartos;

    @Column(name = "banheiros")
    private Integer banheiros;

    @Column(name = "vagas")
    private Integer vagas;

    @Column(name = "endereco", length = 500)
    private String endereco;

    @Column(name = "cep", length = 10)
    private String cep;

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    // Informações adicionais
    @Column(name = "condicao", length = 50)
    private String condicao; // Novo, Usado, Reformado

    @Column(name = "aceita_financiamento")
    private Boolean aceitaFinanciamento = false;

    @Column(name = "observacoes", length = 2000)
    private String observacoes;

    @Column(name = "status", length = 20)
    private String status = "DISPONIVEL"; // DISPONIVEL, VENDIDO, SUSPENSO

    // Relacionamento com usuário criador
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Relacionamento com imagens
    @OneToMany(mappedBy = "imovel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ImagemImovel> imagens = new ArrayList<>();

    // Auditoria e controle
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "ativo")
    private Boolean ativo = true;
}

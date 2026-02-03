package com.leilao.arremateai.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "imagem_imovel", indexes = {
    @Index(name = "idx_imagem_imovel_id", columnList = "imovel_id"),
    @Index(name = "idx_imagem_principal", columnList = "principal")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImagemImovel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imovel_id", nullable = false)
    private Imovel imovel;

    @Column(name = "url", nullable = false, length = 1000)
    private String url;

    @Column(name = "legenda", length = 500)
    private String legenda;

    @Column(name = "principal")
    private Boolean principal = false;

    @Column(name = "ordem")
    private Integer ordem = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

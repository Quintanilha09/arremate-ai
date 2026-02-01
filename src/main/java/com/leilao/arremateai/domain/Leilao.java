package com.leilao.arremateai.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "leilao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Leilao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leiloeira_id", nullable = false)
    private Leiloeira leiloeira;

    @Column(nullable = false, length = 500)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Column(name = "data_encerramento")
    private LocalDateTime dataEncerramento;

    @Column(length = 200)
    private String localizacao;

    @Column(nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private StatusLeilao status = StatusLeilao.AGENDADO;

    @Column(name = "url_edital", length = 1000)
    private String urlEdital;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum StatusLeilao {
        AGENDADO, EM_ANDAMENTO, ENCERRADO, CANCELADO
    }
}

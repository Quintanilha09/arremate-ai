package com.leilao.arremateai.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "leiloeira")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Leiloeira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "tipo_integracao", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private TipoIntegracao tipoIntegracao;

    @Column(columnDefinition = "TEXT")
    private String logoUrl;

    @Column(name = "configuracao_json", columnDefinition = "JSONB")
    private String configuracaoJson;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Status status = Status.ATIVA;

    @Column(name = "ultima_sincronizacao")
    private LocalDateTime ultimaSincronizacao;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum TipoIntegracao {
        API, SCRAPING, WHITE_LABEL
    }

    public enum Status {
        ATIVA, INATIVA, MANUTENCAO
    }
}

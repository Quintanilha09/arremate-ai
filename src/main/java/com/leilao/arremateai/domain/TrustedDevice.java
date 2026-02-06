package com.leilao.arremateai.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa um dispositivo confiável para pular 2FA
 */
@Entity
@Table(name = "trusted_device")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrustedDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "device_token", nullable = false, unique = true)
    private String deviceToken;

    @Column(name = "fingerprint", length = 500)
    private String fingerprint;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_used_at", nullable = false)
    private LocalDateTime lastUsedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Builder.Default
    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.lastUsedAt = now;
        if (this.expiresAt == null) {
            // Padrão: 90 dias
            this.expiresAt = now.plusDays(90);
        }
    }

    /**
     * Verifica se o dispositivo ainda é válido
     */
    public boolean isValid() {
        return !revoked && LocalDateTime.now().isBefore(expiresAt);
    }

    /**
     * Atualiza o último uso do dispositivo
     */
    public void updateLastUsed() {
        this.lastUsedAt = LocalDateTime.now();
    }

    /**
     * Revoga o dispositivo
     */
    public void revoke() {
        this.revoked = true;
    }
}

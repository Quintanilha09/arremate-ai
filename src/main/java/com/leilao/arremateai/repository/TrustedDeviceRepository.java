package com.leilao.arremateai.repository;

import com.leilao.arremateai.domain.TrustedDevice;
import com.leilao.arremateai.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrustedDeviceRepository extends JpaRepository<TrustedDevice, UUID> {

    /**
     * Busca dispositivo por token
     */
    Optional<TrustedDevice> findByDeviceToken(String deviceToken);

    /**
     * Busca dispositivos válidos de um usuário
     */
    @Query("SELECT td FROM TrustedDevice td WHERE td.usuario.id = :usuarioId " +
           "AND td.revoked = false AND td.expiresAt > :now")
    List<TrustedDevice> findValidDevicesByUsuario(
        @Param("usuarioId") UUID usuarioId,
        @Param("now") LocalDateTime now
    );

    /**
     * Busca todos os dispositivos de um usuário
     */
    List<TrustedDevice> findByUsuarioOrderByLastUsedAtDesc(Usuario usuario);

    /**
     * Remove dispositivos expirados
     */
    @Modifying
    @Query("DELETE FROM TrustedDevice td WHERE td.expiresAt < :now")
    void deleteExpiredDevices(@Param("now") LocalDateTime now);

    /**
     * Remove dispositivos de um usuário
     */
    @Modifying
    void deleteByUsuario(Usuario usuario);

    /**
     * Conta dispositivos válidos de um usuário
     */
    @Query("SELECT COUNT(td) FROM TrustedDevice td WHERE td.usuario.id = :usuarioId " +
           "AND td.revoked = false AND td.expiresAt > :now")
    long countValidDevicesByUsuario(
        @Param("usuarioId") UUID usuarioId,
        @Param("now") LocalDateTime now
    );
}

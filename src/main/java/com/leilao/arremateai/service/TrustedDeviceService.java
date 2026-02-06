package com.leilao.arremateai.service;

import com.leilao.arremateai.domain.TrustedDevice;
import com.leilao.arremateai.domain.Usuario;
import com.leilao.arremateai.repository.TrustedDeviceRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrustedDeviceService {

    private final TrustedDeviceRepository trustedDeviceRepository;
    private static final int DEVICE_EXPIRATION_DAYS = 90;

    /**
     * Verifica se um device token é válido para um usuário
     */
    public boolean isDeviceTrusted(String deviceToken) {
        if (deviceToken == null || deviceToken.isBlank()) {
            return false;
        }

        return trustedDeviceRepository.findByDeviceToken(deviceToken)
                .map(device -> {
                    if (device.isValid()) {
                        // Atualiza último uso
                        device.updateLastUsed();
                        trustedDeviceRepository.save(device);
                        log.info("Dispositivo confiável válido, skip 2FA para usuário: {}", device.getUsuario().getId());
                        return true;
                    }
                    log.info("Dispositivo expirado ou revogado");
                    return false;
                })
                .orElse(false);
    }

    /**
     * Cria um novo dispositivo confiável
     */
    @Transactional
    public String trustDevice(Usuario usuario, HttpServletRequest request) {
        String deviceToken = generateDeviceToken();
        String fingerprint = generateFingerprint(request);
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = getClientIp(request);

        TrustedDevice device = TrustedDevice.builder()
                .usuario(usuario)
                .deviceToken(deviceToken)
                .fingerprint(fingerprint)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .expiresAt(LocalDateTime.now().plusDays(DEVICE_EXPIRATION_DAYS))
                .revoked(false)
                .build();

        trustedDeviceRepository.save(device);
        
        log.info("Dispositivo confiável criado para usuário {} - expira em {} dias", 
                usuario.getId(), DEVICE_EXPIRATION_DAYS);
        
        return deviceToken;
    }

    /**
     * Lista dispositivos de um usuário
     */
    public List<TrustedDevice> listUserDevices(Usuario usuario) {
        return trustedDeviceRepository.findByUsuarioOrderByLastUsedAtDesc(usuario);
    }

    /**
     * Revoga um dispositivo
     */
    @Transactional
    public void revokeDevice(UUID deviceId, Usuario usuario) {
        trustedDeviceRepository.findById(deviceId)
                .filter(device -> device.getUsuario().getId().equals(usuario.getId()))
                .ifPresent(device -> {
                    device.revoke();
                    trustedDeviceRepository.save(device);
                    log.info("Dispositivo {} revogado pelo usuário {}", deviceId, usuario.getId());
                });
    }

    /**
     * Revoga todos os dispositivos de um usuário
     */
    @Transactional
    public void revokeAllUserDevices(Usuario usuario) {
        List<TrustedDevice> devices = trustedDeviceRepository.findValidDevicesByUsuario(
                usuario.getId(), LocalDateTime.now());
        
        devices.forEach(TrustedDevice::revoke);
        trustedDeviceRepository.saveAll(devices);
        
        log.info("Todos os dispositivos do usuário {} foram revogados", usuario.getId());
    }

    /**
     * Limpeza automática de dispositivos expirados (executa diariamente)
     */
    @Scheduled(cron = "0 0 3 * * ?") // 3h da manhã
    @Transactional
    public void cleanupExpiredDevices() {
        log.info("Iniciando limpeza de dispositivos expirados");
        trustedDeviceRepository.deleteExpiredDevices(LocalDateTime.now());
        log.info("Limpeza de dispositivos concluída");
    }

    /**
     * Gera token único para o dispositivo
     */
    private String generateDeviceToken() {
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
    }

    /**
     * Gera fingerprint do dispositivo baseado em User-Agent e outros dados
     */
    private String generateFingerprint(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String acceptLanguage = request.getHeader("Accept-Language");
        String acceptEncoding = request.getHeader("Accept-Encoding");
        
        String data = String.format("%s|%s|%s", userAgent, acceptLanguage, acceptEncoding);
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Erro ao gerar fingerprint", e);
            return UUID.randomUUID().toString();
        }
    }

    /**
     * Obtém IP real do cliente (considera proxy/load balancer)
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Se tiver múltiplos IPs (proxy chain), pega o primeiro
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}

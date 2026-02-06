package com.leilao.arremateai.service;

import com.leilao.arremateai.domain.CodigoVerificacao;
import com.leilao.arremateai.repository.CodigoVerificacaoRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class VerificacaoService {

    private static final Logger logger = LoggerFactory.getLogger(VerificacaoService.class);
    private static final SecureRandom random = new SecureRandom();

    private final CodigoVerificacaoRepository codigoRepository;
    private final EmailService emailService;

    @Value("${app.2fa.code-expiration-minutes}")
    private int expirationMinutes;

    public VerificacaoService(
            CodigoVerificacaoRepository codigoRepository,
            EmailService emailService) {
        this.codigoRepository = codigoRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void enviarCodigoVerificacao(String email) {
        // Remove códigos anteriores não verificados
        codigoRepository.deleteByEmail(email);

        // Gera código de 6 dígitos
        String codigo = gerarCodigoAleatorio();

        // Cria novo código de verificação
        CodigoVerificacao codigoVerificacao = new CodigoVerificacao();
        codigoVerificacao.setEmail(email);
        codigoVerificacao.setCodigo(codigo);
        codigoVerificacao.setExpiresAt(LocalDateTime.now().plusMinutes(expirationMinutes));
        codigoVerificacao.setVerificado(false);

        codigoRepository.save(codigoVerificacao);

        // Envia email
        emailService.enviarCodigoVerificacao(email, codigo);

        logger.info("Código de verificação gerado para: {}", email);
    }

    @Transactional
    public boolean verificarCodigo(String email, String codigo) {
        var codigoOpt = codigoRepository.findByEmailAndCodigoAndVerificadoFalse(email, codigo);

        if (codigoOpt.isEmpty()) {
            logger.warn("Código inválido ou já usado para: {}", email);
            return false;
        }

        CodigoVerificacao codigoVerificacao = codigoOpt.orElseThrow(
            () -> new IllegalStateException("Código deveria estar presente após verificação")
        );

        if (codigoVerificacao.isExpired()) {
            logger.warn("Código expirado para: {}", email);
            return false;
        }

        // Marca como verificado
        codigoVerificacao.setVerificado(true);
        codigoVerificacao.setVerifiedAt(LocalDateTime.now());
        codigoRepository.save(codigoVerificacao);

        logger.info("Código verificado com sucesso para: {}", email);
        return true;
    }

    private String gerarCodigoAleatorio() {
        int codigo = 100000 + random.nextInt(900000);
        return String.valueOf(codigo);
    }

    @Transactional
    public void limparCodigosExpirados() {
        codigoRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        logger.info("Códigos expirados removidos");
    }
}

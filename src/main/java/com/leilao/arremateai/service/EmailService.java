package com.leilao.arremateai.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.2fa.from-email}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarCodigoVerificacao(String destinatario, String codigo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(destinatario);
            message.setSubject("ArremateAI - Código de Verificação");
            message.setText(construirMensagemCodigo(codigo));
            
            mailSender.send(message);
            logger.info("Email de verificação enviado para: {}", destinatario);
        } catch (Exception e) {
            logger.error("Erro ao enviar email para {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("Erro ao enviar email de verificação", e);
        }
    }

    private String construirMensagemCodigo(String codigo) {
        return String.format(
            """
            Olá!
            
            Seu código de verificação do ArremateAI é:
            
            %s
            
            Este código expira em 10 minutos.
            
            Se você não solicitou este código, ignore este email.
            
            Atenciosamente,
            Equipe ArremateAI
            """,
            codigo
        );
    }
}

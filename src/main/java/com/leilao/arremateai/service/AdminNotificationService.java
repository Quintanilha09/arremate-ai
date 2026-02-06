package com.leilao.arremateai.service;

import com.leilao.arremateai.domain.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class AdminNotificationService {
    
    private static final Logger log = LoggerFactory.getLogger(AdminNotificationService.class);
    
    private final JavaMailSender mailSender;
    
    @Value("${app.admin.email:admin@arremateai.com}")
    private String adminEmail;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    public AdminNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    /**
     * Notifica admin sobre novo vendedor aguardando aprovação
     */
    public void notificarNovoVendedor(Usuario vendedor) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(adminEmail);
            message.setSubject("🔔 Novo vendedor aguardando aprovação - ArremateAI");
            message.setText(construirMensagemNovoVendedor(vendedor));
            
            mailSender.send(message);
            log.info("E-mail de notificação enviado para admin: {}", adminEmail);
            
        } catch (Exception e) {
            log.error("Erro ao enviar e-mail para admin: {}", e.getMessage());
            // Não propaga exceção para não impedir cadastro do vendedor
        }
    }
    
    /**
     * Notifica vendedor sobre aprovação
     */
    public void notificarVendedorAprovado(Usuario vendedor) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(vendedor.getEmail());
            message.setSubject("✅ Sua conta foi aprovada - ArremateAI");
            message.setText(construirMensagemAprovacao(vendedor));
            
            mailSender.send(message);
            log.info("E-mail de aprovação enviado para: {}", vendedor.getEmail());
            
        } catch (Exception e) {
            log.error("Erro ao enviar e-mail de aprovação: {}", e.getMessage());
        }
    }
    
    /**
     * Notifica vendedor sobre rejeição
     */
    public void notificarVendedorRejeitado(Usuario vendedor, String motivo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(vendedor.getEmail());
            message.setSubject("❌ Sua conta não foi aprovada - ArremateAI");
            message.setText(construirMensagemRejeicao(vendedor, motivo));
            
            mailSender.send(message);
            log.info("E-mail de rejeição enviado para: {}", vendedor.getEmail());
            
        } catch (Exception e) {
            log.error("Erro ao enviar e-mail de rejeição: {}", e.getMessage());
        }
    }
    
    private String construirMensagemNovoVendedor(Usuario vendedor) {
        return String.format("""
            Olá Administrador,
            
            Um novo vendedor se cadastrou na plataforma e está aguardando aprovação:
            
            📋 DADOS DO VENDEDOR:
            Nome: %s
            %s
            E-mail: %s
            Status: PENDENTE_APROVACAO
            
            🔗 ACESSE O PAINEL ADMIN:
            %s/admin/vendedores
            
            Por favor, revise os dados e aprove ou rejeite este cadastro.
            
            ---
            ArremateAI - Plataforma de Leilões
            """,
            vendedor.getNome(),
            vendedor.getCnpj() != null ? "CNPJ: " + vendedor.getCnpj() : "CPF: " + vendedor.getCpf(),
            vendedor.getEmail(),
            frontendUrl
        );
    }
    
    private String construirMensagemAprovacao(Usuario vendedor) {
        return String.format("""
            Olá %s,
            
            Parabéns! Sua conta de vendedor foi aprovada! 🎉
            
            Agora você já pode começar a anunciar seus imóveis em leilão na plataforma ArremateAI.
            
            📍 PRÓXIMOS PASSOS:
            1. Acesse a plataforma: %s
            2. Faça login com suas credenciais
            3. Clique em "Anunciar" para cadastrar seu primeiro imóvel
            
            Dúvidas ou problemas?
            Envie um e-mail para suporte@arremateai.com
            
            Boas vendas!
            
            ---
            Equipe ArremateAI
            """,
            vendedor.getNome(),
            frontendUrl
        );
    }
    
    private String construirMensagemRejeicao(Usuario vendedor, String motivo) {
        return String.format("""
            Olá %s,
            
            Informamos que sua solicitação de cadastro como vendedor não foi aprovada.
            
            ❌ MOTIVO:
            %s
            
            📧 DÚVIDAS?
            Se você acredita que houve algum erro ou deseja mais informações,
            entre em contato conosco através do e-mail: suporte@arremateai.com
            
            Atenciosamente,
            
            ---
            Equipe ArremateAI
            """,
            vendedor.getNome(),
            motivo != null && !motivo.isEmpty() ? motivo : "Não especificado"
        );
    }
}

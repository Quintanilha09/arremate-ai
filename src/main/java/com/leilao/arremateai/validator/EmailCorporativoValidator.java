package com.leilao.arremateai.validator;

import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Valida se um email é corporativo (não é de provedor pessoal/gratuito)
 */
@Component
public class EmailCorporativoValidator {

    /**
     * Lista de domínios de email pessoal/gratuito que não são aceitos como corporativos
     */
    private static final Set<String> DOMINIOS_PESSOAIS = Set.of(
            // Gmail
            "gmail.com",
            "googlemail.com",
            
            // Microsoft
            "hotmail.com",
            "outlook.com",
            "live.com",
            "msn.com",
            
            // Yahoo
            "yahoo.com",
            "yahoo.com.br",
            "ymail.com",
            "rocketmail.com",
            
            // Apple
            "icloud.com",
            "me.com",
            "mac.com",
            
            // Outros provedores populares
            "aol.com",
            "protonmail.com",
            "proton.me",
            "zoho.com",
            "mail.com",
            "gmx.com",
            "gmx.net",
            "yandex.com",
            "yandex.ru",
            
            // Emails temporários/descartáveis
            "tempmail.com",
            "guerrillamail.com",
            "10minutemail.com",
            "throwaway.email",
            "mailinator.com",
            "maildrop.cc"
    );

    /**
     * Verifica se o email é de um provedor corporativo (não pessoal)
     *
     * @param email Email a ser validado
     * @return true se for email corporativo, false se for pessoal
     */
    public boolean isEmailCorporativo(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        int atIndex = email.indexOf('@');
        if (atIndex == -1 || atIndex == email.length() - 1) {
            return false;
        }

        String dominio = email.substring(atIndex + 1).toLowerCase().trim();
        
        return !DOMINIOS_PESSOAIS.contains(dominio);
    }

    /**
     * Valida email corporativo e lança exceção se for pessoal
     *
     * @param email Email a ser validado
     * @throws IllegalArgumentException se o email for de provedor pessoal
     */
    public void validarEmailCorporativo(String email) {
        if (!isEmailCorporativo(email)) {
            String dominio = email.substring(email.indexOf('@') + 1);
            throw new IllegalArgumentException(
                    "Email corporativo inválido. Use um email da sua empresa, não de provedores pessoais como " + dominio
            );
        }
    }
}

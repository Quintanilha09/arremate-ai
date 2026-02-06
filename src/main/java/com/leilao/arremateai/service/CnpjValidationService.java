package com.leilao.arremateai.service;

import com.leilao.arremateai.dto.CnpjResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CnpjValidationService {
    
    private static final Logger log = LoggerFactory.getLogger(CnpjValidationService.class);
    private static final String RECEITAWS_URL = "https://www.receitaws.com.br/v1/cnpj/";
    
    private final RestTemplate restTemplate;
    
    public CnpjValidationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Valida CNPJ via ReceitaWS API
     * 
     * @param cnpj CNPJ com ou sem máscara
     * @return Dados da empresa se CNPJ válido
     * @throws IllegalArgumentException se CNPJ inválido ou inativo
     */
    public CnpjResponseDTO validarCnpj(String cnpj) {
        try {
            // Remove máscara do CNPJ
            String cnpjLimpo = cnpj.replaceAll("\\D", "");
            
            log.info("Validando CNPJ: {}", cnpjLimpo);
            
            // Validação básica de tamanho
            if (cnpjLimpo.length() != 14) {
                throw new IllegalArgumentException("CNPJ deve conter 14 dígitos");
            }
            
            // Consulta API ReceitaWS
            String url = RECEITAWS_URL + cnpjLimpo;
            CnpjResponseDTO response = restTemplate.getForObject(url, CnpjResponseDTO.class);
            
            if (response == null) {
                throw new IllegalArgumentException("Erro ao consultar CNPJ. Tente novamente mais tarde");
            }
            
            // Verifica se CNPJ existe e está ativo
            if (!response.isAtivo()) {
                throw new IllegalArgumentException(
                    "CNPJ com situação cadastral: " + response.getSituacao() + 
                    ". Apenas empresas ativas podem se cadastrar"
                );
            }
            
            log.info("CNPJ validado com sucesso: {} - {}", 
                response.getCnpj(), response.getRazaoSocial());
            
            return response;
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro ao validar CNPJ: {}", e.getMessage());
            throw new IllegalArgumentException(
                "Não foi possível validar o CNPJ. Verifique se o número está correto e tente novamente"
            );
        }
    }
    
    /**
     * Valida apenas se CNPJ está no formato correto (sem consultar API)
     */
    public boolean isCnpjFormatoValido(String cnpj) {
        if (cnpj == null) return false;
        
        String cnpjLimpo = cnpj.replaceAll("\\D", "");
        
        // Verifica se tem 14 dígitos
        if (cnpjLimpo.length() != 14) return false;
        
        // Verifica se não são todos iguais (00000000000000, 11111111111111, etc)
        if (cnpjLimpo.matches("(\\d)\\1{13}")) return false;
        
        // Validação dos dígitos verificadores
        try {
            int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            
            int soma1 = 0;
            for (int i = 0; i < 12; i++) {
                soma1 += Character.getNumericValue(cnpjLimpo.charAt(i)) * pesos1[i];
            }
            int digito1 = soma1 % 11 < 2 ? 0 : 11 - (soma1 % 11);
            
            int soma2 = 0;
            for (int i = 0; i < 13; i++) {
                soma2 += Character.getNumericValue(cnpjLimpo.charAt(i)) * pesos2[i];
            }
            int digito2 = soma2 % 11 < 2 ? 0 : 11 - (soma2 % 11);
            
            return Character.getNumericValue(cnpjLimpo.charAt(12)) == digito1 &&
                   Character.getNumericValue(cnpjLimpo.charAt(13)) == digito2;
                   
        } catch (Exception e) {
            return false;
        }
    }
}

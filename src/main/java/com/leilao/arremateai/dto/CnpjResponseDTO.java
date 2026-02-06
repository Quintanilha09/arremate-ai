package com.leilao.arremateai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CnpjResponseDTO {
    
    private String cnpj;
    
    @JsonProperty("razao_social")
    private String razaoSocial;
    
    @JsonProperty("nome_fantasia")
    private String nomeFantasia;
    
    private String situacao;
    
    @JsonProperty("data_situacao")
    private String dataSituacao;
    
    private String uf;
    private String municipio;
    private String bairro;
    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private String email;
    private String telefone;
    
    @JsonProperty("atividade_principal")
    private List<AtividadeEconomica> atividadePrincipal;
    
    @Data
    public static class AtividadeEconomica {
        private String code;
        private String text;
    }
    
    /**
     * Verifica se o CNPJ está ativo
     */
    public boolean isAtivo() {
        return "ATIVA".equalsIgnoreCase(this.situacao);
    }
    
    /**
     * Retorna endereço formatado
     */
    public String getEnderecoCompleto() {
        StringBuilder sb = new StringBuilder();
        
        if (logradouro != null) sb.append(logradouro);
        if (numero != null) sb.append(", ").append(numero);
        if (complemento != null && !complemento.isEmpty()) sb.append(" - ").append(complemento);
        if (bairro != null) sb.append(" - ").append(bairro);
        if (municipio != null) sb.append(" - ").append(municipio);
        if (uf != null) sb.append("/").append(uf);
        if (cep != null) sb.append(" - CEP: ").append(cep);
        
        return sb.toString();
    }
}

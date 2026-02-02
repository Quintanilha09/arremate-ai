package com.leilao.arremateai.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImovelRequest {
    
    @NotBlank(message = "Número do leilão é obrigatório")
    @Size(max = 100, message = "Número do leilão deve ter no máximo 100 caracteres")
    private String numeroLeilao;
    
    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String descricao;
    
    @NotNull(message = "Valor de avaliação é obrigatório")
    @Positive(message = "Valor de avaliação deve ser positivo")
    private BigDecimal valorAvaliacao;
    
    @NotBlank(message = "Data do leilão é obrigatória")
    private String dataLeilao;
    
    @NotBlank(message = "UF é obrigatória")
    @Size(min = 2, max = 2, message = "UF deve ter exatamente 2 caracteres")
    @Pattern(regexp = "[A-Z]{2}", message = "UF deve conter apenas letras maiúsculas")
    private String uf;
    
    @NotBlank(message = "Instituição é obrigatória")
    @Size(max = 300, message = "Instituição deve ter no máximo 300 caracteres")
    private String instituicao;
    
    @Size(max = 1000, message = "Link do edital deve ter no máximo 1000 caracteres")
    private String linkEdital;
    
    @Size(max = 100, message = "Cidade deve ter no máximo 100 caracteres")
    private String cidade;
    
    @Size(max = 200, message = "Bairro deve ter no máximo 200 caracteres")
    private String bairro;
    
    @Positive(message = "Área total deve ser positiva")
    private BigDecimal areaTotal;
    
    @Size(max = 50, message = "Tipo de imóvel deve ter no máximo 50 caracteres")
    private String tipoImovel;
}

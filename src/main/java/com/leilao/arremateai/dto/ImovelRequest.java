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
    
    // Para POST: todos os campos marcados com @NotBlank/@NotNull são obrigatórios
    // Para PATCH: todos os campos são opcionais (atualização parcial)
    // Para PUT: validações aplicadas normalmente
    
    @NotBlank(message = "Número do leilão é obrigatório", groups = OnCreate.class)
    @Size(max = 100, message = "Número do leilão deve ter no máximo 100 caracteres")
    private String numeroLeilao;
    
    @NotBlank(message = "Descrição é obrigatória", groups = OnCreate.class)
    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String descricao;
    
    @NotNull(message = "Valor de avaliação é obrigatório", groups = OnCreate.class)
    @Positive(message = "Valor de avaliação deve ser positivo")
    private BigDecimal valorAvaliacao;
    
    @NotBlank(message = "Data do leilão é obrigatória", groups = OnCreate.class)
    private String dataLeilao;
    
    @NotBlank(message = "UF é obrigatória", groups = OnCreate.class)
    @Size(min = 2, max = 2, message = "UF deve ter exatamente 2 caracteres")
    @Pattern(regexp = "[A-Z]{2}", message = "UF deve conter apenas letras maiúsculas")
    private String uf;
    
    @NotBlank(message = "Instituição é obrigatória", groups = OnCreate.class)
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

    // Informações essenciais
    @Min(value = 0, message = "Número de quartos não pode ser negativo")
    private Integer quartos;

    @Min(value = 0, message = "Número de banheiros não pode ser negativo")
    private Integer banheiros;

    @Min(value = 0, message = "Número de vagas não pode ser negativo")
    private Integer vagas;

    @Size(max = 500, message = "Endereço deve ter no máximo 500 caracteres")
    private String endereco;

    @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido. Formato esperado: 12345-678 ou 12345678")
    @Size(max = 10, message = "CEP deve ter no máximo 10 caracteres")
    private String cep;

    @DecimalMin(value = "-90.0", message = "Latitude deve estar entre -90 e 90")
    @DecimalMax(value = "90.0", message = "Latitude deve estar entre -90 e 90")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Longitude deve estar entre -180 e 180")
    @DecimalMax(value = "180.0", message = "Longitude deve estar entre -180 e 180")
    private BigDecimal longitude;

    // Informações adicionais
    @Pattern(regexp = "NOVO|USADO|REFORMADO", message = "Condição deve ser NOVO, USADO ou REFORMADO")
    private String condicao;

    private Boolean aceitaFinanciamento;

    @Size(max = 2000, message = "Observações devem ter no máximo 2000 caracteres")
    private String observacoes;

    @Pattern(regexp = "DISPONIVEL|VENDIDO|SUSPENSO", message = "Status deve ser DISPONIVEL, VENDIDO ou SUSPENSO")
    private String status;

    // Grupos de validação
    public interface OnCreate {}
    public interface OnUpdate {}
}

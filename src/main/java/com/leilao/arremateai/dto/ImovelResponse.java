package com.leilao.arremateai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImovelResponse {
    
    private UUID id;
    private String numeroLeilao;
    private String descricao;
    private BigDecimal valorAvaliacao;
    private String dataLeilao;
    private String uf;
    private String instituicao;
    private String linkEdital;
    private String cidade;
    private String bairro;
    private BigDecimal areaTotal;
    private String tipoImovel;
    
    // Informações essenciais
    private Integer quartos;
    private Integer banheiros;
    private Integer vagas;
    private String endereco;
    private String cep;
    private BigDecimal latitude;
    private BigDecimal longitude;
    
    // Informações adicionais
    private String condicao;
    private Boolean aceitaFinanciamento;
    private String observacoes;
    private String status;
    
    // Imagens
    private List<ImagemResponse> imagens;
    private String imagemPrincipal; // URL da imagem principal
}


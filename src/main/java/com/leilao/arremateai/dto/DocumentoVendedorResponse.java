package com.leilao.arremateai.dto;

import com.leilao.arremateai.domain.StatusDocumento;
import com.leilao.arremateai.domain.TipoDocumento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response com dados do documento do vendedor
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentoVendedorResponse {
    
    private UUID id;
    private TipoDocumento tipo;
    private String tipoDescricao;
    private String nomeArquivo;
    private String url;
    private Long tamanhoBytes;
    private String mimeType;
    private StatusDocumento status;
    private String motivoRejeicao;
    private String nomeAnalisadoPor;
    private LocalDateTime analisadoEm;
    private LocalDateTime createdAt;
}

package com.leilao.arremateai.dto;

import com.leilao.arremateai.domain.StatusDocumento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request para atualizar status de um documento
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtualizarStatusDocumentoRequest {
    
    @NotBlank(message = "Status é obrigatório")
    private StatusDocumento status;
    
    @Size(max = 1000, message = "Motivo deve ter no máximo 1000 caracteres")
    private String motivo;
}

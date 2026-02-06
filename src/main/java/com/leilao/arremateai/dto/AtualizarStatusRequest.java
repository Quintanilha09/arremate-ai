package com.leilao.arremateai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AtualizarStatusRequest {

    @NotBlank(message = "Status é obrigatório")
    @Pattern(
        regexp = "DISPONIVEL|SUSPENSO|VENDIDO", 
        message = "Status deve ser DISPONIVEL, SUSPENSO ou VENDIDO"
    )
    private String status;
}

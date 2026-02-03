package com.leilao.arremateai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImagemResponse {
    
    private UUID id;
    private String url;
    private String legenda;
    private Boolean principal;
    private Integer ordem;
}

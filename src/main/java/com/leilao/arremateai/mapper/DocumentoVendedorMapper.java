package com.leilao.arremateai.mapper;

import com.leilao.arremateai.domain.DocumentoVendedor;
import com.leilao.arremateai.dto.DocumentoVendedorResponse;
import org.springframework.stereotype.Component;

@Component
public class DocumentoVendedorMapper {
    
    /**
     * Converte DocumentoVendedor para DocumentoVendedorResponse
     */
    public DocumentoVendedorResponse paraResponse(DocumentoVendedor documento) {
        if (documento == null) {
            return null;
        }
        
        return DocumentoVendedorResponse.builder()
                .id(documento.getId())
                .tipo(documento.getTipo())
                .tipoDescricao(documento.getTipo().getDescricao())
                .nomeArquivo(documento.getNomeArquivo())
                .url(documento.getUrl())
                .tamanhoBytes(documento.getTamanhoBytes())
                .mimeType(documento.getMimeType())
                .status(documento.getStatus())
                .motivoRejeicao(documento.getMotivoRejeicao())
                .nomeAnalisadoPor(documento.getAnalisadoPor() != null ? documento.getAnalisadoPor().getNome() : null)
                .analisadoEm(documento.getAnalisadoEm())
                .createdAt(documento.getCreatedAt())
                .build();
    }
}

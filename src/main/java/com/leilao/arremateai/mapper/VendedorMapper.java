package com.leilao.arremateai.mapper;

import com.leilao.arremateai.domain.Usuario;
import com.leilao.arremateai.dto.VendedorResponse;
import org.springframework.stereotype.Component;

@Component
public class VendedorMapper {
    
    /**
     * Converte Usuario para VendedorResponse
     */
    public VendedorResponse paraResponse(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        
        return VendedorResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .cnpj(usuario.getCnpj())
                .razaoSocial(usuario.getRazaoSocial())
                .nomeFantasia(usuario.getNomeFantasia())
                .inscricaoEstadual(usuario.getInscricaoEstadual())
                .emailCorporativo(usuario.getEmailCorporativo())
                .emailCorporativoVerificado(usuario.getEmailCorporativoVerificado())
                .statusVendedor(usuario.getStatusVendedor())
                .motivoRejeicao(usuario.getMotivoRejeicao())
                .aprovadoEm(usuario.getAprovadoEm())
                .nomeAprovadoPor(usuario.getAprovadoPor() != null ? usuario.getAprovadoPor().getNome() : null)
                .createdAt(usuario.getCreatedAt())
                .build();
    }
}

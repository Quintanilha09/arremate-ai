package com.leilao.arremateai.mapper;

import com.leilao.arremateai.client.dto.LeilaoExternoDTO;
import com.leilao.arremateai.domain.Imovel;
import com.leilao.arremateai.dto.ImovelRequest;
import com.leilao.arremateai.dto.ImovelResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ImovelMapper {

    public ImovelResponse paraResponse(Imovel imovel) {
        return ImovelResponse.builder()
            .numeroLeilao(imovel.getNumeroLeilao())
            .descricao(imovel.getDescricao())
            .valorAvaliacao(imovel.getValorAvaliacao())
            .dataLeilao(imovel.getDataLeilao().toString())
            .uf(imovel.getUf())
            .instituicao(imovel.getInstituicao())
            .linkEdital(imovel.getLinkEdital())
            .build();
    }

    public ImovelResponse paraResponse(LeilaoExternoDTO leilaoExterno) {
        return ImovelResponse.builder()
            .numeroLeilao(leilaoExterno.getNumeroLeilao())
            .descricao(leilaoExterno.getDescricao())
            .valorAvaliacao(leilaoExterno.getValorAvaliacao())
            .dataLeilao(leilaoExterno.getDataLeilao())
            .uf(leilaoExterno.getUf())
            .instituicao(leilaoExterno.getInstituicao())
            .linkEdital(leilaoExterno.getLinkEdital())
            .build();
    }

    public Imovel paraEntidade(ImovelRequest request) {
        var imovel = new Imovel();
        imovel.setNumeroLeilao(request.getNumeroLeilao());
        imovel.setDescricao(request.getDescricao());
        imovel.setValorAvaliacao(request.getValorAvaliacao());
        imovel.setDataLeilao(LocalDate.parse(request.getDataLeilao()));
        imovel.setUf(request.getUf());
        imovel.setInstituicao(request.getInstituicao());
        imovel.setLinkEdital(request.getLinkEdital());
        imovel.setCidade(request.getCidade());
        imovel.setBairro(request.getBairro());
        imovel.setAreaTotal(request.getAreaTotal());
        imovel.setTipoImovel(request.getTipoImovel());
        return imovel;
    }
}

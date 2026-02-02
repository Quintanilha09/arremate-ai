package com.leilao.arremateai.mapper;

import com.leilao.arremateai.client.dto.LeilaoExternoDTO;
import com.leilao.arremateai.domain.ImagemImovel;
import com.leilao.arremateai.domain.Imovel;
import com.leilao.arremateai.dto.ImagemResponse;
import com.leilao.arremateai.dto.ImovelRequest;
import com.leilao.arremateai.dto.ImovelResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
public class ImovelMapper {

    public ImovelResponse paraResponse(Imovel imovel) {
        var response = ImovelResponse.builder()
            .id(imovel.getId())
            .numeroLeilao(imovel.getNumeroLeilao())
            .descricao(imovel.getDescricao())
            .valorAvaliacao(imovel.getValorAvaliacao())
            .dataLeilao(imovel.getDataLeilao().toString())
            .uf(imovel.getUf())
            .instituicao(imovel.getInstituicao())
            .linkEdital(imovel.getLinkEdital())
            .cidade(imovel.getCidade())
            .bairro(imovel.getBairro())
            .areaTotal(imovel.getAreaTotal())
            .tipoImovel(imovel.getTipoImovel())
            .quartos(imovel.getQuartos())
            .banheiros(imovel.getBanheiros())
            .vagas(imovel.getVagas())
            .endereco(imovel.getEndereco())
            .cep(imovel.getCep())
            .latitude(imovel.getLatitude())
            .longitude(imovel.getLongitude())
            .condicao(imovel.getCondicao())
            .aceitaFinanciamento(imovel.getAceitaFinanciamento())
            .observacoes(imovel.getObservacoes())
            .status(imovel.getStatus())
            .build();

        // Mapear imagens se existirem
        if (imovel.getImagens() != null && !imovel.getImagens().isEmpty()) {
            response.setImagens(imovel.getImagens().stream()
                .map(this::paraImagemResponse)
                .collect(Collectors.toList()));

            // Definir imagem principal
            imovel.getImagens().stream()
                .filter(img -> Boolean.TRUE.equals(img.getPrincipal()))
                .findFirst()
                .ifPresent(img -> response.setImagemPrincipal(img.getUrl()));
        }

        return response;
    }

    public ImagemResponse paraImagemResponse(ImagemImovel imagem) {
        return ImagemResponse.builder()
            .id(imagem.getId())
            .url(imagem.getUrl())
            .legenda(imagem.getLegenda())
            .principal(imagem.getPrincipal())
            .ordem(imagem.getOrdem())
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
        imovel.setQuartos(request.getQuartos());
        imovel.setBanheiros(request.getBanheiros());
        imovel.setVagas(request.getVagas());
        imovel.setEndereco(request.getEndereco());
        imovel.setCep(request.getCep());
        imovel.setLatitude(request.getLatitude());
        imovel.setLongitude(request.getLongitude());
        imovel.setCondicao(request.getCondicao());
        imovel.setAceitaFinanciamento(request.getAceitaFinanciamento());
        imovel.setObservacoes(request.getObservacoes());
        imovel.setStatus(request.getStatus() != null ? request.getStatus() : "DISPONIVEL");
        return imovel;
    }
}

package com.leilao.arremateai.service;

import com.leilao.arremateai.client.LeilaoPublicoClient;
import com.leilao.arremateai.domain.Imovel;
import com.leilao.arremateai.dto.ImovelRequest;
import com.leilao.arremateai.dto.ImovelResponse;
import com.leilao.arremateai.repository.ImovelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ImovelService {

    private static final Logger log = LoggerFactory.getLogger(ImovelService.class);
    private final LeilaoPublicoClient leilaoClient;
    private final ImovelRepository imovelRepository;

    public ImovelService(LeilaoPublicoClient leilaoClient, ImovelRepository imovelRepository) {
        this.leilaoClient = leilaoClient;
        this.imovelRepository = imovelRepository;
    }

    public Page<ImovelResponse> buscarComFiltros(
            String uf,
            String cidade,
            String tipoImovel,
            String instituicao,
            BigDecimal valorMin,
            BigDecimal valorMax,
            Pageable pageable
    ) {
        log.info("Buscando imóveis com filtros: uf={}, cidade={}, tipo={}, instituicao={}, valorMin={}, valorMax={}",
                uf, cidade, tipoImovel, instituicao, valorMin, valorMax);

        Specification<Imovel> spec = Specification.where(null);

        if (uf != null && !uf.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.upper(root.get("uf")), uf.toUpperCase()));
        }

        if (cidade != null && !cidade.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.upper(root.get("cidade")), "%" + cidade.toUpperCase() + "%"));
        }

        if (tipoImovel != null && !tipoImovel.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.upper(root.get("tipoImovel")), "%" + tipoImovel.toUpperCase() + "%"));
        }

        if (instituicao != null && !instituicao.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.upper(root.get("instituicao")), "%" + instituicao.toUpperCase() + "%"));
        }

        if (valorMin != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("valorAvaliacao"), valorMin));
        }

        if (valorMax != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("valorAvaliacao"), valorMax));
        }

        Page<Imovel> imoveis = imovelRepository.findAll(spec, pageable);
        return imoveis.map(this::converterParaResponse);
    }

    public List<ImovelResponse> buscarTodosImoveis() {
        log.info("Buscando imóveis cadastrados no banco de dados");
        
        var imoveisBanco = imovelRepository.findAll();
        
        if (!imoveisBanco.isEmpty()) {
            log.info("Encontrados {} imóveis no banco de dados", imoveisBanco.size());
            return imoveisBanco.stream()
                .map(this::converterParaResponse)
                .toList();
        }
        
        log.info("Nenhum imóvel no banco. Buscando de fontes externas");
        var leiloesExternos = leilaoClient.buscarImoveisEmLeilao();
        
        return leiloesExternos.stream()
            .map(leilao -> new ImovelResponse(
                leilao.numeroLeilao(),
                leilao.descricao(),
                leilao.valorAvaliacao(),
                leilao.dataLeilao(),
                leilao.uf(),
                leilao.instituicao(),
                leilao.linkEdital()
            ))
            .toList();
    }

    @Transactional
    public ImovelResponse cadastrarImovel(ImovelRequest request) {
        log.info("Cadastrando imóvel: {}", request.numeroLeilao());
        
        if (imovelRepository.existsByNumeroLeilao(request.numeroLeilao())) {
            throw new IllegalArgumentException("Imóvel com número de leilão " + request.numeroLeilao() + " já existe");
        }
        
        var imovel = new Imovel();
        imovel.setNumeroLeilao(request.numeroLeilao());
        imovel.setDescricao(request.descricao());
        imovel.setValorAvaliacao(request.valorAvaliacao());
        imovel.setDataLeilao(LocalDate.parse(request.dataLeilao()));
        imovel.setUf(request.uf());
        imovel.setInstituicao(request.instituicao());
        imovel.setLinkEdital(request.linkEdital());
        imovel.setCidade(request.cidade());
        imovel.setBairro(request.bairro());
        imovel.setAreaTotal(request.areaTotal());
        imovel.setTipoImovel(request.tipoImovel());
        
        var salvo = imovelRepository.save(imovel);
        log.info("Imóvel cadastrado com ID: {}", salvo.getId());
        
        return converterParaResponse(salvo);
    }

    private ImovelResponse converterParaResponse(Imovel imovel) {
        return new ImovelResponse(
            imovel.getNumeroLeilao(),
            imovel.getDescricao(),
            imovel.getValorAvaliacao(),
            imovel.getDataLeilao().toString(),
            imovel.getUf(),
            imovel.getInstituicao(),
            imovel.getLinkEdital()
        );
    }
}

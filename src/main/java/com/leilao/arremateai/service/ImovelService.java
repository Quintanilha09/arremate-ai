package com.leilao.arremateai.service;

import com.leilao.arremateai.client.LeilaoPublicoClient;
import com.leilao.arremateai.domain.Imovel;
import com.leilao.arremateai.dto.ImovelRequest;
import com.leilao.arremateai.dto.ImovelResponse;
import com.leilao.arremateai.mapper.ImovelMapper;
import com.leilao.arremateai.repository.ImovelRepository;
import com.leilao.arremateai.specification.ImovelSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ImovelService {

    private static final Logger log = LoggerFactory.getLogger(ImovelService.class);
    private final LeilaoPublicoClient leilaoClient;
    private final ImovelRepository imovelRepository;
    private final ImovelMapper imovelMapper;

    public ImovelService(
        LeilaoPublicoClient leilaoClient,
        ImovelRepository imovelRepository,
        ImovelMapper imovelMapper
    ) {
        this.leilaoClient = leilaoClient;
        this.imovelRepository = imovelRepository;
        this.imovelMapper = imovelMapper;
    }

    public Page<ImovelResponse> buscarComFiltros(
            String uf,
            String cidade,
            String tipoImovel,
            String instituicao,
            BigDecimal valorMin,
            BigDecimal valorMax,
            String busca,
            Pageable pageable
    ) {
        log.info("Buscando imóveis com filtros aplicados");

        Specification<Imovel> specification = ImovelSpecifications.builder()
            .comUf(uf)
            .comCidade(cidade)
            .comTipoImovel(tipoImovel)
            .comInstituicao(instituicao)
            .comValorMinimo(valorMin)
            .comValorMaximo(valorMax)
            .comBuscaTexto(busca)
            .construir();

        return imovelRepository.findAll(specification, pageable)
            .map(imovelMapper::paraResponse);
    }

    public List<ImovelResponse> buscarTodosImoveis() {
        log.info("Buscando imóveis cadastrados no banco de dados");

        var imoveisBanco = imovelRepository.findAll();

        return imoveisBanco.isEmpty()
            ? buscarImoveisExternos()
            : converterParaResponses(imoveisBanco);
    }

    private List<ImovelResponse> converterParaResponses(List<Imovel> imoveis) {
        log.info("Encontrados {} imóveis no banco de dados", imoveis.size());
        return imoveis.stream()
            .map(imovelMapper::paraResponse)
            .toList();
    }

    private List<ImovelResponse> buscarImoveisExternos() {
        log.info("Nenhum imóvel no banco. Buscando de fontes externas");
        return leilaoClient.buscarImoveisEmLeilao().stream()
            .map(imovelMapper::paraResponse)
            .toList();
    }

    public ImovelResponse buscarPorId(Long id) {
        log.info("Buscando imóvel por ID: {}", id);

        return imovelRepository.findById(id)
            .map(imovelMapper::paraResponse)
            .orElseThrow(() -> new IllegalArgumentException("Imóvel não encontrado com ID: " + id));
    }

    @Transactional
    public ImovelResponse cadastrarImovel(ImovelRequest request) {
        log.info("Cadastrando imóvel: {}", request.getNumeroLeilao());

        validarImovelNaoDuplicado(request.getNumeroLeilao());

        var imovelSalvo = imovelRepository.save(imovelMapper.paraEntidade(request));
        log.info("Imóvel cadastrado com ID: {}", imovelSalvo.getId());

        return imovelMapper.paraResponse(imovelSalvo);
    }

    private void validarImovelNaoDuplicado(String numeroLeilao) {
        if (imovelRepository.existsByNumeroLeilao(numeroLeilao)) {
            throw new IllegalArgumentException(
                "Imóvel com número de leilão " + numeroLeilao + " já existe"
            );
        }
    }
}

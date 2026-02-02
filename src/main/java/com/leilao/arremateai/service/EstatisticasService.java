package com.leilao.arremateai.service;

import com.leilao.arremateai.domain.Imovel;
import com.leilao.arremateai.dto.EstatisticasResponse;
import com.leilao.arremateai.dto.ImovelResponse;
import com.leilao.arremateai.mapper.ImovelMapper;
import com.leilao.arremateai.repository.ImovelRepository;
import com.leilao.arremateai.specification.ImovelSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class EstatisticasService {

    private final ImovelRepository imovelRepository;
    private final ImovelMapper imovelMapper;

    public EstatisticasService(ImovelRepository imovelRepository, ImovelMapper imovelMapper) {
        this.imovelRepository = imovelRepository;
        this.imovelMapper = imovelMapper;
    }

    public EstatisticasResponse obterEstatisticas() {
        List<Imovel> todosImoveis = imovelRepository.findAll();
        List<Imovel> imoveisAtivos = todosImoveis.stream()
                .filter(Imovel::getAtivo)
                .toList();

        // Totais
        long totalImoveis = todosImoveis.size();
        long totalAtivos = imoveisAtivos.size();
        long totalInativos = totalImoveis - totalAtivos;

        // Agrupamentos por UF
        Map<String, Long> totalPorUf = imoveisAtivos.stream()
                .collect(Collectors.groupingBy(Imovel::getUf, Collectors.counting()));

        // Agrupamentos por Cidade
        Map<String, Long> totalPorCidade = imoveisAtivos.stream()
                .collect(Collectors.groupingBy(Imovel::getCidade, Collectors.counting()));

        // Agrupamentos por Tipo
        Map<String, Long> totalPorTipo = imoveisAtivos.stream()
                .collect(Collectors.groupingBy(Imovel::getTipoImovel, Collectors.counting()));

        // Agrupamentos por Instituição
        Map<String, Long> totalPorInstituicao = imoveisAtivos.stream()
                .collect(Collectors.groupingBy(Imovel::getInstituicao, Collectors.counting()));

        // Agrupamentos por Status
        Map<String, Long> totalPorStatus = imoveisAtivos.stream()
                .collect(Collectors.groupingBy(Imovel::getStatus, Collectors.counting()));

        // Cálculos de valores
        BigDecimal valorTotal = imoveisAtivos.stream()
                .map(Imovel::getValorAvaliacao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorMedio = totalAtivos > 0
                ? valorTotal.divide(BigDecimal.valueOf(totalAtivos), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal valorMinimo = imoveisAtivos.stream()
                .map(Imovel::getValorAvaliacao)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal valorMaximo = imoveisAtivos.stream()
                .map(Imovel::getValorAvaliacao)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // Estatísticas de imagens
        long imoveisComImagem = imoveisAtivos.stream()
                .filter(i -> i.getImagens() != null && !i.getImagens().isEmpty())
                .count();

        long imoveisSemImagem = totalAtivos - imoveisComImagem;

        double percentualComImagem = totalAtivos > 0
                ? (imoveisComImagem * 100.0) / totalAtivos
                : 0.0;

        return new EstatisticasResponse(
                totalImoveis,
                totalAtivos,
                totalInativos,
                totalPorUf,
                totalPorCidade,
                totalPorTipo,
                totalPorInstituicao,
                totalPorStatus,
                valorMedio,
                valorMinimo,
                valorMaximo,
                valorTotal,
                imoveisComImagem,
                imoveisSemImagem,
                Math.round(percentualComImagem * 100.0) / 100.0
        );
    }

    public List<ImovelResponse> obterDestaques(Integer limite) {
        int limiteReal = limite != null && limite > 0 ? limite : 5;

        Specification<Imovel> apenasAtivos = ImovelSpecifications.apenasAtivos();
        return imovelRepository.findAll(apenasAtivos).stream()
                .filter(i -> i.getImagens() != null && !i.getImagens().isEmpty())
                .sorted(Comparator.comparing(Imovel::getValorAvaliacao).reversed())
                .limit(limiteReal)
                .map(imovelMapper::paraResponse)
                .toList();
    }

    public List<ImovelResponse> obterRecentes(Integer limite) {
        int limiteReal = limite != null && limite > 0 ? limite : 10;

        Specification<Imovel> apenasAtivos = ImovelSpecifications.apenasAtivos();
        return imovelRepository.findAll(apenasAtivos).stream()
                .sorted(Comparator.comparing(Imovel::getCreatedAt).reversed())
                .limit(limiteReal)
                .map(imovelMapper::paraResponse)
                .toList();
    }

    public List<ImovelResponse> obterMaisProcurados(Integer limite) {
        int limiteReal = limite != null && limite > 0 ? limite : 5;

        Specification<Imovel> apenasAtivos = ImovelSpecifications.apenasAtivos();
        return imovelRepository.findAll(apenasAtivos).stream()
                .filter(i -> i.getImagens() != null && !i.getImagens().isEmpty())
                .sorted(Comparator
                        .comparing((Imovel i) -> i.getImagens().size()).reversed()
                        .thenComparing(Imovel::getValorAvaliacao))
                .limit(limiteReal)
                .map(imovelMapper::paraResponse)
                .toList();
    }
}

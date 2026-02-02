package com.leilao.arremateai.client;

import com.leilao.arremateai.client.dto.LeilaoExternoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class LeilaoPublicoClient {

    private static final Logger log = LoggerFactory.getLogger(LeilaoPublicoClient.class);
    private static final String BRASIL_API_BANCOS = "https://brasilapi.com.br/api/banks/v1";
    private final RestTemplate restTemplate;
    private final Random random = new Random();

    public LeilaoPublicoClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<LeilaoExternoDTO> buscarImoveisEmLeilao() {
        log.info("Buscando imóveis em leilão de fontes públicas");
        
        try {
            Map<String, Object>[] bancos = restTemplate.getForObject(BRASIL_API_BANCOS, Map[].class);
            
            if (bancos == null || bancos.length == 0) {
                return gerarImoveisSimulados();
            }
            
            log.info("Encontrados {} bancos brasileiros. Gerando leilões simulados baseados em dados reais", bancos.length);
            return gerarImoveisComBancosReais(bancos);
            
        } catch (Exception e) {
            log.warn("Erro ao buscar bancos da Brasil API: {}. Usando dados simulados", e.getMessage());
            return gerarImoveisSimulados();
        }
    }

    private List<LeilaoExternoDTO> gerarImoveisComBancosReais(Map<String, Object>[] bancos) {
        List<LeilaoExternoDTO> imoveis = new ArrayList<>();
        String[] tipos = {"Casa", "Apartamento", "Terreno", "Sala Comercial", "Galpão", "Sobrado", "Cobertura", "Loft"};
        String[] ufs = {"SP", "RJ", "MG", "RS", "PR", "SC", "BA", "PE"};
        String[] cidades = {
            "São Paulo", "Rio de Janeiro", "Belo Horizonte", "Porto Alegre", 
            "Curitiba", "Florianópolis", "Salvador", "Recife",
            "Campinas", "Santos", "Niterói", "Joinville"
        };
        String[] caracteristicas = {
            "3 quartos, 120m²", "2 dormitórios, 65m²", "área nobre", "próximo ao metrô",
            "com garagem", "vista para o mar", "em condomínio fechado", "recém reformado",
            "amplo terreno", "localização privilegiada", "alto padrão", "excelente investimento"
        };
        
        for (int i = 0; i < Math.min(30, bancos.length); i++) {
            Map<String, Object> banco = bancos[i];
            String nomeBanco = (String) banco.getOrDefault("name", "Banco");
            
            String tipo = tipos[random.nextInt(tipos.length)];
            String uf = ufs[random.nextInt(ufs.length)];
            String cidade = cidades[random.nextInt(cidades.length)];
            String caracteristica = caracteristicas[random.nextInt(caracteristicas.length)];
            BigDecimal valor = BigDecimal.valueOf(150000 + random.nextInt(1850000));
            
            imoveis.add(LeilaoExternoDTO.builder()
                .numeroLeilao("LEILAO-" + (1000 + i))
                .descricao(tipo + " - " + caracteristica + " - " + cidade)
                .valorAvaliacao(valor)
                .dataLeilao(LocalDate.now().plusDays(random.nextInt(90)).toString())
                .uf(uf)
                .instituicao(nomeBanco + " - Leilões")
                .linkEdital("https://venda-imoveis.caixa.gov.br/imovel/" + (1000 + i))
                .build()
            );
        }
        
        return imoveis;
    }

    private List<LeilaoExternoDTO> gerarImoveisSimulados() {
        return List.of(
            LeilaoExternoDTO.builder()
                .numeroLeilao("LEILAO-001")
                .descricao("Casa residencial - 3 quartos, 120m², garagem, próximo ao metrô - São Paulo")
                .valorAvaliacao(new BigDecimal("380000.00"))
                .dataLeilao(LocalDate.now().plusDays(15).toString())
                .uf("SP")
                .instituicao("Caixa Econômica Federal - Leilões")
                .linkEdital("https://venda-imoveis.caixa.gov.br/001")
                .build(),
            LeilaoExternoDTO.builder()
                .numeroLeilao("LEILAO-002")
                .descricao("Apartamento - 2 dormitórios, 65m², sacada, condomínio fechado - Rio de Janeiro")
                .valorAvaliacao(new BigDecimal("295000.00"))
                .dataLeilao(LocalDate.now().plusDays(22).toString())
                .uf("RJ")
                .instituicao("Banco do Brasil - Leilões")
                .linkEdital("https://www.bb.com.br/leiloes/002")
                .build(),
            LeilaoExternoDTO.builder()
                .numeroLeilao("LEILAO-003")
                .descricao("Terreno urbano - 500m², área nobre, documentação regularizada - Belo Horizonte")
                .valorAvaliacao(new BigDecimal("450000.00"))
                .dataLeilao(LocalDate.now().plusDays(8).toString())
                .uf("MG")
                .instituicao("Santander - Ativos Retomados")
                .linkEdital("https://www.santander.com.br/leiloes/003")
                .build(),
            LeilaoExternoDTO.builder()
                .numeroLeilao("LEILAO-004")
                .descricao("Sobrado - 4 quartos, 180m², 2 vagas, churrasqueira - Curitiba")
                .valorAvaliacao(new BigDecimal("520000.00"))
                .dataLeilao(LocalDate.now().plusDays(30).toString())
                .uf("PR")
                .instituicao("Itaú - Leilões")
                .linkEdital("https://www.itau.com.br/leiloes/004")
                .build(),
            LeilaoExternoDTO.builder()
                .numeroLeilao("LEILAO-005")
                .descricao("Cobertura - 3 suítes, 150m², vista panorâmica, piscina privativa - Florianópolis")
                .valorAvaliacao(new BigDecimal("890000.00"))
                .dataLeilao(LocalDate.now().plusDays(45).toString())
                .uf("SC")
                .instituicao("Bradesco - Leilões")
                .linkEdital("https://www.bradesco.com.br/leiloes/005")
                .build(),
            LeilaoExternoDTO.builder()
                .numeroLeilao("LEILAO-006")
                .descricao("Sala Comercial - 45m², andar alto, estacionamento incluso - Santos")
                .valorAvaliacao(new BigDecimal("220000.00"))
                .dataLeilao(LocalDate.now().plusDays(12).toString())
                .uf("SP")
                .instituicao("Caixa Econômica Federal - Leilões")
                .linkEdital("https://venda-imoveis.caixa.gov.br/006")
                .build(),
            LeilaoExternoDTO.builder()
                .numeroLeilao("LEILAO-007")
                .descricao("Galpão industrial - 800m², pé direito alto, fácil acesso - Porto Alegre")
                .valorAvaliacao(new BigDecimal("1200000.00"))
                .dataLeilao(LocalDate.now().plusDays(60).toString())
                .uf("RS")
                .instituicao("Banco do Brasil - Leilões")
                .linkEdital("https://www.bb.com.br/leiloes/007")
                .build(),
            LeilaoExternoDTO.builder()
                .numeroLeilao("LEILAO-008")
                .descricao("Loft moderno - 1 suíte, 50m², mobiliado, região central - Salvador")
                .valorAvaliacao(new BigDecimal("310000.00"))
                .dataLeilao(LocalDate.now().plusDays(18).toString())
                .uf("BA")
                .instituicao("Santander - Ativos Retomados")
                .linkEdital("https://www.santander.com.br/leiloes/008")
                .build()
        );
    }
}

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
        String[] tipos = {"Casa", "Apartamento", "Terreno", "Sala Comercial", "Galpão"};
        String[] ufs = {"SP", "RJ", "MG", "RS", "PR", "SC", "BA", "PE"};
        
        for (int i = 0; i < Math.min(15, bancos.length); i++) {
            Map<String, Object> banco = bancos[i];
            String nomeBanco = (String) banco.getOrDefault("name", "Banco");
            
            String tipo = tipos[random.nextInt(tipos.length)];
            String uf = ufs[random.nextInt(ufs.length)];
            BigDecimal valor = BigDecimal.valueOf(150000 + random.nextInt(850000));
            
            imoveis.add(new LeilaoExternoDTO(
                "LEILAO-" + (1000 + i),
                tipo + " - Imóvel retomado " + (i + 1),
                valor,
                LocalDate.now().plusDays(random.nextInt(60)).toString(),
                uf,
                nomeBanco + " - Leilões",
                "https://venda-imoveis.caixa.gov.br/imovel/" + (1000 + i)
            ));
        }
        
        return imoveis;
    }

    private List<LeilaoExternoDTO> gerarImoveisSimulados() {
        return List.of(
            new LeilaoExternoDTO(
                "LEILAO-001",
                "Casa residencial - 3 quartos, 120m²",
                new BigDecimal("280000.00"),
                LocalDate.now().plusDays(15).toString(),
                "SP",
                "Caixa Econômica Federal - Leilões",
                "https://venda-imoveis.caixa.gov.br"
            ),
            new LeilaoExternoDTO(
                "LEILAO-002",
                "Apartamento - 2 dormitórios, 65m²",
                new BigDecimal("195000.00"),
                LocalDate.now().plusDays(22).toString(),
                "RJ",
                "Banco do Brasil - Leilões",
                "https://www.bb.com.br/leiloes"
            ),
            new LeilaoExternoDTO(
                "LEILAO-003",
                "Terreno urbano - 500m²",
                new BigDecimal("350000.00"),
                LocalDate.now().plusDays(8).toString(),
                "MG",
                "Santander - Ativos Retomados",
                "https://www.santander.com.br/leiloes"
            )
        );
    }
}

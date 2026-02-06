package com.leilao.arremateai.service;

import com.leilao.arremateai.domain.Imovel;
import com.leilao.arremateai.domain.StatusVendedor;
import com.leilao.arremateai.domain.TipoUsuario;
import com.leilao.arremateai.domain.Usuario;
import com.leilao.arremateai.dto.ImovelRequest;
import com.leilao.arremateai.dto.ImovelResponse;
import com.leilao.arremateai.mapper.ImovelMapper;
import com.leilao.arremateai.repository.ImovelRepository;
import com.leilao.arremateai.repository.ImagemImovelRepository;
import com.leilao.arremateai.repository.UsuarioRepository;
import com.leilao.arremateai.specification.ImovelSpecifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ImovelService {

    private static final Logger log = LoggerFactory.getLogger(ImovelService.class);
    private final ImovelRepository imovelRepository;
    private final ImagemImovelRepository imagemRepository;
    private final ImovelMapper imovelMapper;
    private final UsuarioRepository usuarioRepository;

    public ImovelService(
        ImovelRepository imovelRepository,
        ImagemImovelRepository imagemRepository,
        ImovelMapper imovelMapper,
        UsuarioRepository usuarioRepository
    ) {
        this.imovelRepository = imovelRepository;
        this.imagemRepository = imagemRepository;
        this.imovelMapper = imovelMapper;
        this.usuarioRepository = usuarioRepository;
    }

    public Page<ImovelResponse> buscarComFiltros(
            String uf,
            String cidade,
            String tipoImovel,
            String instituicao,
            BigDecimal valorMin,
            BigDecimal valorMax,
            String busca,
            Integer quartosMin,
            Integer banheirosMin,
            Integer vagasMin,
            BigDecimal areaMin,
            BigDecimal areaMax,
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
            .comQuartosMinimo(quartosMin)
            .comBanheirosMinimo(banheirosMin)
            .comVagasMinimo(vagasMin)
            .comAreaMinima(areaMin)
            .comAreaMaxima(areaMax)
            .construir();

        // Adiciona filtro para apenas imóveis ativos
        specification = specification.and(ImovelSpecifications.apenasAtivos());

        return imovelRepository.findAll(specification, pageable)
            .map(imovelMapper::paraResponse);
    }

    public List<ImovelResponse> buscarTodosImoveis() {
        log.info("Buscando imóveis cadastrados no banco de dados");

        // Buscar apenas imóveis ativos
        Specification<Imovel> apenasAtivos = ImovelSpecifications.apenasAtivos();
        var imoveisBanco = imovelRepository.findAll(apenasAtivos);

        return converterParaResponses(imoveisBanco);
    }

    private List<ImovelResponse> converterParaResponses(List<Imovel> imoveis) {
        log.info("Encontrados {} imóveis no banco de dados", imoveis.size());
        return imoveis.stream()
            .map(imovelMapper::paraResponse)
            .toList();
    }

    public ImovelResponse buscarPorId(UUID id) {
        log.info("Buscando imóvel por ID: {}", id);

        return imovelRepository.findById(id)
            .filter(Imovel::getAtivo)
            .map(imovelMapper::paraResponse)
            .orElseThrow(() -> new IllegalArgumentException("Imóvel não encontrado com ID: " + id));
    }

    @Transactional
    public ImovelResponse cadastrarImovel(ImovelRequest request, String emailUsuario) {
        log.info("Cadastrando imóvel: {} para usuário: {}", request.getNumeroLeilao(), emailUsuario);

        validarImovelNaoDuplicado(request.getNumeroLeilao());

        // Buscar usuário
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com email: " + emailUsuario));

        // Validar se vendedor está aprovado
        validarVendedorAprovado(usuario);

        // Criar imóvel e associar usuário
        Imovel imovel = imovelMapper.paraEntidade(request);
        imovel.setUsuario(usuario);
        
        var imovelSalvo = imovelRepository.save(imovel);
        log.info("Imóvel cadastrado com ID: {} para usuário: {}", imovelSalvo.getId(), emailUsuario);

        return imovelMapper.paraResponse(imovelSalvo);
    }

    private void validarImovelNaoDuplicado(String numeroLeilao) {
        if (imovelRepository.existsByNumeroLeilao(numeroLeilao)) {
            throw new IllegalArgumentException(
                "Imóvel com número de leilão " + numeroLeilao + " já existe"
            );
        }
    }

    @Transactional
    public ImovelResponse atualizarImovel(UUID id, ImovelRequest request) {
        log.info("Atualizando imóvel com ID: {}", id);

        Imovel imovel = imovelRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Imóvel não encontrado com ID: " + id));

        if (!imovel.getAtivo()) {
            throw new IllegalArgumentException("Imóvel com ID " + id + " está inativo e não pode ser atualizado");
        }

        // Validar se o número de leilão não está duplicado (exceto para o próprio imóvel)
        if (!imovel.getNumeroLeilao().equals(request.getNumeroLeilao())) {
            validarImovelNaoDuplicado(request.getNumeroLeilao());
        }

        // Atualizar todos os campos
        imovel.setNumeroLeilao(request.getNumeroLeilao());
        imovel.setDescricao(request.getDescricao());
        imovel.setValorAvaliacao(request.getValorAvaliacao());
        imovel.setDataLeilao(java.time.LocalDate.parse(request.getDataLeilao()));
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

        Imovel imovelAtualizado = imovelRepository.save(imovel);
        log.info("Imóvel atualizado com sucesso: ID {}", id);

        return imovelMapper.paraResponse(imovelAtualizado);
    }

    @Transactional
    public ImovelResponse atualizarParcial(UUID id, ImovelRequest request) {
        log.info("Atualizando parcialmente imóvel com ID: {}", id);

        Imovel imovel = imovelRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Imóvel não encontrado com ID: " + id));

        if (!imovel.getAtivo()) {
            throw new IllegalArgumentException("Imóvel com ID " + id + " está inativo e não pode ser atualizado");
        }

        // Atualizar apenas campos não-nulos
        if (request.getNumeroLeilao() != null && !request.getNumeroLeilao().isBlank()) {
            if (!imovel.getNumeroLeilao().equals(request.getNumeroLeilao())) {
                validarImovelNaoDuplicado(request.getNumeroLeilao());
            }
            imovel.setNumeroLeilao(request.getNumeroLeilao());
        }
        if (request.getDescricao() != null && !request.getDescricao().isBlank()) {
            imovel.setDescricao(request.getDescricao());
        }
        if (request.getValorAvaliacao() != null) {
            imovel.setValorAvaliacao(request.getValorAvaliacao());
        }
        if (request.getDataLeilao() != null && !request.getDataLeilao().isBlank()) {
            imovel.setDataLeilao(java.time.LocalDate.parse(request.getDataLeilao()));
        }
        if (request.getUf() != null && !request.getUf().isBlank()) {
            imovel.setUf(request.getUf());
        }
        if (request.getInstituicao() != null && !request.getInstituicao().isBlank()) {
            imovel.setInstituicao(request.getInstituicao());
        }
        if (request.getLinkEdital() != null) {
            imovel.setLinkEdital(request.getLinkEdital());
        }
        if (request.getCidade() != null) {
            imovel.setCidade(request.getCidade());
        }
        if (request.getBairro() != null) {
            imovel.setBairro(request.getBairro());
        }
        if (request.getAreaTotal() != null) {
            imovel.setAreaTotal(request.getAreaTotal());
        }
        if (request.getTipoImovel() != null) {
            imovel.setTipoImovel(request.getTipoImovel());
        }
        if (request.getQuartos() != null) {
            imovel.setQuartos(request.getQuartos());
        }
        if (request.getBanheiros() != null) {
            imovel.setBanheiros(request.getBanheiros());
        }
        if (request.getVagas() != null) {
            imovel.setVagas(request.getVagas());
        }
        if (request.getEndereco() != null) {
            imovel.setEndereco(request.getEndereco());
        }
        if (request.getCep() != null) {
            imovel.setCep(request.getCep());
        }
        if (request.getLatitude() != null) {
            imovel.setLatitude(request.getLatitude());
        }
        if (request.getLongitude() != null) {
            imovel.setLongitude(request.getLongitude());
        }
        if (request.getCondicao() != null) {
            imovel.setCondicao(request.getCondicao());
        }
        if (request.getAceitaFinanciamento() != null) {
            imovel.setAceitaFinanciamento(request.getAceitaFinanciamento());
        }
        if (request.getObservacoes() != null) {
            imovel.setObservacoes(request.getObservacoes());
        }
        if (request.getStatus() != null) {
            imovel.setStatus(request.getStatus());
        }

        Imovel imovelAtualizado = imovelRepository.save(imovel);
        log.info("Imóvel atualizado parcialmente com sucesso: ID {}", id);

        return imovelMapper.paraResponse(imovelAtualizado);
    }

    @Transactional
    public void removerImovel(UUID id) {
        log.info("Removendo imóvel com ID: {} (soft delete)", id);

        Imovel imovel = imovelRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Imóvel não encontrado com ID: " + id));

        if (!imovel.getAtivo()) {
            throw new IllegalArgumentException("Imóvel com ID " + id + " já está inativo");
        }

        imovel.setAtivo(false);
        imovelRepository.save(imovel);
        log.info("Imóvel removido com sucesso (marcado como inativo): ID {}", id);
    }

    public Map<String, Object> validarImovel(UUID id) {
        log.info("Validando imóvel ID: {}", id);

        Imovel imovel = imovelRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Imóvel não encontrado com ID: " + id));

        Map<String, Object> validacao = new HashMap<>();
        boolean valido = true;
        String mensagem = "Imóvel válido";

        // Verificar se tem pelo menos uma imagem
        long totalImagens = imagemRepository.findByImovelIdOrderByOrdemAsc(id).size();
        
        if (totalImagens == 0) {
            valido = false;
            mensagem = "Imóvel deve ter pelo menos uma imagem";
        }

        validacao.put("valido", valido);
        validacao.put("mensagem", mensagem);
        validacao.put("totalImagens", totalImagens);
        validacao.put("imovelId", id);

        return validacao;
    }

    public Page<ImovelResponse> buscarImoveisPorUsuario(String email, String status, Pageable pageable) {
        log.info("Buscando imóveis do usuário: {}, status: {}", email, status);

        Page<Imovel> imoveis;
        
        if (status != null && !status.isEmpty()) {
            imoveis = imovelRepository.findByUsuarioEmailAndStatus(email, status, pageable);
        } else {
            imoveis = imovelRepository.findByUsuarioEmail(email, pageable);
        }

        return imoveis.map(imovelMapper::paraResponse);
    }

    @Transactional
    public ImovelResponse atualizarStatus(UUID id, String novoStatus, String emailUsuario) {
        log.info("Atualizando status do imóvel {} para {}, usuário: {}", id, novoStatus, emailUsuario);

        Imovel imovel = imovelRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Imóvel não encontrado com ID: " + id));

        if (!imovel.getAtivo()) {
            throw new IllegalArgumentException("Imóvel com ID " + id + " está inativo");
        }

        // Buscar usuário autenticado
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Verificar se é o dono do imóvel ou ADMIN
        boolean isAdmin = usuario.getTipo() == TipoUsuario.ADMIN;
        boolean isDono = imovel.getUsuario() != null && 
            imovel.getUsuario().getEmail().equals(emailUsuario);

        if (!isAdmin && !isDono) {
            throw new IllegalArgumentException("Você não tem permissão para alterar o status deste imóvel");
        }

        imovel.setStatus(novoStatus);
        var imovelAtualizado = imovelRepository.save(imovel);
        
        log.info("Status do imóvel {} atualizado para {}", id, novoStatus);
        return imovelMapper.paraResponse(imovelAtualizado);
    }

    /**
     * Valida se vendedor está APROVADO antes de permitir criar/editar imóveis
     */
    private void validarVendedorAprovado(Usuario usuario) {
        if (usuario.getTipo() == TipoUsuario.VENDEDOR) {
            if (usuario.getStatusVendedor() == null || usuario.getStatusVendedor() != StatusVendedor.APROVADO) {
                String mensagem;
                switch (usuario.getStatusVendedor()) {
                    case PENDENTE_DOCUMENTOS:
                        mensagem = "Você precisa enviar os documentos para aprovação antes de anunciar imóveis";
                        break;
                    case PENDENTE_APROVACAO:
                        mensagem = "Sua conta está em análise. Aguarde a aprovação do administrador para anunciar imóveis";
                        break;
                    case REJEITADO:
                        mensagem = "Sua conta foi rejeitada. Motivo: " + (usuario.getMotivoRejeicao() != null ? usuario.getMotivoRejeicao() : "Não informado");
                        break;
                    case SUSPENSO:
                        mensagem = "Sua conta está suspensa. Entre em contato com o administrador";
                        break;
                    default:
                        mensagem = "Você não tem permissão para anunciar imóveis";
                }
                throw new IllegalStateException(mensagem);
            }
        }
    }
}

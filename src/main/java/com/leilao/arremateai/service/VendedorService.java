package com.leilao.arremateai.service;

import com.leilao.arremateai.domain.*;
import com.leilao.arremateai.dto.*;
import com.leilao.arremateai.mapper.DocumentoVendedorMapper;
import com.leilao.arremateai.mapper.VendedorMapper;
import com.leilao.arremateai.repository.DocumentoVendedorRepository;
import com.leilao.arremateai.repository.HistoricoStatusVendedorRepository;
import com.leilao.arremateai.repository.UsuarioRepository;
import com.leilao.arremateai.validator.EmailCorporativoValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendedorService {
    
    private final UsuarioRepository usuarioRepository;
    private final DocumentoVendedorRepository documentoRepository;
    private final HistoricoStatusVendedorRepository historicoRepository;
    private final PasswordEncoder passwordEncoder;
    private final VendedorMapper vendedorMapper;
    private final DocumentoVendedorMapper documentoMapper;
    private final EmailCorporativoValidator emailCorporativoValidator;
    // private final EmailService emailService; // TODO: implementar
    // private final ArmazenamentoService armazenamentoService; // TODO: implementar
    
    /**
     * Registra um novo vendedor PJ
     */
    @Transactional
    public VendedorResponse registrarVendedor(CadastroVendedorRequest request) {
        log.info("Registrando novo vendedor PJ - CNPJ: {}", request.getCnpj());
        
        // Validar email corporativo
        emailCorporativoValidator.validarEmailCorporativo(request.getEmailCorporativo());
        
        // Validar se já existe vendedor com este CNPJ
        if (usuarioRepository.existsByCnpj(request.getCnpj())) {
            throw new IllegalArgumentException("Já existe um vendedor cadastrado com este CNPJ");
        }
        
        // Validar se email já está em uso
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Este email já está em uso");
        }
        
        // TODO: Validar CNPJ na Receita Federal
        // validarCnpjReceitaFederal(request.getCnpj());
        
        // Criar usuário vendedor
        log.info("Senha recebida (primeiros 3 chars): {}...", request.getSenha().substring(0, Math.min(3, request.getSenha().length())));
        log.info("Tamanho da senha: {}", request.getSenha().length());
        
        Usuario vendedor = new Usuario();
        vendedor.setNome(request.getNome());
        vendedor.setEmail(request.getEmail());
        vendedor.setSenha(passwordEncoder.encode(request.getSenha()));
        
        log.info("Senha criptografada (primeiros 10 chars): {}...", vendedor.getSenha().substring(0, 10));
        
        vendedor.setCpf(request.getCpf());
        vendedor.setTelefone(request.getTelefone());
        vendedor.setTipo(TipoUsuario.VENDEDOR);
        vendedor.setAtivo(true);
        
        // Dados PJ
        vendedor.setCnpj(request.getCnpj());
        vendedor.setRazaoSocial(request.getRazaoSocial());
        vendedor.setNomeFantasia(request.getNomeFantasia());
        vendedor.setInscricaoEstadual(request.getInscricaoEstadual());
        vendedor.setEmailCorporativo(request.getEmailCorporativo());
        vendedor.setEmailCorporativoVerificado(false);
        vendedor.setStatusVendedor(StatusVendedor.PENDENTE_DOCUMENTOS);
        
        vendedor = usuarioRepository.save(vendedor);
        
        // Registrar no histórico
        registrarHistorico(vendedor, null, StatusVendedor.PENDENTE_DOCUMENTOS, "Cadastro inicial", null);
        
        // TODO: Enviar email de verificação
        // emailService.enviarEmailVerificacao(vendedor);
        
        log.info("Vendedor registrado com sucesso - ID: {}", vendedor.getId());
        return vendedorMapper.paraResponse(vendedor);
    }
    
    /**
     * Busca status do vendedor logado
     */
    @Transactional(readOnly = true)
    public VendedorResponse buscarMeuStatus(UUID vendedorId) {
        Usuario vendedor = buscarVendedorPorId(vendedorId);
        return vendedorMapper.paraResponse(vendedor);
    }
    
    /**
     * Lista documentos do vendedor logado
     */
    @Transactional(readOnly = true)
    public List<DocumentoVendedorResponse> listarMeusDocumentos(UUID vendedorId) {
        return documentoRepository.findByUsuarioId(vendedorId)
                .stream()
                .map(documentoMapper::paraResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Upload de documento do vendedor
     */
    @Transactional
    public DocumentoVendedorResponse uploadDocumento(UUID vendedorId, TipoDocumento tipo, MultipartFile arquivo) {
        log.info("Upload de documento - Vendedor: {}, Tipo: {}", vendedorId, tipo);
        
        Usuario vendedor = buscarVendedorPorId(vendedorId);
        
        // Validar se vendedor está no status correto
        if (vendedor.getStatusVendedor() != StatusVendedor.PENDENTE_DOCUMENTOS 
                && vendedor.getStatusVendedor() != StatusVendedor.REJEITADO) {
            throw new IllegalStateException("Vendedor não pode enviar documentos neste momento");
        }
        
        // TODO: Upload do arquivo para storage (S3 ou local)
        // String url = armazenamentoService.salvarArquivo(arquivo, "documentos-vendedor");
        String url = "/uploads/documentos/" + UUID.randomUUID() + "_" + arquivo.getOriginalFilename();
        
        // Salvar registro do documento
        DocumentoVendedor documento = DocumentoVendedor.builder()
                .usuario(vendedor)
                .tipo(tipo)
                .nomeArquivo(arquivo.getOriginalFilename())
                .url(url)
                .tamanhoBytes(arquivo.getSize())
                .mimeType(arquivo.getContentType())
                .status(StatusDocumento.PENDENTE)
                .build();
        
        documento = documentoRepository.save(documento);
        
        // Verificar se todos os documentos obrigatórios foram enviados
        verificarDocumentosCompletos(vendedor);
        
        log.info("Documento salvo com sucesso - ID: {}", documento.getId());
        return documentoMapper.paraResponse(documento);
    }
    
    /**
     * Verifica se vendedor enviou todos os documentos obrigatórios e atualiza status
     */
    private void verificarDocumentosCompletos(Usuario vendedor) {
        // Documentos obrigatórios
        List<TipoDocumento> obrigatorios = List.of(
                TipoDocumento.CNPJ_RECEITA,
                TipoDocumento.RG_RESPONSAVEL, // ou CNH
                TipoDocumento.COMPROVANTE_ENDERECO
        );
        
        // MEI ou CONTRATO_SOCIAL (um dos dois)
        boolean temContratoOuMEI = documentoRepository.existsByUsuarioIdAndTipo(vendedor.getId(), TipoDocumento.CONTRATO_SOCIAL)
                || documentoRepository.existsByUsuarioIdAndTipo(vendedor.getId(), TipoDocumento.MEI);
        
        // Doc responsável (RG ou CNH)
        boolean temDocResponsavel = documentoRepository.existsByUsuarioIdAndTipo(vendedor.getId(), TipoDocumento.RG_RESPONSAVEL)
                || documentoRepository.existsByUsuarioIdAndTipo(vendedor.getId(), TipoDocumento.CNH_RESPONSAVEL);
        
        boolean temCNPJ = documentoRepository.existsByUsuarioIdAndTipo(vendedor.getId(), TipoDocumento.CNPJ_RECEITA);
        boolean temEndereco = documentoRepository.existsByUsuarioIdAndTipo(vendedor.getId(), TipoDocumento.COMPROVANTE_ENDERECO);
        
        if (temCNPJ && temContratoOuMEI && temDocResponsavel && temEndereco) {
            // Todos os documentos obrigatórios enviados
            if (vendedor.getStatusVendedor() == StatusVendedor.PENDENTE_DOCUMENTOS) {
                StatusVendedor statusAnterior = vendedor.getStatusVendedor();
                vendedor.setStatusVendedor(StatusVendedor.PENDENTE_APROVACAO);
                usuarioRepository.save(vendedor);
                
                registrarHistorico(vendedor, statusAnterior, StatusVendedor.PENDENTE_APROVACAO, 
                        "Todos os documentos obrigatórios foram enviados", null);
                
                // TODO: Notificar admin sobre novo vendedor pendente
                log.info("Vendedor {} passou para PENDENTE_APROVACAO", vendedor.getId());
            }
        }
    }
    
    /**
     * Busca vendedores pendentes de aprovação (ADMIN)
     */
    @Transactional(readOnly = true)
    public Page<VendedorResponse> listarVendedoresPendentes(Pageable pageable) {
        return usuarioRepository.findByTipoAndStatusVendedor(
                        TipoUsuario.VENDEDOR, StatusVendedor.PENDENTE_APROVACAO, pageable)
                .map(vendedorMapper::paraResponse);
    }
    
    /**
     * Busca detalhes de um vendedor (ADMIN)
     */
    @Transactional(readOnly = true)
    public VendedorResponse buscarVendedor(UUID vendedorId) {
        Usuario vendedor = buscarVendedorPorId(vendedorId);
        return vendedorMapper.paraResponse(vendedor);
    }
    
    /**
     * Lista documentos de um vendedor (ADMIN)
     */
    @Transactional(readOnly = true)
    public List<DocumentoVendedorResponse> listarDocumentosVendedor(UUID vendedorId) {
        return documentoRepository.findByUsuarioId(vendedorId)
                .stream()
                .map(documentoMapper::paraResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Aprova vendedor (ADMIN)
     */
    @Transactional
    public VendedorResponse aprovarVendedor(UUID vendedorId, UUID adminId, AprovarVendedorRequest request) {
        log.info("Aprovando vendedor {} por admin {}", vendedorId, adminId);
        
        Usuario vendedor = buscarVendedorPorId(vendedorId);
        Usuario admin = buscarUsuarioPorId(adminId);
        
        if (vendedor.getStatusVendedor() != StatusVendedor.PENDENTE_APROVACAO) {
            throw new IllegalStateException("Vendedor não está pendente de aprovação");
        }
        
        StatusVendedor statusAnterior = vendedor.getStatusVendedor();
        vendedor.setStatusVendedor(StatusVendedor.APROVADO);
        vendedor.setAprovadoPor(admin);
        vendedor.setAprovadoEm(LocalDateTime.now());
        vendedor.setMotivoRejeicao(null);
        
        vendedor = usuarioRepository.save(vendedor);
        
        registrarHistorico(vendedor, statusAnterior, StatusVendedor.APROVADO, 
                request.getComentario(), admin);
        
        // TODO: Enviar email de aprovação
        // emailService.enviarEmailAprovacao(vendedor);
        
        log.info("Vendedor aprovado com sucesso");
        return vendedorMapper.paraResponse(vendedor);
    }
    
    /**
     * Rejeita vendedor (ADMIN)
     */
    @Transactional
    public VendedorResponse rejeitarVendedor(UUID vendedorId, UUID adminId, RejeitarVendedorRequest request) {
        log.info("Rejeitando vendedor {} por admin {}", vendedorId, adminId);
        
        Usuario vendedor = buscarVendedorPorId(vendedorId);
        Usuario admin = buscarUsuarioPorId(adminId);
        
        if (vendedor.getStatusVendedor() != StatusVendedor.PENDENTE_APROVACAO) {
            throw new IllegalStateException("Vendedor não está pendente de aprovação");
        }
        
        StatusVendedor statusAnterior = vendedor.getStatusVendedor();
        vendedor.setStatusVendedor(StatusVendedor.REJEITADO);
        vendedor.setMotivoRejeicao(request.getMotivo());
        vendedor.setAprovadoPor(null);
        vendedor.setAprovadoEm(null);
        
        vendedor = usuarioRepository.save(vendedor);
        
        registrarHistorico(vendedor, statusAnterior, StatusVendedor.REJEITADO, 
                request.getMotivo(), admin);
        
        // TODO: Enviar email de rejeição
        // emailService.enviarEmailRejeicao(vendedor, request.getMotivo());
        
        log.info("Vendedor rejeitado");
        return vendedorMapper.paraResponse(vendedor);
    }
    
    /**
     * Atualiza status de um documento (ADMIN)
     */
    @Transactional
    public DocumentoVendedorResponse atualizarStatusDocumento(UUID documentoId, UUID adminId, 
                                                              AtualizarStatusDocumentoRequest request) {
        log.info("Atualizando status do documento {} por admin {}", documentoId, adminId);
        
        DocumentoVendedor documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new IllegalArgumentException("Documento não encontrado"));
        
        Usuario admin = buscarUsuarioPorId(adminId);
        
        documento.setStatus(request.getStatus());
        documento.setMotivoRejeicao(request.getMotivo());
        documento.setAnalisadoPor(admin);
        documento.setAnalisadoEm(LocalDateTime.now());
        
        documento = documentoRepository.save(documento);
        
        log.info("Status do documento atualizado para {}", request.getStatus());
        return documentoMapper.paraResponse(documento);
    }
    
    /**
     * Registra alteração de status no histórico
     */
    private void registrarHistorico(Usuario vendedor, StatusVendedor statusAnterior, 
                                    StatusVendedor statusNovo, String motivo, Usuario alteradoPor) {
        HistoricoStatusVendedor historico = HistoricoStatusVendedor.builder()
                .usuario(vendedor)
                .statusAnterior(statusAnterior)
                .statusNovo(statusNovo)
                .motivo(motivo)
                .alteradoPor(alteradoPor)
                .build();
        
        historicoRepository.save(historico);
    }
    
    private Usuario buscarVendedorPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vendedor não encontrado"));
        
        if (usuario.getTipo() != TipoUsuario.VENDEDOR) {
            throw new IllegalArgumentException("Usuário não é um vendedor");
        }
        
        return usuario;
    }
    
    private Usuario buscarUsuarioPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }
}

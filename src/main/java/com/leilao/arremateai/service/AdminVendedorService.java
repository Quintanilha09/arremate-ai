package com.leilao.arremateai.service;

import com.leilao.arremateai.domain.HistoricoStatusVendedor;
import com.leilao.arremateai.domain.StatusVendedor;
import com.leilao.arremateai.domain.TipoUsuario;
import com.leilao.arremateai.domain.Usuario;
import com.leilao.arremateai.dto.VendedorResponse;
import com.leilao.arremateai.mapper.VendedorMapper;
import com.leilao.arremateai.repository.HistoricoStatusVendedorRepository;
import com.leilao.arremateai.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminVendedorService {
    
    private final UsuarioRepository usuarioRepository;
    private final HistoricoStatusVendedorRepository historicoRepository;
    private final VendedorMapper vendedorMapper;
    private final AdminNotificationService adminNotificationService;
    
    /**
     * Lista vendedores pendentes de aprovação
     */
    public List<VendedorResponse> listarVendedoresPendentes() {
        log.info("Listando vendedores pendentes de aprovação");
        
        List<Usuario> vendedores = usuarioRepository.findByTipoAndStatusVendedorAndAtivoTrue(
            TipoUsuario.VENDEDOR,
            StatusVendedor.PENDENTE_APROVACAO
        );
        
        return vendedores.stream()
            .map(vendedorMapper::paraResponse)
            .toList();
    }
    
    /**
     * Lista vendedores por status
     */
    public List<VendedorResponse> listarVendedoresPorStatus(String status) {
        log.info("Listando vendedores com status: {}", status);
        
        StatusVendedor statusEnum = StatusVendedor.valueOf(status.toUpperCase());
        List<Usuario> vendedores = usuarioRepository.findByTipoAndStatusVendedorAndAtivoTrue(
            TipoUsuario.VENDEDOR,
            statusEnum
        );
        
        return vendedores.stream()
            .map(vendedorMapper::paraResponse)
            .toList();
    }
    
    /**
     * Lista todos os vendedores
     */
    public List<VendedorResponse> listarTodosVendedores() {
        log.info("Listando todos os vendedores");
        
        List<Usuario> vendedores = usuarioRepository.findByTipoAndAtivoTrue(TipoUsuario.VENDEDOR);
        
        return vendedores.stream()
            .map(vendedorMapper::paraResponse)
            .toList();
    }
    
    /**
     * Busca vendedor por ID
     */
    public VendedorResponse buscarVendedorPorId(UUID id) {
        log.info("Buscando vendedor por ID: {}", id);
        
        Usuario vendedor = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Vendedor não encontrado"));
        
        if (vendedor.getTipo() != TipoUsuario.VENDEDOR) {
            throw new IllegalArgumentException("Usuário não é um vendedor");
        }
        
        return vendedorMapper.paraResponse(vendedor);
    }
    
    /**
     * Aprova vendedor
     */
    @Transactional
    public VendedorResponse aprovarVendedor(UUID vendedorId, String comentario) {
        log.info("Aprovando vendedor ID: {}", vendedorId);
        
        Usuario vendedor = usuarioRepository.findById(vendedorId)
            .orElseThrow(() -> new IllegalArgumentException("Vendedor não encontrado"));
        
        if (vendedor.getStatusVendedor() == StatusVendedor.APROVADO) {
            throw new IllegalArgumentException("Vendedor já está aprovado");
        }
        
        StatusVendedor statusAnterior = vendedor.getStatusVendedor();
        vendedor.setStatusVendedor(StatusVendedor.APROVADO);
        vendedor.setAprovadoEm(LocalDateTime.now());
        vendedor.setAprovadoPor(buscarAdminAtual());
        vendedor.setMotivoRejeicao(null); // Limpa motivo de rejeição anterior se houver
        
        vendedor = usuarioRepository.save(vendedor);
        
        // Registrar no histórico
        registrarHistorico(
            vendedor,
            statusAnterior,
            StatusVendedor.APROVADO,
            comentario != null ? comentario : "Vendedor aprovado pelo administrador",
            buscarAdminAtual()
        );
        
        // Notificar vendedor por e-mail
        adminNotificationService.notificarVendedorAprovado(vendedor);
        
        log.info("Vendedor aprovado com sucesso: {}", vendedorId);
        return vendedorMapper.paraResponse(vendedor);
    }
    
    /**
     * Rejeita vendedor
     */
    @Transactional
    public VendedorResponse rejeitarVendedor(UUID vendedorId, String motivo) {
        log.info("Rejeitando vendedor ID: {}", vendedorId);
        
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Motivo da rejeição é obrigatório");
        }
        
        Usuario vendedor = usuarioRepository.findById(vendedorId)
            .orElseThrow(() -> new IllegalArgumentException("Vendedor não encontrado"));
        
        StatusVendedor statusAnterior = vendedor.getStatusVendedor();
        vendedor.setStatusVendedor(StatusVendedor.REJEITADO);
        vendedor.setMotivoRejeicao(motivo);
        
        vendedor = usuarioRepository.save(vendedor);
        
        // Registrar no histórico
        registrarHistorico(
            vendedor,
            statusAnterior,
            StatusVendedor.REJEITADO,
            "Rejeitado: " + motivo,
            buscarAdminAtual()
        );
        
        // Notificar vendedor por e-mail
        adminNotificationService.notificarVendedorRejeitado(vendedor, motivo);
        
        log.info("Vendedor rejeitado: {}", vendedorId);
        return vendedorMapper.paraResponse(vendedor);
    }
    
    /**
     * Suspende vendedor
     */
    @Transactional
    public VendedorResponse suspenderVendedor(UUID vendedorId, String motivo) {
        log.info("Suspendendo vendedor ID: {}", vendedorId);
        
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Motivo da suspensão é obrigatório");
        }
        
        Usuario vendedor = usuarioRepository.findById(vendedorId)
            .orElseThrow(() -> new IllegalArgumentException("Vendedor não encontrado"));
        
        StatusVendedor statusAnterior = vendedor.getStatusVendedor();
        vendedor.setStatusVendedor(StatusVendedor.SUSPENSO);
        vendedor.setMotivoRejeicao(motivo);
        
        vendedor = usuarioRepository.save(vendedor);
        
        // Registrar no histórico
        registrarHistorico(
            vendedor,
            statusAnterior,
            StatusVendedor.SUSPENSO,
            "Suspenso: " + motivo,
            buscarAdminAtual()
        );
        
        log.info("Vendedor suspenso: {}", vendedorId);
        return vendedorMapper.paraResponse(vendedor);
    }
    
    /**
     * Obtém estatísticas de vendedores
     */
    public Map<String, Object> obterEstatisticas() {
        log.info("Obtendo estatísticas de vendedores");
        
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("total", usuarioRepository.countByTipoAndAtivoTrue(TipoUsuario.VENDEDOR));
        stats.put("pendentes", usuarioRepository.countByTipoAndStatusVendedorAndAtivoTrue(
            TipoUsuario.VENDEDOR, StatusVendedor.PENDENTE_APROVACAO));
        stats.put("aprovados", usuarioRepository.countByTipoAndStatusVendedorAndAtivoTrue(
            TipoUsuario.VENDEDOR, StatusVendedor.APROVADO));
        stats.put("rejeitados", usuarioRepository.countByTipoAndStatusVendedorAndAtivoTrue(
            TipoUsuario.VENDEDOR, StatusVendedor.REJEITADO));
        stats.put("suspensos", usuarioRepository.countByTipoAndStatusVendedorAndAtivoTrue(
            TipoUsuario.VENDEDOR, StatusVendedor.SUSPENSO));
        
        return stats;
    }
    
    private void registrarHistorico(Usuario vendedor, StatusVendedor statusAnterior, 
                                    StatusVendedor novoStatus, String observacao, Usuario alteradoPor) {
        HistoricoStatusVendedor historico = new HistoricoStatusVendedor();
        historico.setUsuario(vendedor);
        historico.setStatusAnterior(statusAnterior);
        historico.setStatusNovo(novoStatus);
        historico.setMotivo(observacao);
        historico.setAlteradoPor(alteradoPor);
        
        historicoRepository.save(historico);
    }
    
    private Usuario buscarAdminAtual() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Admin não encontrado"));
        } catch (Exception e) {
            return null; // Sistema automático
        }
    }
}

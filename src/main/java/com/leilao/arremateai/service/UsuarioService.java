package com.leilao.arremateai.service;

import com.leilao.arremateai.domain.Usuario;
import com.leilao.arremateai.dto.UsuarioRequest;
import com.leilao.arremateai.dto.UsuarioResponse;
import com.leilao.arremateai.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }

    public UsuarioResponse cadastrar(UsuarioRequest request) {
        log.info("Cadastrando novo usuário: {}", request.email());

        // Validar email único
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // Validar CPF único (se fornecido)
        if (request.cpf() != null && usuarioRepository.existsByCpf(request.cpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        // Criar usuário
        Usuario usuario = new Usuario();
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setTelefone(request.telefone());
        usuario.setCpf(request.cpf());
        usuario.setTipo(request.tipo() != null ? request.tipo() : com.leilao.arremateai.domain.TipoUsuario.COMPRADOR);
        usuario.setAtivo(true);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        log.info("Usuário cadastrado com sucesso: {}", usuarioSalvo.getId());

        return converterParaResponse(usuarioSalvo);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id));
        return converterParaResponse(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + email));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::converterParaResponse)
                .toList();
    }

    public UsuarioResponse atualizar(UUID id, UsuarioRequest request) {
        log.info("Atualizando usuário: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id));

        // Validar email único (se mudou)
        if (!usuario.getEmail().equals(request.email()) 
                && usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // Atualizar campos
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        if (request.senha() != null && !request.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.senha()));
        }
        usuario.setTelefone(request.telefone());
        usuario.setCpf(request.cpf());
        if (request.tipo() != null) {
            usuario.setTipo(request.tipo());
        }

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        log.info("Usuário atualizado com sucesso");

        return converterParaResponse(usuarioAtualizado);
    }

    public void desativar(UUID id) {
        log.info("Desativando usuário: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + id));

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);

        log.info("Usuário desativado com sucesso");
    }

    public void redefinirSenha(String email, String novaSenha) {
        log.info("Redefinindo senha para usuário: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado: " + email));

        // Verificar se é usuário OAuth2 (sem senha)
        if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
            throw new IllegalArgumentException("Usuário autenticado via Google. Não é possível redefinir senha.");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);

        log.info("Senha redefinida com sucesso");
    }

    public UsuarioResponse converterParaResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getCpf(),
                usuario.getTipo(),
                usuario.getAtivo(),
                usuario.getCreatedAt(),
                usuario.getAvatarUrl()
        );
    }
}

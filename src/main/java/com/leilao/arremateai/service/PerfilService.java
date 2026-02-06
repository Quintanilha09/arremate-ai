package com.leilao.arremateai.service;

import com.leilao.arremateai.domain.Usuario;
import com.leilao.arremateai.dto.AlterarSenhaRequest;
import com.leilao.arremateai.dto.AtualizarPerfilRequest;
import com.leilao.arremateai.dto.PerfilResponse;
import com.leilao.arremateai.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service responsável por operações de perfil do usuário
 * Segue o princípio Single Responsibility focando apenas em perfil
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PerfilService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    private static final String AVATAR_UPLOAD_DIR = "uploads/avatars/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * Busca perfil completo do usuário autenticado
     */
    @Transactional(readOnly = true)
    public PerfilResponse buscarPerfil(String email) {
        log.info("Buscando perfil do usuário: {}", email);
        
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return mapToResponse(usuario);
    }

    /**
     * Atualiza dados do perfil do usuário
     */
    @Transactional
    public PerfilResponse atualizarPerfil(String email, AtualizarPerfilRequest request) {
        log.info("Atualizando perfil do usuário: {}", email);
        
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Atualiza apenas campos não nulos (permite atualização parcial)
        if (request.nome() != null && !request.nome().isBlank()) {
            usuario.setNome(request.nome());
        }
        
        if (request.telefone() != null) {
            usuario.setTelefone(request.telefone());
        }
        
        if (request.cpf() != null) {
            // Verifica se CPF já existe para outro usuário
            usuarioRepository.findByCpf(request.cpf())
                .ifPresent(u -> {
                    if (!u.getId().equals(usuario.getId())) {
                        throw new RuntimeException("CPF já cadastrado para outro usuário");
                    }
                });
            usuario.setCpf(request.cpf());
        }

        Usuario atualizado = usuarioRepository.save(usuario);
        log.info("Perfil atualizado com sucesso para usuário: {}", email);
        
        return mapToResponse(atualizado);
    }

    /**
     * Altera senha do usuário com validação de senha atual
     */
    @Transactional
    public void alterarSenha(String email, AlterarSenhaRequest request) {
        log.info("Alterando senha do usuário: {}", email);
        
        // Validação de senhas conferem
        if (!request.senhasConferem()) {
            throw new RuntimeException("Nova senha e confirmação não conferem");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        // Verifica se usu\u00e1rio tem senha (n\u00e3o \u00e9 OAuth2)
        if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
            throw new RuntimeException("Usu\u00e1rio autenticado via Google. N\u00e3o \u00e9 poss\u00edvel alterar senha.");
        }
        // Valida senha atual
        if (!passwordEncoder.matches(request.senhaAtual(), usuario.getSenha())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        // Valida que nova senha é diferente da atual
        if (passwordEncoder.matches(request.novaSenha(), usuario.getSenha())) {
            throw new RuntimeException("Nova senha deve ser diferente da senha atual");
        }

        // Criptografa e salva nova senha
        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
        usuarioRepository.save(usuario);
        
        log.info("Senha alterada com sucesso para usuário: {}", email);
    }

    /**
     * Faz upload de avatar do usuário
     * Valida tipo e tamanho do arquivo
     */
    @Transactional
    public PerfilResponse uploadAvatar(String email, MultipartFile file) throws IOException {
        log.info("Upload de avatar para usuário: {}", email);
        
        // Validações
        if (file.isEmpty()) {
            throw new RuntimeException("Arquivo de imagem é obrigatório");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("Arquivo muito grande. Máximo permitido: 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Arquivo deve ser uma imagem (JPG, PNG, etc)");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Remove avatar antigo se existir
        if (usuario.getAvatarUrl() != null && !usuario.getAvatarUrl().isBlank()) {
            removerAvatarAntigo(usuario.getAvatarUrl());
        }

        // Salva novo avatar
        String nomeArquivo = gerarNomeArquivo(usuario.getId(), file.getOriginalFilename());
        String avatarUrl = salvarArquivo(file, nomeArquivo);

        usuario.setAvatarUrl(avatarUrl);
        Usuario atualizado = usuarioRepository.save(usuario);
        
        log.info("Avatar atualizado com sucesso para usuário: {}", email);
        
        return mapToResponse(atualizado);
    }

    /**
     * Remove avatar do usuário
     */
    @Transactional
    public PerfilResponse removerAvatar(String email) {
        log.info("Removendo avatar do usuário: {}", email);
        
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuario.getAvatarUrl() != null && !usuario.getAvatarUrl().isBlank()) {
            removerAvatarAntigo(usuario.getAvatarUrl());
            usuario.setAvatarUrl(null);
            
            Usuario atualizado = usuarioRepository.save(usuario);
            log.info("Avatar removido com sucesso para usuário: {}", email);
            
            return mapToResponse(atualizado);
        }

        return mapToResponse(usuario);
    }

    // ========== Métodos Privados (Helpers) ==========

    private PerfilResponse mapToResponse(Usuario usuario) {
        return new PerfilResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getTelefone(),
            usuario.getCpf(),
            usuario.getTipo(),
            usuario.getAvatarUrl(),
            usuario.getAtivo(),
            usuario.getCreatedAt(),
            usuario.getUpdatedAt()
        );
    }

    private String gerarNomeArquivo(UUID usuarioId, String nomeOriginal) {
        String extensao = "";
        if (nomeOriginal != null && nomeOriginal.contains(".")) {
            extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        }
        return "avatar_" + usuarioId + "_" + System.currentTimeMillis() + extensao;
    }

    private String salvarArquivo(MultipartFile file, String nomeArquivo) throws IOException {
        Path uploadPath = Paths.get(AVATAR_UPLOAD_DIR);
        
        // Cria diretório se não existir
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path arquivoPath = uploadPath.resolve(nomeArquivo);
        Files.copy(file.getInputStream(), arquivoPath, StandardCopyOption.REPLACE_EXISTING);

        return "/" + AVATAR_UPLOAD_DIR + nomeArquivo;
    }

    private void removerAvatarAntigo(String avatarUrl) {
        try {
            if (avatarUrl.startsWith("/")) {
                avatarUrl = avatarUrl.substring(1);
            }
            Path arquivoPath = Paths.get(avatarUrl);
            Files.deleteIfExists(arquivoPath);
            log.info("Avatar antigo removido: {}", avatarUrl);
        } catch (IOException e) {
            log.warn("Erro ao remover avatar antigo: {}", avatarUrl, e);
        }
    }
}

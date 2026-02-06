package com.leilao.arremateai.service;

import com.leilao.arremateai.domain.TipoUsuario;
import com.leilao.arremateai.domain.Usuario;
import com.leilao.arremateai.dto.OAuth2UserInfo;
import com.leilao.arremateai.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class OAuth2Service {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2Service.class);
    private static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private static final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    private final UsuarioRepository usuarioRepository;
    private final RestTemplate restTemplate;

    public OAuth2Service(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.restTemplate = new RestTemplate();
    }

    public Usuario processGoogleCallback(String code) {
        try {
            // 1. Trocar código por access token
            String accessToken = exchangeCodeForToken(code);
            
            // 2. Buscar informações do usuário
            OAuth2UserInfo userInfo = getUserInfo(accessToken);
            
            // 3. Criar ou buscar usuário
            return findOrCreateUser(userInfo);
            
        } catch (Exception e) {
            logger.error("Erro ao processar callback do Google OAuth2", e);
            throw new RuntimeException("Erro ao autenticar com Google: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private String exchangeCodeForToken(String code) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", redirectUri);
            params.add("grant_type", "authorization_code");

            logger.info("=== Iniciando troca de código por token ===");
            logger.info("Redirect URI: {}", redirectUri);
            logger.info("Client ID: {}", clientId);
            logger.info("Code length: {} chars", code.length());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(GOOGLE_TOKEN_URL, request, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("✓ Token obtido com sucesso do Google");
                Object accessToken = response.getBody().get("access_token");
                if (accessToken == null) {
                    throw new RuntimeException("Access token não encontrado na resposta do Google");
                }
                return (String) accessToken;
            }
            
            logger.error("✗ Falha ao obter token: Status {}, Body: {}", response.getStatusCode(), response.getBody());
            throw new RuntimeException("Falha ao obter access token do Google: " + response.getStatusCode());
            
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            logger.error("✗ Erro HTTP {} ao trocar código por token", e.getStatusCode());
            logger.error("Response body: {}", e.getResponseBodyAsString());
            logger.error("POSSÍVEIS CAUSAS:");
            logger.error("1. Código de autorização já foi usado (códigos só podem ser usados UMA vez)");
            logger.error("2. Client Secret incorreto no arquivo .env");
            logger.error("3. Redirect URI não corresponde ao configurado no Google Console");
            logger.error("4. Código expirou (expira em ~10 minutos)");
            throw new RuntimeException("Erro HTTP " + e.getStatusCode() + " ao autenticar com Google. Verifique os logs acima.");
        } catch (Exception e) {
            logger.error("✗ Erro inesperado ao trocar código por token", e);
            throw new RuntimeException("Erro ao autenticar com Google: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private OAuth2UserInfo getUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                GOOGLE_USERINFO_URL,
                HttpMethod.GET,
                entity,
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                
                // Valida campos obrigatórios
                String email = (String) body.get("email");
                String name = (String) body.get("name");
                String id = (String) body.get("id");
                
                if (email == null || email.isBlank()) {
                    throw new RuntimeException("Email não encontrado na resposta do Google");
                }
                if (name == null || name.isBlank()) {
                    throw new RuntimeException("Nome não encontrado na resposta do Google");
                }
                if (id == null || id.isBlank()) {
                    throw new RuntimeException("ID não encontrado na resposta do Google");
                }
                
                logger.info("Informações do usuário obtidas: {}", email);
                return new OAuth2UserInfo(email, name, id);
            }
            
            logger.error("Falha ao obter userinfo: Status {}", response.getStatusCode());
            throw new RuntimeException("Falha ao obter informações do usuário do Google");
            
        } catch (Exception e) {
            logger.error("Erro ao buscar informações do usuário", e);
            throw new RuntimeException("Erro ao buscar dados do usuário: " + e.getMessage());
        }
    }

    private Usuario findOrCreateUser(OAuth2UserInfo userInfo) {
        Optional<Usuario> existingUser = usuarioRepository.findByEmail(userInfo.email());
        
        if (existingUser.isPresent()) {
            logger.info("Usuário existente encontrado: {}", userInfo.email());
            return existingUser.get();
        }
        
        // Criar novo usuário
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(userInfo.nome());
        novoUsuario.setEmail(userInfo.email());
        novoUsuario.setSenha(""); // Senha vazia para usuários OAuth
        novoUsuario.setTipo(TipoUsuario.COMPRADOR); // Tipo padrão
        novoUsuario.setAtivo(true);
        
        Usuario savedUser = usuarioRepository.save(novoUsuario);
        logger.info("Novo usuário criado via Google OAuth: {}", userInfo.email());
        
        return savedUser;
    }
}

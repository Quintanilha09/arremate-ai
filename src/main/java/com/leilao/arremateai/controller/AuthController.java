package com.leilao.arremateai.controller;

import com.leilao.arremateai.domain.Usuario;
import com.leilao.arremateai.dto.*;
import com.leilao.arremateai.security.JwtService;
import com.leilao.arremateai.service.OAuth2Service;
import com.leilao.arremateai.service.UsuarioService;
import com.leilao.arremateai.service.VerificacaoService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final VerificacaoService verificacaoService;
    private final OAuth2Service oauth2Service;

    public AuthController(
            UsuarioService usuarioService,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            VerificacaoService verificacaoService,
            OAuth2Service oauth2Service
    ) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.verificacaoService = verificacaoService;
        this.oauth2Service = oauth2Service;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody UsuarioRequest request) {
        logger.info("Registrando novo usuário: {}", request.email());
        
        UsuarioResponse usuario = usuarioService.cadastrar(request);
        Usuario usuarioEntity = usuarioService.buscarPorEmail(request.email());
        String token = jwtService.generateToken(usuarioEntity);

        AuthResponse response = new AuthResponse(
                token,
                "Bearer",
                usuario
        );

        logger.info("Usuário registrado com sucesso: {}", usuario.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Tentativa de login para: {}", request.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.senha()
                )
        );

        Usuario usuario = usuarioService.buscarPorEmail(request.email());
        String token = jwtService.generateToken(usuario);
        UsuarioResponse usuarioResponse = usuarioService.converterParaResponse(usuario);

        AuthResponse response = new AuthResponse(
                token,
                "Bearer",
                usuarioResponse
        );

        logger.info("Login realizado com sucesso: {}", usuario.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        
        logger.info("Buscando informações do usuário autenticado: {}", usuario.getId());
        UsuarioResponse response = usuarioService.converterParaResponse(usuario);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Em uma implementação JWT stateless, o logout é feito no client-side
        // (remover o token do localStorage/sessionStorage)
        // Para implementar blacklist de tokens, seria necessário um cache (Redis)
        logger.info("Logout realizado");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();
        
        logger.info("Renovando token para usuário: {}", usuario.getId());
        
        String newToken = jwtService.generateToken(usuario);
        UsuarioResponse usuarioResponse = usuarioService.converterParaResponse(usuario);

        AuthResponse response = new AuthResponse(
                newToken,
                "Bearer",
                usuarioResponse
        );

        return ResponseEntity.ok(response);
    }

    // ========== 2FA Endpoints ==========

    @PostMapping("/2fa/enviar-codigo")
    public ResponseEntity<Map<String, String>> enviarCodigoVerificacao(
            @Valid @RequestBody EnviarCodigoRequest request) {
        logger.info("Enviando código de verificação para: {}", request.email());
        
        try {
            verificacaoService.enviarCodigoVerificacao(request.email());
            return ResponseEntity.ok(Map.of("message", "Código enviado com sucesso"));
        } catch (Exception e) {
            logger.error("Erro ao enviar código: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao enviar código de verificação"));
        }
    }

    @PostMapping("/2fa/verificar-codigo")
    public ResponseEntity<Map<String, Object>> verificarCodigo(
            @Valid @RequestBody VerificarCodigoRequest request) {
        logger.info("Verificando código para: {}", request.email());
        
        boolean valido = verificacaoService.verificarCodigo(request.email(), request.codigo());
        
        if (valido) {
            return ResponseEntity.ok(Map.of(
                    "valido", true,
                    "message", "Código verificado com sucesso"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "valido", false,
                            "error", "Código inválido ou expirado"
                    ));
        }
    }

    // ========== OAuth2 Callback ==========
    
    @GetMapping("/oauth2/callback/google")
    public RedirectView googleCallback(@RequestParam("code") String code) {
        try {
            logger.info("Recebendo callback do Google OAuth2");
            
            // Processar código e obter/criar usuário
            Usuario usuario = oauth2Service.processGoogleCallback(code);
            
            // Gerar JWT token
            String token = jwtService.generateToken(usuario);
            
            // Redirecionar para o frontend com o token
            String frontendUrl = "http://localhost:3000/auth/success?token=" + token;
            
            logger.info("Login OAuth2 bem-sucedido para: {}", usuario.getEmail());
            return new RedirectView(frontendUrl);
            
        } catch (Exception e) {
            logger.error("Erro no callback OAuth2", e);
            return new RedirectView("http://localhost:3000/login?error=" + e.getMessage());
        }
    }
}

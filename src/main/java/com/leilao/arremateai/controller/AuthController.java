package com.leilao.arremateai.controller;

import com.leilao.arremateai.domain.Usuario;
import com.leilao.arremateai.dto.AuthResponse;
import com.leilao.arremateai.dto.LoginRequest;
import com.leilao.arremateai.dto.UsuarioRequest;
import com.leilao.arremateai.dto.UsuarioResponse;
import com.leilao.arremateai.security.JwtService;
import com.leilao.arremateai.service.UsuarioService;
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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(
            UsuarioService usuarioService,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
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
}

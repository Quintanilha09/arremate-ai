package com.leilao.arremateai.repository;

import com.leilao.arremateai.domain.TipoUsuario;
import com.leilao.arremateai.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    
    Optional<Usuario> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByCpf(String cpf);
    
    List<Usuario> findByTipo(TipoUsuario tipo);
    
    List<Usuario> findByAtivoTrue();
    
    long countByTipo(TipoUsuario tipo);
}

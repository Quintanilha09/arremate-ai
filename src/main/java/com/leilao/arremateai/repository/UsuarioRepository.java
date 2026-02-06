package com.leilao.arremateai.repository;

import com.leilao.arremateai.domain.StatusVendedor;
import com.leilao.arremateai.domain.TipoUsuario;
import com.leilao.arremateai.domain.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    
    Optional<Usuario> findByEmail(String email);
    
    Optional<Usuario> findByCpf(String cpf);
    
    boolean existsByEmail(String email);
    
    boolean existsByCpf(String cpf);
    
    List<Usuario> findByTipo(TipoUsuario tipo);
    
    List<Usuario> findByAtivoTrue();
    
    long countByTipo(TipoUsuario tipo);
    
    // ===== MÉTODOS PARA VENDEDOR =====
    
    /**
     * Busca vendedor por CNPJ
     */
    Optional<Usuario> findByCnpj(String cnpj);
    
    /**
     * Verifica se já existe vendedor com este CNPJ
     */
    boolean existsByCnpj(String cnpj);
    
    /**
     * Busca vendedores por status
     */
    List<Usuario> findByTipoAndStatusVendedor(TipoUsuario tipo, StatusVendedor status);
    
    /**
     * Busca vendedores pendentes de aprovação (paginado)
     */
    Page<Usuario> findByTipoAndStatusVendedor(TipoUsuario tipo, StatusVendedor status, Pageable pageable);
    
    /**
     * Conta vendedores por status
     */
    long countByTipoAndStatusVendedor(TipoUsuario tipo, StatusVendedor status);
}

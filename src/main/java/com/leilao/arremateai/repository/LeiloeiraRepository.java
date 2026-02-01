package com.leilao.arremateai.repository;

import com.leilao.arremateai.domain.Leiloeira;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeiloeiraRepository extends JpaRepository<Leiloeira, Long> {

    List<Leiloeira> findByStatus(Leiloeira.Status status);

    Optional<Leiloeira> findByNome(String nome);

    List<Leiloeira> findByTipoIntegracao(Leiloeira.TipoIntegracao tipoIntegracao);
}

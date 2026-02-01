package com.leilao.arremateai.repository;

import com.leilao.arremateai.domain.Leilao;
import com.leilao.arremateai.domain.Leiloeira;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeilaoRepository extends JpaRepository<Leilao, Long> {

    List<Leilao> findByStatus(Leilao.StatusLeilao status);

    List<Leilao> findByLeiloeira(Leiloeira leiloeira);

    List<Leilao> findByDataEncerramentoAfter(LocalDateTime dataLimite);
}

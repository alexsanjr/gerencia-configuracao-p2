package com.cefet.vocealuga.repositories;

import com.cefet.vocealuga.entities.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    Optional<Pagamento> findByToken(String token);
    void deleteByDataExpiracaoBefore(LocalDateTime dataExpiracao);
}
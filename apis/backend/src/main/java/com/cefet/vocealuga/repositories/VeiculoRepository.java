package com.cefet.vocealuga.repositories;

import com.cefet.vocealuga.entities.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    @Query("SELECT obj FROM Veiculo obj " +
            "WHERE UPPER(obj.marca) LIKE UPPER(CONCAT('%', :marca, '%'))")
    Page<Veiculo> searchByBrand(String marca, Pageable pageable);

    Optional<Veiculo> findByPlaca(String placa);
}

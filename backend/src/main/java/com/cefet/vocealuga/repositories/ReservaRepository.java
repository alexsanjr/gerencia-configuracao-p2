package com.cefet.vocealuga.repositories;

import com.cefet.vocealuga.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    @Query("SELECT r.id FROM Reserva r WHERE r.usuario.id = :usuarioId")
    List<Long> findIdsByUsuarioId(@Param("usuarioId") Long usuarioId);
}

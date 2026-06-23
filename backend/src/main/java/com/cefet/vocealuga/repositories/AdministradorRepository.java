package com.cefet.vocealuga.repositories;

import com.cefet.vocealuga.entities.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    Optional<Administrador> findByEmail(String email);

    @Query("SELECT a FROM Administrador a WHERE a.id = :id")
    Optional<Administrador> findByIdCompleto(@Param("id") Long id);
}


package com.cefet.vocealuga.repositories;

import com.cefet.vocealuga.entities.Gerente;
import com.cefet.vocealuga.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GerenteRepository extends JpaRepository<Usuario, Long> {
    Optional<Gerente> findByEmail(String email);
}


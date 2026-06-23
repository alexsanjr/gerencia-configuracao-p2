package com.cefet.vocealuga.repositories;

import com.cefet.vocealuga.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);

    Usuario findByDocumento(String documento);
}


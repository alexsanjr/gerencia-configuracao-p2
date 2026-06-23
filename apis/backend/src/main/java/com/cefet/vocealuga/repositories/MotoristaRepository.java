package com.cefet.vocealuga.repositories;

import com.cefet.vocealuga.entities.Motorista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MotoristaRepository extends JpaRepository<Motorista, Long> {

}
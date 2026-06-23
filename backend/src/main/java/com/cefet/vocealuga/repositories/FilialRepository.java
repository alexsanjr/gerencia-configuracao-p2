package com.cefet.vocealuga.repositories;

import com.cefet.vocealuga.entities.Filial;
import com.cefet.vocealuga.entities.Veiculo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FilialRepository extends JpaRepository<Filial, Long> {

}

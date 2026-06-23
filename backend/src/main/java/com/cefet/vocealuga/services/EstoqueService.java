package com.cefet.vocealuga.services;

import com.cefet.vocealuga.dtos.EstoqueDTO;
import com.cefet.vocealuga.entities.Estoque;
import com.cefet.vocealuga.entities.Filial;
import com.cefet.vocealuga.repositories.EstoqueRepository;
import com.cefet.vocealuga.repositories.FilialRepository;
import com.cefet.vocealuga.services.exceptions.DatabaseException;
import com.cefet.vocealuga.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EstoqueService {

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private FilialRepository filialRepository;

    @Transactional(readOnly = true)
    public EstoqueDTO findById(Long id) {

        Estoque estoque = estoqueRepository.findById(id).orElseThrow(() -> new RuntimeException("Estoque não encontrado"));
        return convertToDTO(estoque);
    }

    @Transactional(readOnly = true)
    public Page<EstoqueDTO> findAll(Pageable pageable) {
        Page<Estoque> result = estoqueRepository.findAll(pageable);
        return result.map(this::convertToDTO);
    }

    @Transactional
    public EstoqueDTO insert(EstoqueDTO dto) {
        Estoque entity = convertToEntity(dto);
        entity = estoqueRepository.save(entity);
        return convertToDTO(entity);
    }

    @Transactional
    public EstoqueDTO update(Long id, EstoqueDTO dto) {
        try {
            Estoque entity = estoqueRepository.getReferenceById(id);
            dto.setId(id);
            entity = convertToEntity(dto);
            entity = estoqueRepository.save(entity);
            return convertToDTO(entity);
        }
        catch (JpaObjectRetrievalFailureException e) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if(!estoqueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            estoqueRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }



    public EstoqueDTO convertToDTO(Estoque estoque) {
        EstoqueDTO dto = new EstoqueDTO();
        dto.setId(estoque.getId());
        dto.setNome(estoque.getNome());
        dto.setFilialId(estoque.getFilial().getId());
        return dto;
    }

    public Estoque convertToEntity(EstoqueDTO dto) {
        Estoque estoque = new Estoque();
        estoque.setId(dto.getId());
        estoque.setNome(dto.getNome());
        Filial filial = filialRepository.findById(dto.getFilialId())
                .orElseThrow(() -> new RuntimeException("Filial não encontrada"));
        filial.setId(dto.getFilialId());
        estoque.setFilial(filial);
        return estoque;
    }

}


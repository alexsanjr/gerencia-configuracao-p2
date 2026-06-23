package com.cefet.vocealuga.services;

import com.cefet.vocealuga.dtos.EstacaoDeServicoDTO;
import com.cefet.vocealuga.entities.EstacaoDeServico;
import com.cefet.vocealuga.repositories.EstacaoDeServicoRepository;
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
public class EstacaoDeServicoService {

    @Autowired
    private EstacaoDeServicoRepository repository;

    @Transactional(readOnly = true)
    public EstacaoDeServicoDTO findById(Long id) {

        EstacaoDeServico entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Estação de serviço não encontrada"));
        return convertToDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<EstacaoDeServicoDTO> findAll(Pageable pageable) {
        Page<EstacaoDeServico> result = repository.findAll(pageable);
        return result.map(this::convertToDTO);
    }

    @Transactional
    public EstacaoDeServicoDTO insert(EstacaoDeServicoDTO dto) {
        EstacaoDeServico entity = convertToEntity(dto);
        entity = repository.save(entity);
        return convertToDTO(entity);
    }

    @Transactional
    public EstacaoDeServicoDTO update(Long id, EstacaoDeServicoDTO dto) {
        try {
            EstacaoDeServico entity = repository.getReferenceById(id);
            dto.setId(entity.getId());
            entity = convertToEntity(dto);
            entity = repository.save(entity);
            return convertToDTO(entity);
        }
        catch (JpaObjectRetrievalFailureException e) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if(!repository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    public EstacaoDeServicoDTO convertToDTO(EstacaoDeServico entity) {
        EstacaoDeServicoDTO dto = new EstacaoDeServicoDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setLocal(entity.getLocal());
        return dto;
    }

    public EstacaoDeServico convertToEntity(EstacaoDeServicoDTO dto) {
        EstacaoDeServico entity = new EstacaoDeServico();
        entity.setId(dto.getId());
        entity.setNome(dto.getNome());
        entity.setLocal(dto.getLocal());
        return entity;
    }

}


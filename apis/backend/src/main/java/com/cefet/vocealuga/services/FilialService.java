package com.cefet.vocealuga.services;

import com.cefet.vocealuga.dtos.FilialDTO;
import com.cefet.vocealuga.entities.Filial;
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
public class FilialService {

    @Autowired
    private FilialRepository filialRepository;

    @Transactional(readOnly = true)
    public FilialDTO findById(Long id) {

        Filial filial = filialRepository.findById(id).orElseThrow(() -> new RuntimeException("Filial não encontrada"));
        return convertToDTO(filial);
    }

    @Transactional(readOnly = true)
    public Page<FilialDTO> findAll(Pageable pageable) {
        Page<Filial> result = filialRepository.findAll(pageable);
        return result.map(this::convertToDTO);
    }

    @Transactional
    public FilialDTO insert(FilialDTO dto) {
        Filial entity = convertToEntity(dto);
        entity = filialRepository.save(entity);
        return convertToDTO(entity);
    }

    @Transactional
    public FilialDTO update(Long id, FilialDTO dto) {
        try {
            Filial entity = filialRepository.getReferenceById(id);
            dto.setId(id);
            entity = convertToEntity(dto);
            entity = filialRepository.save(entity);
            return convertToDTO(entity);
        }
        catch (JpaObjectRetrievalFailureException e) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if(!filialRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            filialRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }



    public FilialDTO convertToDTO(Filial filial) {
        FilialDTO dto = new FilialDTO();
        dto.setId(filial.getId());
        dto.setNome(filial.getNome());
        dto.setLocal(filial.getLocal());
        return dto;
    }

    public Filial convertToEntity(FilialDTO dto) {
        Filial filial = new Filial();
        filial.setId(dto.getId());
        filial.setNome(dto.getNome());
        filial.setLocal(dto.getLocal());
        return filial;
    }

}


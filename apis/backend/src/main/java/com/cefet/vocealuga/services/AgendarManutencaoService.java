package com.cefet.vocealuga.services;

import com.cefet.vocealuga.dtos.AgendarManutencaoDTO;
import com.cefet.vocealuga.entities.AgendarManutencao;
import com.cefet.vocealuga.entities.EstacaoDeServico;
import com.cefet.vocealuga.entities.Veiculo;
import com.cefet.vocealuga.entities.enums.StatusVeiculo;
import com.cefet.vocealuga.repositories.AgendarManutencaoRepository;
import com.cefet.vocealuga.repositories.EstacaoDeServicoRepository;
import com.cefet.vocealuga.repositories.VeiculoRepository;
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

import java.time.Instant;

@Service
public class AgendarManutencaoService {

    @Autowired
    private AgendarManutencaoRepository repository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private EstacaoDeServicoRepository estacaoDeServicoRepository;

    @Transactional(readOnly = true)
    public AgendarManutencaoDTO findById(Long id) {

        AgendarManutencao entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Estação de serviço não encontrada"));
        return convertToDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<AgendarManutencaoDTO> findAll(Pageable pageable) {
        Page<AgendarManutencao> result = repository.findAll(pageable);
        return result.map(this::convertToDTO);
    }

    @Transactional
    public AgendarManutencaoDTO insert(AgendarManutencaoDTO dto) {
        Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado"));

        if (veiculo.getStatusVeiculo() != StatusVeiculo.DISPONIVEL) {
            throw new IllegalStateException("Veículo não está disponível para manutenção");
        }

        veiculo.setStatusVeiculo(StatusVeiculo.MANUTENCAO);
        veiculoRepository.save(veiculo);

        AgendarManutencao entity = convertToEntity(dto);
        entity = repository.save(entity);

        return convertToDTO(entity);
    }

    @Transactional
    public AgendarManutencaoDTO update(Long id, AgendarManutencaoDTO dto) {
        try {
            AgendarManutencao entity = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + id));
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

    public AgendarManutencaoDTO convertToDTO(AgendarManutencao entity) {
        AgendarManutencaoDTO dto = new AgendarManutencaoDTO();
        dto.setId(entity.getId());
        dto.setDataManutencao(entity.getDataManutencao());
        dto.setMotivoManutencao(entity.getMotivoManutencao());
        dto.setEstacaoDeServicoId(entity.getEstacaoDeServico().getId());
        dto.setVeiculoId(entity.getVeiculo().getId());
        return dto;
    }

    public AgendarManutencao convertToEntity(AgendarManutencaoDTO dto) {
        AgendarManutencao entity = new AgendarManutencao();
        entity.setId(dto.getId());
        if (dto.getDataManutencao() != null) {
            entity.setDataManutencao(dto.getDataManutencao());
        } else {
            entity.setDataManutencao(Instant.now());
        }
        entity.setMotivoManutencao(dto.getMotivoManutencao());

        Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() -> new RuntimeException("Filial não encontrada"));
        veiculo.setId(dto.getVeiculoId());
        entity.setVeiculo(veiculo);

        EstacaoDeServico estacaoDeServico = estacaoDeServicoRepository.findById(dto.getEstacaoDeServicoId())
                .orElseThrow(() -> new RuntimeException("Estação de serviço não encontrada"));
        estacaoDeServico.setId(dto.getEstacaoDeServicoId());
        entity.setEstacaoDeServico(estacaoDeServico);

        return entity;
    }

}


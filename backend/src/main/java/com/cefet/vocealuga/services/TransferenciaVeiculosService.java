package com.cefet.vocealuga.services;


import com.cefet.vocealuga.dtos.TransferenciaVeiculosDTO;
import com.cefet.vocealuga.entities.Estoque;
import com.cefet.vocealuga.entities.TransferenciaVeiculos;
import com.cefet.vocealuga.entities.Veiculo;
import com.cefet.vocealuga.repositories.EstoqueRepository;
import com.cefet.vocealuga.repositories.TransferenciaVeiculosRepository;
import com.cefet.vocealuga.repositories.VeiculoRepository;
import com.cefet.vocealuga.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransferenciaVeiculosService {

    @Autowired
    private TransferenciaVeiculosRepository transferenciaVeiculosRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Transactional(readOnly = true)
    public TransferenciaVeiculosDTO findById(Long id) {

        TransferenciaVeiculos transferenciaVeiculos = transferenciaVeiculosRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transferencia não encontrada"));
        return convertToDTO(transferenciaVeiculos);
    }

    @Transactional(readOnly = true)
    public Page<TransferenciaVeiculosDTO> findAll(Pageable pageable) {
        Page<TransferenciaVeiculos> result = transferenciaVeiculosRepository.findAll(pageable);
        return result.map(this::convertToDTO);
    }

    @Transactional
    public TransferenciaVeiculosDTO insert(TransferenciaVeiculosDTO dto) {
        TransferenciaVeiculos entity = convertToEntity(dto);
        entity = transferenciaVeiculosRepository.save(entity);
        return convertToDTO(entity);
    }

    @Transactional
    public TransferenciaVeiculosDTO update(Long id, TransferenciaVeiculosDTO dto) {
        try {
            TransferenciaVeiculos entity = transferenciaVeiculosRepository.getReferenceById(id);
            entity.setStatus(dto.getStatus());
            entity = transferenciaVeiculosRepository.save(entity);
            return convertToDTO(entity);
        }
        catch (JpaObjectRetrievalFailureException e) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
    }

    public TransferenciaVeiculosDTO convertToDTO(TransferenciaVeiculos entity) {
        TransferenciaVeiculosDTO dto = new TransferenciaVeiculosDTO();
        dto.setId(entity.getId());
        dto.setEstoqueOrigem(entity.getEstoqueOrigem().getId());
        dto.setEstoqueDestino(entity.getEstoqueDestino().getId());
        dto.setStatus(entity.getStatus());
        dto.setData(entity.getData());
        dto.setIdVeiculos(entity.getVeiculos().stream().map(Veiculo::getId).collect(Collectors.toSet()));
        return dto;
    }

    public TransferenciaVeiculos convertToEntity(TransferenciaVeiculosDTO dto) {
        TransferenciaVeiculos entity = new TransferenciaVeiculos();
        entity.setId(dto.getId());
        entity.setStatus(dto.getStatus());
        entity.setData(Instant.now());
        Estoque origem = estoqueRepository.findById(dto.getEstoqueOrigem())
                .orElseThrow(() -> new RuntimeException("Filial não encontrada"));
        Estoque destino = estoqueRepository.findById(dto.getEstoqueDestino())
                .orElseThrow(() -> new RuntimeException("Filial não encontrada"));
        entity.setEstoqueOrigem(origem);
        entity.setEstoqueDestino(destino);

        Set<Veiculo> veiculos = dto.getIdVeiculos()
                .stream()
                .map(id -> veiculoRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Veículo com ID " + id + " não encontrado")))
                .collect(Collectors.toSet());

        entity.setVeiculos(veiculos);
        return entity;
    }

}


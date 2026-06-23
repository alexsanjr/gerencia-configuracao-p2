package com.cefet.vocealuga.service;

import com.cefet.vocealuga.dtos.TransferenciaVeiculosDTO;
import com.cefet.vocealuga.entities.Estoque;
import com.cefet.vocealuga.entities.TransferenciaVeiculos;
import com.cefet.vocealuga.entities.Veiculo;
import com.cefet.vocealuga.entities.enums.StatusTransferencia;
import com.cefet.vocealuga.repositories.EstoqueRepository;
import com.cefet.vocealuga.repositories.TransferenciaVeiculosRepository;
import com.cefet.vocealuga.repositories.VeiculoRepository;
import com.cefet.vocealuga.services.TransferenciaVeiculosService;
import com.cefet.vocealuga.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransferenciaVeiculosServiceTest {

    @InjectMocks
    private TransferenciaVeiculosService service;

    @Mock
    private TransferenciaVeiculosRepository repository;

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private VeiculoRepository veiculoRepository;

    private Long existingId;
    private Long nonExistingId;
    private TransferenciaVeiculos transferencia;
    private TransferenciaVeiculosDTO transferenciaDTO;
    private Estoque estoqueOrigem;
    private Estoque estoqueDestino;
    private Set<Veiculo> veiculos;
    private Set<Long> veiculosIds;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        existingId = 1L;
        nonExistingId = 2L;

        // Preparando objetos de teste
        estoqueOrigem = new Estoque();
        estoqueOrigem.setId(1L);

        estoqueDestino = new Estoque();
        estoqueDestino.setId(2L);

        // Criando veículos para transferência
        veiculos = new HashSet<>();
        Veiculo veiculo1 = new Veiculo();
        veiculo1.setId(1L);
        Veiculo veiculo2 = new Veiculo();
        veiculo2.setId(2L);
        veiculos.add(veiculo1);
        veiculos.add(veiculo2);

        // IDs dos veículos
        veiculosIds = new HashSet<>();
        veiculosIds.add(1L);
        veiculosIds.add(2L);

        // Criando transferência
        Instant data = Instant.now();
        transferencia = new TransferenciaVeiculos(existingId, estoqueOrigem, estoqueDestino, data, StatusTransferencia.PENDENTE);
        transferencia.setVeiculos(veiculos);

        // Criando DTO
        transferenciaDTO = new TransferenciaVeiculosDTO();
        transferenciaDTO.setId(existingId);
        transferenciaDTO.setEstoqueOrigem(estoqueOrigem.getId());
        transferenciaDTO.setEstoqueDestino(estoqueDestino.getId());
        transferenciaDTO.setData(data);
        transferenciaDTO.setIdVeiculos(veiculosIds);
        transferenciaDTO.setStatus(StatusTransferencia.PENDENTE);

        // Configurando comportamentos dos mocks
        when(repository.findById(existingId)).thenReturn(Optional.of(transferencia));
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
        when(repository.getReferenceById(existingId)).thenReturn(transferencia);
        when(repository.getReferenceById(nonExistingId)).thenThrow(JpaObjectRetrievalFailureException.class);
        when(repository.save(any(TransferenciaVeiculos.class))).thenReturn(transferencia);

        Page<TransferenciaVeiculos> page = new PageImpl<>(List.of(transferencia));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        when(estoqueRepository.findById(estoqueOrigem.getId())).thenReturn(Optional.of(estoqueOrigem));
        when(estoqueRepository.findById(estoqueDestino.getId())).thenReturn(Optional.of(estoqueDestino));

        for (Veiculo v : veiculos) {
            when(veiculoRepository.findById(v.getId())).thenReturn(Optional.of(v));
        }
    }

    @Test
    void deveEncontrarTransferenciaPorId() {
        // Act
        TransferenciaVeiculosDTO resultado = service.findById(existingId);

        // Assert
        assertNotNull(resultado);
        assertEquals(existingId, resultado.getId());
        assertEquals(estoqueOrigem.getId(), resultado.getEstoqueOrigem());
        assertEquals(estoqueDestino.getId(), resultado.getEstoqueDestino());
        assertEquals(StatusTransferencia.PENDENTE, resultado.getStatus());
        assertEquals(2, resultado.getIdVeiculos().size());

        // Verify
        verify(repository).findById(existingId);
    }

    @Test
    void deveLancarExcecaoAoBuscarIdInexistente() {
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));

        // Verify
        verify(repository).findById(nonExistingId);
    }

    @Test
    void deveListarTodasTransferencias() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<TransferenciaVeiculosDTO> resultado = service.findAll(pageable);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());

        // Verify
        verify(repository).findAll(pageable);
    }

    @Test
    void deveCriarNovaTransferenciaComSucesso() {
        // Arrange - configurando o comportamento do save para refletir os dados inseridos
        when(repository.save(any(TransferenciaVeiculos.class))).thenAnswer(invocation -> {
            TransferenciaVeiculos entity = invocation.getArgument(0);
            entity.setId(existingId);
            return entity;
        });

        // Act
        TransferenciaVeiculosDTO resultado = service.insert(transferenciaDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(existingId, resultado.getId());
        assertEquals(estoqueOrigem.getId(), resultado.getEstoqueOrigem());
        assertEquals(estoqueDestino.getId(), resultado.getEstoqueDestino());
        assertEquals(StatusTransferencia.PENDENTE, resultado.getStatus());
        assertEquals(2, resultado.getIdVeiculos().size());

        // Verify
        verify(estoqueRepository).findById(estoqueOrigem.getId());
        verify(estoqueRepository).findById(estoqueDestino.getId());
        verify(veiculoRepository, times(2)).findById(any());
        verify(repository).save(any(TransferenciaVeiculos.class));
    }

    @Test
    void deveLancarExcecaoAoInserirComEstoqueInexistente() {
        // Arrange
        when(estoqueRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        transferenciaDTO.setEstoqueOrigem(nonExistingId);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.insert(transferenciaDTO));
        assertTrue(exception.getMessage().contains("Filial não encontrada"));

        // Verify
        verify(estoqueRepository).findById(nonExistingId);
        verify(repository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoInserirComVeiculoInexistente() {
        // Arrange
        Long idVeiculoInexistente = 99L;
        transferenciaDTO.setIdVeiculos(Set.of(idVeiculoInexistente));
        when(veiculoRepository.findById(idVeiculoInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.insert(transferenciaDTO));
        assertTrue(exception.getMessage().contains("Veículo com ID"));

        // Verify
        verify(veiculoRepository).findById(idVeiculoInexistente);
        verify(repository, never()).save(any());
    }

    @Test
    void deveAtualizarStatusTransferencia() {
        // Arrange
        TransferenciaVeiculosDTO atualizacaoDTO = new TransferenciaVeiculosDTO();
        atualizacaoDTO.setStatus(StatusTransferencia.CONCLUIDO);

        when(repository.save(any(TransferenciaVeiculos.class))).thenAnswer(invocation -> {
            TransferenciaVeiculos entity = invocation.getArgument(0);
            entity.setStatus(StatusTransferencia.CONCLUIDO);
            return entity;
        });

        // Act
        TransferenciaVeiculosDTO resultado = service.update(existingId, atualizacaoDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(existingId, resultado.getId());
        assertEquals(StatusTransferencia.CONCLUIDO, resultado.getStatus());

        // Verify
        verify(repository).getReferenceById(existingId);
        verify(repository).save(any(TransferenciaVeiculos.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarTransferenciaInexistente() {
        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> service.update(nonExistingId, transferenciaDTO));

        // Verify
        verify(repository).getReferenceById(nonExistingId);
        verify(repository, never()).save(any());
    }

    @Test
    void deveConverterEntityParaDTO() {
        // Act
        TransferenciaVeiculosDTO dto = service.convertToDTO(transferencia);

        // Assert
        assertNotNull(dto);
        assertEquals(transferencia.getId(), dto.getId());
        assertEquals(transferencia.getEstoqueOrigem().getId(), dto.getEstoqueOrigem());
        assertEquals(transferencia.getEstoqueDestino().getId(), dto.getEstoqueDestino());
        assertEquals(transferencia.getData(), dto.getData());
        assertEquals(transferencia.getStatus(), dto.getStatus());
        assertEquals(2, dto.getIdVeiculos().size());
        assertTrue(dto.getIdVeiculos().contains(1L));
        assertTrue(dto.getIdVeiculos().contains(2L));
    }

    @Test
    void deveConverterDTOParaEntity() {
        // Act
        TransferenciaVeiculos entity = service.convertToEntity(transferenciaDTO);

        // Assert
        assertNotNull(entity);
        assertEquals(transferenciaDTO.getId(), entity.getId());
        assertEquals(transferenciaDTO.getEstoqueOrigem(), entity.getEstoqueOrigem().getId());
        assertEquals(transferenciaDTO.getEstoqueDestino(), entity.getEstoqueDestino().getId());
        assertEquals(transferenciaDTO.getStatus(), entity.getStatus());
        assertEquals(2, entity.getVeiculos().size());

        // Verify
        verify(estoqueRepository).findById(estoqueOrigem.getId());
        verify(estoqueRepository).findById(estoqueDestino.getId());
        verify(veiculoRepository, times(2)).findById(any());
    }
}
package com.cefet.vocealuga.service;

import com.cefet.vocealuga.dtos.AgendarManutencaoDTO;
import com.cefet.vocealuga.entities.AgendarManutencao;
import com.cefet.vocealuga.entities.EstacaoDeServico;
import com.cefet.vocealuga.entities.Veiculo;
import com.cefet.vocealuga.entities.enums.StatusVeiculo;
import com.cefet.vocealuga.repositories.AgendarManutencaoRepository;
import com.cefet.vocealuga.repositories.EstacaoDeServicoRepository;
import com.cefet.vocealuga.repositories.VeiculoRepository;
import com.cefet.vocealuga.services.AgendarManutencaoService;
import com.cefet.vocealuga.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AgendarManutencaoServiceTest {

    @InjectMocks
    private AgendarManutencaoService service;

    @Mock
    private AgendarManutencaoRepository repository;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private EstacaoDeServicoRepository estacaoDeServicoRepository;

    private Long existingId;
    private Long nonExistingId;
    private Veiculo veiculo;
    private EstacaoDeServico estacaoDeServico;
    private AgendarManutencao agendarManutencao;
    private AgendarManutencaoDTO agendarManutencaoDTO;
    private Instant dataManutencao;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        existingId = 1L;
        nonExistingId = 2L;
        dataManutencao = Instant.now().plusSeconds(86400); // 1 dia à frente

        veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setStatusVeiculo(StatusVeiculo.DISPONIVEL);

        estacaoDeServico = new EstacaoDeServico();
        estacaoDeServico.setId(1L);

        agendarManutencao = new AgendarManutencao();
        agendarManutencao.setId(existingId);
        agendarManutencao.setVeiculo(veiculo);
        agendarManutencao.setEstacaoDeServico(estacaoDeServico);
        agendarManutencao.setMotivoManutencao("Revisão programada");
        agendarManutencao.setDataManutencao(dataManutencao);

        agendarManutencaoDTO = new AgendarManutencaoDTO();
        agendarManutencaoDTO.setId(existingId);
        agendarManutencaoDTO.setVeiculoId(veiculo.getId());
        agendarManutencaoDTO.setEstacaoDeServicoId(estacaoDeServico.getId());
        agendarManutencaoDTO.setMotivoManutencao("Revisão programada");
        agendarManutencaoDTO.setDataManutencao(dataManutencao);

        when(repository.findById(existingId)).thenReturn(Optional.of(agendarManutencao));
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        when(veiculoRepository.findById(veiculo.getId())).thenReturn(Optional.of(veiculo));
        when(estacaoDeServicoRepository.findById(estacaoDeServico.getId())).thenReturn(Optional.of(estacaoDeServico));

        when(repository.save(any(AgendarManutencao.class))).thenReturn(agendarManutencao);
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        Page<AgendarManutencao> page = new PageImpl<>(List.of(agendarManutencao));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);
    }

    @Test
    void deveAgendarManutencaoComVeiculoDisponivel() {
        // Arrange
        AgendarManutencaoDTO novoAgendamento = new AgendarManutencaoDTO();
        novoAgendamento.setVeiculoId(veiculo.getId());
        novoAgendamento.setEstacaoDeServicoId(estacaoDeServico.getId());
        novoAgendamento.setMotivoManutencao("Troca de óleo e filtros");
        novoAgendamento.setDataManutencao(dataManutencao);

        // Configuração específica para este teste
        veiculo.setStatusVeiculo(StatusVeiculo.DISPONIVEL);

        // Capturador para verificar se o status do veículo é alterado
        ArgumentCaptor<Veiculo> veiculoCaptor = ArgumentCaptor.forClass(Veiculo.class);

        // Act
        AgendarManutencaoDTO resultado = service.insert(novoAgendamento);

        // Assert
        assertNotNull(resultado);
        assertEquals(existingId, resultado.getId());

        // Verificar se o veiculoRepository.save foi chamado com o veículo com status alterado para MANUTENCAO
        verify(veiculoRepository).save(veiculoCaptor.capture());
        assertEquals(StatusVeiculo.MANUTENCAO, veiculoCaptor.getValue().getStatusVeiculo());

        // Verificações adicionais
        verify(veiculoRepository, times(2)).findById(veiculo.getId());
        verify(estacaoDeServicoRepository, times(1)).findById(estacaoDeServico.getId());

        verify(repository, times(1)).save(any(AgendarManutencao.class));
    }

    @Test
    void deveLancarExcecaoAoAgendarManutencaoComVeiculoIndisponivel() {
        // Arrange
        AgendarManutencaoDTO novoAgendamento = new AgendarManutencaoDTO();
        novoAgendamento.setVeiculoId(veiculo.getId());
        novoAgendamento.setEstacaoDeServicoId(estacaoDeServico.getId());
        novoAgendamento.setMotivoManutencao("Troca de óleo e filtros");
        novoAgendamento.setDataManutencao(dataManutencao);

        // Configurando veículo para estar em uso (não disponível)
        veiculo.setStatusVeiculo(StatusVeiculo.EM_USO);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> service.insert(novoAgendamento));

        // Verificações
        verify(veiculoRepository, times(1)).findById(veiculo.getId());
        verify(veiculoRepository, never()).save(any(Veiculo.class));
        verify(repository, never()).save(any(AgendarManutencao.class));
    }

    @Test
    void deveLancarExcecaoAoAgendarManutencaoComVeiculoJaEmManutencao() {
        // Arrange
        AgendarManutencaoDTO novoAgendamento = new AgendarManutencaoDTO();
        novoAgendamento.setVeiculoId(veiculo.getId());
        novoAgendamento.setEstacaoDeServicoId(estacaoDeServico.getId());
        novoAgendamento.setMotivoManutencao("Troca de óleo e filtros");
        novoAgendamento.setDataManutencao(dataManutencao);

        // Configurando veículo para já estar em manutenção
        veiculo.setStatusVeiculo(StatusVeiculo.MANUTENCAO);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> service.insert(novoAgendamento));

        // Verificações
        verify(veiculoRepository, times(1)).findById(veiculo.getId());
        verify(veiculoRepository, never()).save(any(Veiculo.class));
        verify(repository, never()).save(any(AgendarManutencao.class));
    }

    @Test
    void deveLancarExcecaoAoAgendarManutencaoComVeiculoReservado() {
        // Arrange
        AgendarManutencaoDTO novoAgendamento = new AgendarManutencaoDTO();
        novoAgendamento.setVeiculoId(veiculo.getId());
        novoAgendamento.setEstacaoDeServicoId(estacaoDeServico.getId());
        novoAgendamento.setMotivoManutencao("Troca de óleo e filtros");
        novoAgendamento.setDataManutencao(dataManutencao);

        // Configurando veículo para estar reservado
        veiculo.setStatusVeiculo(StatusVeiculo.RESERVADO);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> service.insert(novoAgendamento));

        // Verificações
        verify(veiculoRepository, times(1)).findById(veiculo.getId());
        verify(veiculoRepository, never()).save(any(Veiculo.class));
        verify(repository, never()).save(any(AgendarManutencao.class));
    }

    @Test
    void deveBuscarAgendamentoPorId() {
        AgendarManutencaoDTO resultado = service.findById(existingId);

        assertNotNull(resultado);
        assertEquals(existingId, resultado.getId());
        assertEquals("Revisão programada", resultado.getMotivoManutencao());
        assertEquals(veiculo.getId(), resultado.getVeiculoId());
        assertEquals(estacaoDeServico.getId(), resultado.getEstacaoDeServicoId());

        verify(repository).findById(existingId);
    }

    @Test
    void deveLancarExcecaoQuandoBuscarPorIdInexistente() {
        assertThrows(RuntimeException.class, () -> service.findById(nonExistingId));

        verify(repository).findById(nonExistingId);
    }

    @Test
    void deveListarTodosAgendamentos() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<AgendarManutencaoDTO> resultado = service.findAll(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals(existingId, resultado.getContent().get(0).getId());

        verify(repository).findAll(pageable);
    }

    @Test
    void deveAtualizarAgendamentoExistente() {
        // Arrange
        AgendarManutencaoDTO atualizacao = new AgendarManutencaoDTO();
        atualizacao.setVeiculoId(veiculo.getId());
        atualizacao.setEstacaoDeServicoId(estacaoDeServico.getId());
        atualizacao.setMotivoManutencao("Manutenção preventiva alterada");
        atualizacao.setDataManutencao(dataManutencao.plusSeconds(3600)); // +1 hora

        // Configurando o mock para retornar uma entidade com os valores atualizados
        when(repository.save(any(AgendarManutencao.class))).thenAnswer(invocation -> {
            AgendarManutencao savedEntity = invocation.getArgument(0);
            savedEntity.setId(existingId); // Mantém o ID conforme esperado
            return savedEntity;
        });

        // Act
        AgendarManutencaoDTO resultado = service.update(existingId, atualizacao);

        // Assert
        assertNotNull(resultado);
        assertEquals(existingId, resultado.getId());
        assertEquals("Manutenção preventiva alterada", resultado.getMotivoManutencao());

        // Verifica se os métodos corretos foram chamados
        verify(repository).findById(existingId);
        verify(veiculoRepository).findById(veiculo.getId());
        verify(estacaoDeServicoRepository).findById(estacaoDeServico.getId());
        verify(repository).save(any(AgendarManutencao.class));
    }

    @Test
    void deveLancarExcecaoAoAtualizarAgendamentoInexistente() {
        when(repository.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, agendarManutencaoDTO);
        });

        verify(repository).findById(nonExistingId);
    }

    @Test
    void deveDeletarAgendamentoExistente() {
        when(repository.existsById(existingId)).thenReturn(true);
        doNothing().when(repository).deleteById(existingId);

        assertDoesNotThrow(() -> service.delete(existingId));

        verify(repository).existsById(existingId);
        verify(repository).deleteById(existingId);
    }

    @Test
    void deveLancarExcecaoAoDeletarAgendamentoInexistente() {
        when(repository.existsById(nonExistingId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));

        verify(repository).existsById(nonExistingId);
        verify(repository, never()).deleteById(nonExistingId);
    }
}
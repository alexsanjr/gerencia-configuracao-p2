package com.cefet.vocealuga.service;

import com.cefet.vocealuga.dtos.ReservaDTO;
import com.cefet.vocealuga.entities.*;
import com.cefet.vocealuga.entities.enums.StatusReserva;
import com.cefet.vocealuga.entities.enums.StatusVeiculo;
import com.cefet.vocealuga.entities.enums.TipoReserva;
import com.cefet.vocealuga.repositories.*;
import com.cefet.vocealuga.services.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReservaServiceTest {

    private ReservaRepository reservaRepository;
    private FilialRepository filialRepository;
    private UsuarioRepository usuarioRepository;
    private VeiculoRepository veiculoRepository;
    private MotoristaRepository motoristaRepository;
    private ReservaService reservaService;

    @BeforeEach
    void setUp() throws Exception {
        reservaRepository = mock(ReservaRepository.class);
        filialRepository = mock(FilialRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        veiculoRepository = mock(VeiculoRepository.class);
        motoristaRepository = mock(MotoristaRepository.class);
        reservaService = new ReservaService();

        var reservaField = ReservaService.class.getDeclaredField("repository");
        reservaField.setAccessible(true);
        reservaField.set(reservaService, reservaRepository);

        var filialField = ReservaService.class.getDeclaredField("filialRepository");
        filialField.setAccessible(true);
        filialField.set(reservaService, filialRepository);

        var usuarioField = ReservaService.class.getDeclaredField("usuarioRepository");
        usuarioField.setAccessible(true);
        usuarioField.set(reservaService, usuarioRepository);

        var veiculoField = ReservaService.class.getDeclaredField("veiculoRepository");
        veiculoField.setAccessible(true);
        veiculoField.set(reservaService, veiculoRepository);

        var motoristaField = ReservaService.class.getDeclaredField("motoristaRepository");
        motoristaField.setAccessible(true);
        motoristaField.set(reservaService, motoristaRepository);
    }

    @Test
    void deveCriarReservaComMotorista() {
        ReservaDTO dto = new ReservaDTO();
        dto.setUsuarioId(1L);
        dto.setLocalRetiradaId(2L);
        dto.setCategoria(TipoReserva.ANTECIPADA);
        dto.setStatus(StatusReserva.PENDENTE);
        dto.setDataReserva(LocalDate.now());
        dto.setDataVencimento(LocalDate.now().plusDays(3));
        dto.setVeiculoId(1L);
        dto.setMotoristaId(5L);

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Filial filial = new Filial();
        filial.setId(2L);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(1L);

        Motorista motorista = new Motorista();
        motorista.setId(5L);
        motorista.setNome("João Silva");
        motorista.setCnh("12345678901");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(filialRepository.findById(2L)).thenReturn(Optional.of(filial));
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(motoristaRepository.findById(5L)).thenReturn(Optional.of(motorista));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva r = invocation.getArgument(0);
            r.setId(100L);
            return r;
        });

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(usuario);

        ReservaDTO result = reservaService.insert(dto, authentication);

        ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
        verify(reservaRepository).save(captor.capture());
        Reserva reservaSalva = captor.getValue();

        assertEquals(StatusReserva.PENDENTE, reservaSalva.getStatus());
        assertEquals(5L, result.getMotoristaId());
        assertEquals(motorista, reservaSalva.getMotorista());
    }

    @Test
    void deveFalharQuandoMotoristaIdAusente() {
        ReservaDTO dto = new ReservaDTO();
        dto.setUsuarioId(1L);
        dto.setLocalRetiradaId(2L);
        dto.setVeiculoId(3L);
        dto.setCategoria(TipoReserva.ANTECIPADA);
        dto.setStatus(StatusReserva.PENDENTE);
        dto.setDataReserva(LocalDate.now());
        dto.setDataVencimento(LocalDate.now().plusDays(3));
        // Motorista ID não definido

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(usuario);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservaService.insert(dto, authentication));

        assertEquals("Motorista é obrigatório", exception.getMessage());
    }

    @Test
    void deveCriarReservaComStatusEmAndamento() {
        ReservaDTO dto = new ReservaDTO();
        dto.setUsuarioId(1L);
        dto.setLocalRetiradaId(2L);
        dto.setCategoria(TipoReserva.ANTECIPADA);
        dto.setStatus(StatusReserva.EM_ANDAMENTO); // Definindo explicitamente como EM_ANDAMENTO
        dto.setDataReserva(LocalDate.now());
        dto.setDataVencimento(LocalDate.now().plusDays(3));
        dto.setVeiculoId(1L);
        dto.setMotoristaId(5L);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Filial filial = new Filial();
        filial.setId(2L);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(1L);

        Motorista motorista = new Motorista();
        motorista.setId(5L);
        motorista.setNome("João Silva");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(filialRepository.findById(2L)).thenReturn(Optional.of(filial));
        when(veiculoRepository.findById(1L)).thenReturn(Optional.of(veiculo));
        when(motoristaRepository.findById(5L)).thenReturn(Optional.of(motorista));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva r = invocation.getArgument(0);
            return r;
        });

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(usuario);

        ReservaDTO result = reservaService.insert(dto, authentication);

        ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
        verify(reservaRepository).save(captor.capture());
        Reserva reservaSalva = captor.getValue();

        assertEquals(StatusReserva.PENDENTE, reservaSalva.getStatus());
        assertEquals(StatusReserva.PENDENTE, result.getStatus());
    }

    @Test
    void deveAtualizarReservaComNovoMotorista() {
        // Motorista original
        Motorista motoristaOriginal = new Motorista();
        motoristaOriginal.setId(5L);
        motoristaOriginal.setNome("João Silva");

        // Novo motorista
        Motorista novoMotorista = new Motorista();
        novoMotorista.setId(6L);
        novoMotorista.setNome("Maria Oliveira");

        // Reserva original
        Reserva reserva = new Reserva();
        reserva.setId(400L);
        reserva.setStatus(StatusReserva.PENDENTE);
        reserva.setDataReserva(LocalDate.now());
        reserva.setDataVencimento(LocalDate.now().plusDays(2));
        reserva.setCategoria(TipoReserva.ANTECIPADA);
        reserva.setMotorista(motoristaOriginal);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        reserva.setUsuario(usuario);

        Filial filial = new Filial();
        filial.setId(2L);
        reserva.setLocalRetirada(filial);

        // Mock busca reserva
        when(reservaRepository.findById(400L)).thenReturn(Optional.of(reserva));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(filialRepository.findById(3L)).thenReturn(Optional.of(filial));
        when(motoristaRepository.findById(6L)).thenReturn(Optional.of(novoMotorista));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        ReservaDTO dto = new ReservaDTO();
        dto.setId(400L);
        dto.setUsuarioId(1L);
        dto.setLocalRetiradaId(3L);
        dto.setCategoria(TipoReserva.IMEDIATA);
        dto.setStatus(StatusReserva.PENDENTE);
        dto.setDataReserva(LocalDate.now().plusDays(1));
        dto.setDataVencimento(LocalDate.now().plusDays(4));
        dto.setMotoristaId(6L);  // Atualizar para novo motorista

        ReservaDTO atualizado = reservaService.update(400L, dto);

        ArgumentCaptor<Reserva> captor = ArgumentCaptor.forClass(Reserva.class);
        verify(reservaRepository).save(captor.capture());
        Reserva reservaSalva = captor.getValue();

        assertEquals(400L, atualizado.getId());
        assertEquals(6L, atualizado.getMotoristaId());
        assertEquals(novoMotorista, reservaSalva.getMotorista());
    }

    @Test
    void deveFalharQuandoMotoristaInexistente() {
        ReservaDTO dto = new ReservaDTO();
        dto.setUsuarioId(1L);
        dto.setLocalRetiradaId(2L);
        dto.setVeiculoId(3L);
        dto.setCategoria(TipoReserva.ANTECIPADA);
        dto.setStatus(StatusReserva.PENDENTE);
        dto.setDataReserva(LocalDate.now());
        dto.setDataVencimento(LocalDate.now().plusDays(3));
        dto.setMotoristaId(999L); // ID inexistente

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Filial filial = new Filial();
        filial.setId(2L);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(3L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(filialRepository.findById(2L)).thenReturn(Optional.of(filial));
        when(veiculoRepository.findById(3L)).thenReturn(Optional.of(veiculo));
        when(motoristaRepository.findById(999L)).thenReturn(Optional.empty());

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(usuario);

        assertThrows(RuntimeException.class, () -> reservaService.insert(dto, authentication));
    }

    @Test
    void naoDeveCriarReservaComDataInvalida() {
        ReservaDTO dto = new ReservaDTO();
        dto.setUsuarioId(1L);
        dto.setLocalRetiradaId(2L);
        dto.setVeiculoId(1L);
        dto.setMotoristaId(5L);
        dto.setCategoria(TipoReserva.ANTECIPADA);
        dto.setStatus(StatusReserva.PENDENTE);
        LocalDate hoje = LocalDate.now();
        dto.setDataReserva(hoje);
        dto.setDataVencimento(hoje);

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(usuario);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservaService.insert(dto, authentication));

        assertEquals("Data de vencimento deve ser posterior à data de reserva", exception.getMessage());
    }

    @Test
    void deveTrocarVeiculoNaAtualizacao() {
        // Veículo original
        Veiculo veiculoOriginal = new Veiculo();
        veiculoOriginal.setId(10L);
        veiculoOriginal.setStatusVeiculo(StatusVeiculo.RESERVADO);

        // Novo veículo
        Veiculo novoVeiculo = new Veiculo();
        novoVeiculo.setId(20L);
        novoVeiculo.setStatusVeiculo(StatusVeiculo.DISPONIVEL);

        // Reserva original
        Reserva reserva = new Reserva();
        reserva.setId(600L);
        reserva.setStatus(StatusReserva.PENDENTE);
        reserva.setDataReserva(LocalDate.now());
        reserva.setDataVencimento(LocalDate.now().plusDays(2));
        reserva.setCategoria(TipoReserva.ANTECIPADA);
        reserva.setVeiculo(veiculoOriginal);

        Motorista motorista = new Motorista();
        motorista.setId(5L);
        reserva.setMotorista(motorista);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        reserva.setUsuario(usuario);

        Filial filial = new Filial();
        filial.setId(2L);
        reserva.setLocalRetirada(filial);

        // Configurar mocks
        when(reservaRepository.findById(600L)).thenReturn(Optional.of(reserva));
        when(veiculoRepository.findById(20L)).thenReturn(Optional.of(novoVeiculo));
        when(motoristaRepository.findById(5L)).thenReturn(Optional.of(motorista));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(filialRepository.findById(2L)).thenReturn(Optional.of(filial));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        // DTO para atualização
        ReservaDTO dto = new ReservaDTO();
        dto.setId(600L);
        dto.setUsuarioId(1L);
        dto.setLocalRetiradaId(2L);
        dto.setMotoristaId(5L);
        dto.setVeiculoId(20L); // Novo veículo
        dto.setCategoria(TipoReserva.ANTECIPADA);
        dto.setStatus(StatusReserva.PENDENTE);
        dto.setDataReserva(LocalDate.now().plusDays(1));
        dto.setDataVencimento(LocalDate.now().plusDays(5));

        // Executar atualização
        ReservaDTO atualizado = reservaService.update(600L, dto);

        // Verificar que os veículos foram atualizados corretamente
        ArgumentCaptor<Veiculo> veiculoCaptor = ArgumentCaptor.forClass(Veiculo.class);
        verify(veiculoRepository, times(2)).save(veiculoCaptor.capture());

        // O primeiro veículo salvo deve ser o original com status DISPONIVEL
        List<Veiculo> veiculosSalvos = veiculoCaptor.getAllValues();
        assertEquals(StatusVeiculo.DISPONIVEL, veiculosSalvos.get(0).getStatusVeiculo());
        assertEquals(10L, veiculosSalvos.get(0).getId());

        // O segundo veículo salvo deve ser o novo com status RESERVADO
        assertEquals(StatusVeiculo.RESERVADO, veiculosSalvos.get(1).getStatusVeiculo());
        assertEquals(20L, veiculosSalvos.get(1).getId());

        // Verificar que a reserva foi atualizada com o novo veículo
        assertEquals(20L, atualizado.getVeiculoId());
    }

    @Test
    void deveEncontrarReservaPorId() {
        // Preparar dados de teste
        Reserva reserva = new Reserva();
        reserva.setId(700L);
        reserva.setStatus(StatusReserva.PENDENTE);
        reserva.setDataReserva(LocalDate.now());
        reserva.setDataVencimento(LocalDate.now().plusDays(2));
        reserva.setCategoria(TipoReserva.ANTECIPADA);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        reserva.setUsuario(usuario);

        Filial filial = new Filial();
        filial.setId(2L);
        reserva.setLocalRetirada(filial);

        Veiculo veiculo = new Veiculo();
        veiculo.setId(3L);
        reserva.setVeiculo(veiculo);

        Motorista motorista = new Motorista();
        motorista.setId(4L);
        reserva.setMotorista(motorista);

        // Configurar mock
        when(reservaRepository.findById(700L)).thenReturn(Optional.of(reserva));

        // Executar busca
        ReservaDTO encontrado = reservaService.findById(700L);

        // Verificar dados
        assertEquals(700L, encontrado.getId());
        assertEquals(StatusReserva.PENDENTE, encontrado.getStatus());
        assertEquals(TipoReserva.ANTECIPADA, encontrado.getCategoria());
        assertEquals(1L, encontrado.getUsuarioId());
        assertEquals(2L, encontrado.getLocalRetiradaId());
        assertEquals(3L, encontrado.getVeiculoId());
        assertEquals(4L, encontrado.getMotoristaId());
    }

    @Test
    void deveDeletarReservaExistente() {
        // Configurar mock para verificar existência da reserva
        when(reservaRepository.existsById(800L)).thenReturn(true);

        // Executar deleção
        reservaService.delete(800L);
        verify(reservaRepository).deleteById(800L);
    }

    @Test
    void naoDeveCriarReservaSemVeiculo() {
        ReservaDTO dto = new ReservaDTO();
        dto.setUsuarioId(1L);
        dto.setLocalRetiradaId(2L);
        dto.setMotoristaId(3L);
        dto.setCategoria(TipoReserva.ANTECIPADA);
        dto.setStatus(StatusReserva.PENDENTE);
        dto.setDataReserva(LocalDate.now());
        dto.setDataVencimento(LocalDate.now().plusDays(3));
        // Veículo ID não definido

        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(usuario);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> reservaService.insert(dto, authentication));

        assertEquals("Veículo é obrigatório", exception.getMessage());
    }

    @Test
    void deveAtualizarStatusVeiculoQuandoCancelarReserva() {
        // Configuração do veículo
        Veiculo veiculo = new Veiculo();
        veiculo.setId(30L);
        veiculo.setStatusVeiculo(StatusVeiculo.RESERVADO);

        // Configuração da reserva original
        Reserva reserva = new Reserva();
        reserva.setId(700L);
        reserva.setStatus(StatusReserva.PENDENTE);
        reserva.setDataReserva(LocalDate.now());
        reserva.setDataVencimento(LocalDate.now().plusDays(5));
        reserva.setVeiculo(veiculo);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        reserva.setUsuario(usuario);

        Filial filial = new Filial();
        filial.setId(2L);
        reserva.setLocalRetirada(filial);

        Motorista motorista = new Motorista();
        motorista.setId(3L);
        reserva.setMotorista(motorista);

        // Configurar mocks
        when(reservaRepository.findById(700L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        // DTO para atualização - cancela a reserva
        ReservaDTO dto = new ReservaDTO();
        dto.setId(700L);
        dto.setStatus(StatusReserva.CANCELADO);
        dto.setDataReserva(LocalDate.now());
        dto.setDataVencimento(LocalDate.now().plusDays(5));

        // Executar atualização
        reservaService.update(700L, dto);

        // Verificar que o status do veículo foi alterado para DISPONIVEL
        ArgumentCaptor<Veiculo> captor = ArgumentCaptor.forClass(Veiculo.class);
        verify(veiculoRepository).save(captor.capture());

        Veiculo veiculoAtualizado = captor.getValue();
        assertEquals(StatusVeiculo.DISPONIVEL, veiculoAtualizado.getStatusVeiculo());
        assertEquals(30L, veiculoAtualizado.getId());
    }

    @Test
    void deveAtualizarStatusVeiculoQuandoColocarEmAndamento() {
        // Configuração do veículo
        Veiculo veiculo = new Veiculo();
        veiculo.setId(50L);
        veiculo.setStatusVeiculo(StatusVeiculo.RESERVADO);

        // Configuração da reserva original
        Reserva reserva = new Reserva();
        reserva.setId(900L);
        reserva.setStatus(StatusReserva.PENDENTE);
        reserva.setDataReserva(LocalDate.now());
        reserva.setDataVencimento(LocalDate.now().plusDays(5));
        reserva.setVeiculo(veiculo);

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        reserva.setUsuario(usuario);

        Filial filial = new Filial();
        filial.setId(2L);
        reserva.setLocalRetirada(filial);

        Motorista motorista = new Motorista();
        motorista.setId(3L);
        reserva.setMotorista(motorista);

        // Configurar mocks
        when(reservaRepository.findById(900L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        // DTO para atualização - coloca em andamento
        ReservaDTO dto = new ReservaDTO();
        dto.setId(900L);
        dto.setStatus(StatusReserva.EM_ANDAMENTO);
        dto.setDataReserva(LocalDate.now());
        dto.setDataVencimento(LocalDate.now().plusDays(5));

        // Executar atualização
        reservaService.update(900L, dto);

        // Verificar que o status do veículo foi alterado para EM_USO
        ArgumentCaptor<Veiculo> captor = ArgumentCaptor.forClass(Veiculo.class);
        verify(veiculoRepository).save(captor.capture());

        Veiculo veiculoAtualizado = captor.getValue();
        assertEquals(StatusVeiculo.EM_USO, veiculoAtualizado.getStatusVeiculo());
        assertEquals(50L, veiculoAtualizado.getId());
    }
}
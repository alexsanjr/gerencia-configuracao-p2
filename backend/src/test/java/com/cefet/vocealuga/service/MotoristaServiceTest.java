package com.cefet.vocealuga.service;

import com.cefet.vocealuga.dtos.MeDTO;
import com.cefet.vocealuga.dtos.MotoristaDTO;
import com.cefet.vocealuga.entities.Motorista;
import com.cefet.vocealuga.entities.Usuario;
import com.cefet.vocealuga.repositories.MotoristaRepository;
import com.cefet.vocealuga.repositories.UsuarioRepository;
import com.cefet.vocealuga.services.AuthService;
import com.cefet.vocealuga.services.MotoristaService;
import com.cefet.vocealuga.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MotoristaServiceTest {

    private MotoristaRepository motoristaRepository;
    private MotoristaService motoristaService;
    private AuthService authService;
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setUp() throws Exception {
        // Criação dos mocks
        motoristaRepository = mock(MotoristaRepository.class);
        authService = mock(AuthService.class);
        usuarioRepository = mock(UsuarioRepository.class);

        // Instancia o serviço
        motoristaService = new MotoristaService();

        // Injeta os mocks usando reflexão
        var repositoryField = MotoristaService.class.getDeclaredField("repository");
        repositoryField.setAccessible(true);
        repositoryField.set(motoristaService, motoristaRepository);

        var authServiceField = MotoristaService.class.getDeclaredField("authService");
        authServiceField.setAccessible(true);
        authServiceField.set(motoristaService, authService);

        var usuarioRepositoryField = MotoristaService.class.getDeclaredField("usuarioRepository");
        usuarioRepositoryField.setAccessible(true);
        usuarioRepositoryField.set(motoristaService, usuarioRepository);
    }

    @Test
    void deveCriarMotorista() {
        // Dados do motorista
        MotoristaDTO dto = new MotoristaDTO();
        dto.setNome("João Silva");
        dto.setCnh("12345678901");
        dto.setCpf("123.456.789-00");
        dto.setDataNascimento(LocalDate.of(1990, 5, 15));

        // Configurar mock
        when(motoristaRepository.save(any(Motorista.class))).thenAnswer(invocation -> {
            Motorista m = invocation.getArgument(0);
            m.setId(1L);
            return m;
        });

        // Executar método
        MotoristaDTO resultado = motoristaService.criarMotorista(dto);

        // Verificar resultado
        assertEquals(1L, resultado.getId());
        assertEquals("João Silva", resultado.getNome());
        assertEquals("12345678901", resultado.getCnh());
        assertEquals("123.456.789-00", resultado.getCpf());
        assertEquals(LocalDate.of(1990, 5, 15), resultado.getDataNascimento());

        // Verificar que o save foi chamado
        ArgumentCaptor<Motorista> motoristaCaptor = ArgumentCaptor.forClass(Motorista.class);
        verify(motoristaRepository).save(motoristaCaptor.capture());
        Motorista motoristaSalvo = motoristaCaptor.getValue();
        assertEquals("João Silva", motoristaSalvo.getNome());
        assertEquals("12345678901", motoristaSalvo.getCnh());
    }

    @Test
    void deveCriarMotoristaPeloUsuarioLogado() {
        // Dados do usuário
        MeDTO userInfo = new MeDTO();
        userInfo.setNome("Maria Souza");
        userInfo.setDocumento("987.654.321-00");
        userInfo.setDataDeNascimento(LocalDate.of(1985, 10, 20));
        userInfo.setEmail("maria@email.com");

        Usuario usuario = new Usuario();
        usuario.setId(2L);
        usuario.setNome("Maria Souza");
        usuario.setEmail("maria@email.com");

        // Configurar mocks
        Authentication authentication = mock(Authentication.class);
        when(authService.findMe(authentication)).thenReturn(userInfo);
        when(usuarioRepository.findByEmail("maria@email.com")).thenReturn(usuario);
        when(motoristaRepository.save(any(Motorista.class))).thenAnswer(invocation -> {
            Motorista m = invocation.getArgument(0);
            m.setId(2L);
            return m;
        });

        // Executar método
        MotoristaDTO resultado = motoristaService.criarMotoristaPeloUsuarioLogado("98765432109", authentication);

        // Verificar resultado
        assertEquals(2L, resultado.getId());
        assertEquals("Maria Souza", resultado.getNome());
        assertEquals("98765432109", resultado.getCnh());
        assertEquals("987.654.321-00", resultado.getCpf());
        assertEquals(LocalDate.of(1985, 10, 20), resultado.getDataNascimento());

        // Verificar que o método foi chamado corretamente
        verify(authService).findMe(authentication);
        verify(usuarioRepository).findByEmail("maria@email.com");
        verify(motoristaRepository).save(any(Motorista.class));
    }

    @Test
    void deveBuscarMotoristaPorId() {
        // Dados do motorista
        Motorista motorista = new Motorista();
        motorista.setId(1L);
        motorista.setNome("Carlos Santos");
        motorista.setCnh("11122233344");
        motorista.setCpf("111.222.333-44");
        motorista.setDataNascimento(LocalDate.of(1988, 3, 10));

        // Configurar mock
        when(motoristaRepository.findById(1L)).thenReturn(Optional.of(motorista));

        // Executar método
        MotoristaDTO resultado = motoristaService.findById(1L);

        // Verificar resultado
        assertEquals(1L, resultado.getId());
        assertEquals("Carlos Santos", resultado.getNome());
        assertEquals("11122233344", resultado.getCnh());
        assertEquals("111.222.333-44", resultado.getCpf());
        assertEquals(LocalDate.of(1988, 3, 10), resultado.getDataNascimento());

        // Verificar que o método foi chamado
        verify(motoristaRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoMotoristaNaoEncontrado() {
        // Configurar mock para retornar Optional vazio
        when(motoristaRepository.findById(999L)).thenReturn(Optional.empty());

        // Verificar exceção
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> motoristaService.findById(999L));

        assertEquals("Motorista não encontrado com ID: 999", exception.getMessage());
        verify(motoristaRepository).findById(999L);
    }

    @Test
    void deveBuscarTodosOsMotoristas() {
        // Dados dos motoristas
        Motorista motorista1 = new Motorista();
        motorista1.setId(1L);
        motorista1.setNome("Ana Silva");
        motorista1.setCnh("11111111111");
        motorista1.setCpf("111.111.111-11");
        motorista1.setDataNascimento(LocalDate.of(1990, 1, 1));

        Motorista motorista2 = new Motorista();
        motorista2.setId(2L);
        motorista2.setNome("João Santos");
        motorista2.setCnh("22222222222");
        motorista2.setCpf("222.222.222-22");
        motorista2.setDataNascimento(LocalDate.of(1985, 5, 15));

        List<Motorista> motoristas = Arrays.asList(motorista1, motorista2);

        // Configurar mock
        when(motoristaRepository.findAll()).thenReturn(motoristas);

        // Executar método
        List<MotoristaDTO> resultado = motoristaService.findAll();

        // Verificar resultado
        assertEquals(2, resultado.size());

        MotoristaDTO dto1 = resultado.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("Ana Silva", dto1.getNome());
        assertEquals("11111111111", dto1.getCnh());

        MotoristaDTO dto2 = resultado.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("João Santos", dto2.getNome());
        assertEquals("22222222222", dto2.getCnh());

        // Verificar que o método foi chamado
        verify(motoristaRepository).findAll();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverMotoristas() {
        // Configurar mock para retornar lista vazia
        when(motoristaRepository.findAll()).thenReturn(Arrays.asList());

        // Executar método
        List<MotoristaDTO> resultado = motoristaService.findAll();

        // Verificar resultado
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(motoristaRepository).findAll();
    }

    @Test
    void deveCriarMotoristaComDadosCompletos() {
        // Dados completos do motorista
        MotoristaDTO dto = new MotoristaDTO();
        dto.setNome("Pedro Oliveira");
        dto.setCnh("55555555555");
        dto.setCpf("555.555.555-55");
        dto.setDataNascimento(LocalDate.of(1992, 8, 25));

        // Configurar mock
        when(motoristaRepository.save(any(Motorista.class))).thenAnswer(invocation -> {
            Motorista m = invocation.getArgument(0);
            m.setId(10L);
            return m;
        });

        // Executar método
        MotoristaDTO resultado = motoristaService.criarMotorista(dto);

        // Verificar resultado
        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("Pedro Oliveira", resultado.getNome());
        assertEquals("55555555555", resultado.getCnh());
        assertEquals("555.555.555-55", resultado.getCpf());
        assertEquals(LocalDate.of(1992, 8, 25), resultado.getDataNascimento());

        // Verificar que save foi chamado uma única vez
        verify(motoristaRepository, times(1)).save(any(Motorista.class));
    }

    @Test
    void devePreencherCorretamenteOsDadosAoConverterParaDTO() {
        // Dados do motorista
        Motorista motorista = new Motorista();
        motorista.setId(5L);
        motorista.setNome("Lucas Ferreira");
        motorista.setCnh("99999999999");
        motorista.setCpf("999.999.999-99");
        motorista.setDataNascimento(LocalDate.of(1987, 12, 3));

        // Configurar mock
        when(motoristaRepository.findById(5L)).thenReturn(Optional.of(motorista));

        // Executar método
        MotoristaDTO resultado = motoristaService.findById(5L);

        // Verificar todos os campos
        assertNotNull(resultado);
        assertEquals(5L, resultado.getId());
        assertEquals("Lucas Ferreira", resultado.getNome());
        assertEquals("99999999999", resultado.getCnh());
        assertEquals("999.999.999-99", resultado.getCpf());
        assertEquals(LocalDate.of(1987, 12, 3), resultado.getDataNascimento());
    }

    @Test
    void deveCriarMotoristaPeloUsuarioComDadosDoAuthService() {
        // Dados do usuário do AuthService
        MeDTO userInfo = new MeDTO();
        userInfo.setNome("Fernanda Costa");
        userInfo.setDocumento("333.333.333-33");
        userInfo.setDataDeNascimento(LocalDate.of(1991, 7, 18));
        userInfo.setEmail("fernanda@email.com");

        Usuario usuario = new Usuario();
        usuario.setId(3L);
        usuario.setEmail("fernanda@email.com");

        // Configurar mocks
        Authentication authentication = mock(Authentication.class);
        when(authService.findMe(authentication)).thenReturn(userInfo);
        when(usuarioRepository.findByEmail("fernanda@email.com")).thenReturn(usuario);
        when(motoristaRepository.save(any(Motorista.class))).thenAnswer(invocation -> {
            Motorista m = invocation.getArgument(0);
            m.setId(3L);
            return m;
        });

        // Executar método
        MotoristaDTO resultado = motoristaService.criarMotoristaPeloUsuarioLogado("77777777777", authentication);

        // Verificar que os dados do usuário foram usados
        assertEquals("Fernanda Costa", resultado.getNome());
        assertEquals("333.333.333-33", resultado.getCpf());
        assertEquals(LocalDate.of(1991, 7, 18), resultado.getDataNascimento());
        assertEquals("77777777777", resultado.getCnh());

        // Verificar que o motorista foi salvo com os dados corretos
        ArgumentCaptor<Motorista> captor = ArgumentCaptor.forClass(Motorista.class);
        verify(motoristaRepository).save(captor.capture());
        Motorista motoristaSalvo = captor.getValue();
        assertEquals("Fernanda Costa", motoristaSalvo.getNome());
        assertEquals("333.333.333-33", motoristaSalvo.getCpf());
        assertEquals("77777777777", motoristaSalvo.getCnh());
    }
}
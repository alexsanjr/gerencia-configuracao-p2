package com.cefet.vocealuga.service;

import com.cefet.vocealuga.dtos.MeDTO;
import com.cefet.vocealuga.entities.Cliente;
import com.cefet.vocealuga.repositories.ReservaRepository;
import com.cefet.vocealuga.services.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Test
    void deveRetornarReservasDoCliente() throws IllegalAccessException, NoSuchFieldException {
        // Arrange
        ReservaRepository reservaRepository = mock(ReservaRepository.class);
        AuthService authService = new AuthService();
        var field = AuthService.class.getDeclaredField("reservaRepository");
        field.setAccessible(true);
        field.set(authService, reservaRepository);

        Cliente cliente = new Cliente();
        cliente.setId(10L);
        cliente.setNome("João");
        cliente.setEmail("joao@email.com");
        cliente.setTelefone("123456789");
        cliente.setDataDeNascimento(LocalDate.of(1990, 1, 1));
        cliente.setPontosFidelidade(100);
        cliente.setDocumento("12345678900");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(cliente);
        when(reservaRepository.findIdsByUsuarioId(10L)).thenReturn(List.of(1L, 2L, 3L));

        MeDTO dto = authService.findMe(auth);

        assertEquals("João", dto.getNome());
        assertEquals("joao@email.com", dto.getEmail());
        assertEquals("123456789", dto.getTelefone());
        assertEquals(LocalDate.of(1990, 1, 1), dto.getDataDeNascimento());
        assertEquals(100, dto.getPontosFidelidade());
        assertEquals("12345678900", dto.getDocumento());
        assertEquals(List.of(1L, 2L, 3L), dto.getReservas());
        assertEquals("ROLE_CLIENTE", dto.getRole());
    }
}

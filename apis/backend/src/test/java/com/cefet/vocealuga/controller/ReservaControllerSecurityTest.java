
package com.cefet.vocealuga.controller;

import com.cefet.vocealuga.controllers.ReservaController;
import com.cefet.vocealuga.repositories.UsuarioRepository;
import com.cefet.vocealuga.services.JwtTokenService;
import com.cefet.vocealuga.services.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservaController.class)
public class ReservaControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservaService reservaService;

    @MockBean
    private JwtTokenService jwtTokenService;

    @MockBean
    private UsuarioRepository usuarioRepository;


    @Test
    void naoDevePermitirAcessarReservaSemAutenticacao() throws Exception {
        mockMvc.perform(get("/reservas/1"))
                .andExpect(status().isUnauthorized());
    }
}


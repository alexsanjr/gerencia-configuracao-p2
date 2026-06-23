package com.cefet.vocealuga.service;

import com.cefet.vocealuga.dtos.AuthResponse;
import com.cefet.vocealuga.dtos.LoginRequest;
import com.cefet.vocealuga.dtos.RegisterRequest;
import com.cefet.vocealuga.dtos.enums.TipoRegister;
import com.cefet.vocealuga.entities.Cliente;
import com.cefet.vocealuga.repositories.UsuarioRepository;
import com.cefet.vocealuga.services.JwtTokenService;
import com.cefet.vocealuga.services.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        usuarioService = new UsuarioService(usuarioRepository, passwordEncoder, jwtTokenService);
    }

    @Test
    void deveRegistrarUsuarioComDadosValidos() {
        RegisterRequest request = new RegisterRequest(
                "Robson", "12345678954", LocalDate.of(1998, 12, 12),
                "robson@gmail.com", "21999999999", "Senha123!", TipoRegister.CLIENTE
        );

        when(usuarioRepository.findByEmail("robson@gmail.com")).thenReturn(null);
        when(passwordEncoder.encode(any())).thenReturn("senhaCodificada");
        when(usuarioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = usuarioService.registerUser(request);

        assertEquals(201, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Usuário CLIENTE criado com sucesso!", body.get("mensagem"));
        assertEquals("robson@gmail.com", body.get("email"));
    }

    @Test
    void naoDeveRegistrarUsuarioComEmailJaCadastrado() {
        RegisterRequest request = new RegisterRequest(
                "Robson", "12345678954", LocalDate.of(1998, 12, 12),
                "robson@gmail.com", "21999999999", "Senha123!", TipoRegister.CLIENTE
        );

        when(usuarioRepository.findByEmail("robson@gmail.com")).thenReturn(new Cliente());

        ResponseEntity<?> response = usuarioService.registerUser(request);

        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Usuário já existe", body.get("erro"));
    }

    @Test
    void naoDeveRegistrarUsuarioMenorDeIdade() {
        RegisterRequest request = new RegisterRequest(
                "Robson", "12345678954", LocalDate.now().minusYears(17),
                "robson@gmail.com", "21999999999", "Senha123!", TipoRegister.CLIENTE
        );

        when(usuarioRepository.findByEmail("robson@gmail.com")).thenReturn(null);

        ResponseEntity<?> response = usuarioService.registerUser(request);

        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Usuário é menor de idade", body.get("erro"));
    }

    @Test
    void naoDeveRegistrarUsuarioComTipoInvalido() {
        RegisterRequest request = new RegisterRequest(
                "Robson", "12345678954", LocalDate.of(1990, 1, 1),
                "robson@gmail.com", "21999999999", "Senha123!", null
        );

        when(usuarioRepository.findByEmail("robson@gmail.com")).thenReturn(null);

        ResponseEntity<?> response = usuarioService.registerUser(request);

        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Tipo de usuário inválido", body.get("erro"));
    }

    @Test
    void naoDeveRegistrarUsuarioComTelefoneInvalido() {
        RegisterRequest request = new RegisterRequest(
                "Robson", "12345678954", LocalDate.of(1990, 1, 1),
                "robson@gmail.com", "123abc", "123abc", TipoRegister.CLIENTE // telefone inválido
        );

        when(usuarioRepository.findByEmail("robson@gmail.com")).thenReturn(null);

        ResponseEntity<?> response = usuarioService.registerUser(request);

        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Telefone inválido", body.get("erro"));
    }

    @Test
    void naoDeveRegistrarUsuarioComCpfJaCadastrado() {
        RegisterRequest request = new RegisterRequest(
                "Robson", "12345678954", LocalDate.of(1998, 12, 12),
                "robson2@gmail.com", "21999999999", "Senha123!", TipoRegister.CLIENTE
        );

        when(usuarioRepository.findByEmail("robson2@gmail.com")).thenReturn(null);
        when(usuarioRepository.findByDocumento("12345678954")).thenReturn(new Cliente());

        ResponseEntity<?> response = usuarioService.registerUser(request);

        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("CPF já existe", body.get("erro"));
    }

    @Test
    void naoDeveRegistrarUsuarioComCamposObrigatoriosEmBranco() {
        RegisterRequest request = new RegisterRequest(
                "", "", null, "", "", "", TipoRegister.CLIENTE
        );

        ResponseEntity<?> response = usuarioService.registerUser(request);

        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue(body.get("erro").toString().contains("Campos obrigatórios em branco"));
    }

    @Test
    void naoDeveRegistrarUsuarioComEmailInvalido() {
        RegisterRequest request = new RegisterRequest(
                "Robson", "12345678954", LocalDate.of(1998, 12, 12),
                "email-invalido", "21999999999", "Senha123!", TipoRegister.CLIENTE
        );

        ResponseEntity<?> response = usuarioService.registerUser(request);

        assertEquals(400, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue(body.get("erro").toString().toLowerCase().contains("email"));
    }

    @Test
    void deveAutenticarUsuarioComCredenciaisValidas() {
        LoginRequest loginRequest = new LoginRequest("robson@gmail.com", "Senha123!");

        Cliente usuario = new Cliente();
        usuario.setEmail("robson@gmail.com");
        usuario.setPassword("senhaCodificada");
        usuario.setId(1L); // Adicionando um ID ao usuário para passar ao generateToken

        when(usuarioRepository.findByEmail("robson@gmail.com")).thenReturn(usuario);
        when(passwordEncoder.matches("Senha123!", "senhaCodificada")).thenReturn(true);

        JwtTokenService jwtTokenService = mock(JwtTokenService.class);
        when(jwtTokenService.generateToken(anyString(), anyString(), anyLong())).thenReturn("tokenFake");

        UsuarioService usuarioServiceReal = new UsuarioService(usuarioRepository, passwordEncoder, jwtTokenService);

        AuthResponse response = usuarioServiceReal.authenticateUser(loginRequest);

        assertNotNull(response.getToken());
        assertEquals("ROLE_CLIENTE", response.getTipo());
        assertEquals("Login realizado com sucesso", response.getMensagem());
    }

    @Test
    void naoDeveAutenticarUsuarioComCredenciaisInvalidas() {
        LoginRequest loginRequest = new LoginRequest("usuario@invalido.com", "SenhaErrada");


        when(usuarioRepository.findByEmail("usuario@invalido.com")).thenReturn(null);

        AuthResponse response = usuarioService.authenticateUser(loginRequest);

        assertNull(response.getToken());
        assertEquals("Credenciais inválidas", response.getMensagem());
    }

    @Test
    void naoDeveAutenticarUsuarioComSenhaIncorreta() {
        LoginRequest loginRequest = new LoginRequest("robson@gmail.com", "SenhaErrada");

        Cliente usuario = new Cliente();
        usuario.setEmail("robson@gmail.com");
        usuario.setPassword("senhaCodificada");

        when(usuarioRepository.findByEmail("robson@gmail.com")).thenReturn(usuario);
        when(passwordEncoder.matches("SenhaErrada", "senhaCodificada")).thenReturn(false);

        AuthResponse response = usuarioService.authenticateUser(loginRequest);

        assertNull(response.getToken());
        assertEquals("Credenciais inválidas", response.getMensagem());
    }

    @Test
    void naoDeveAutenticarUsuarioComCamposVazios() {
        LoginRequest loginRequest = new LoginRequest("", "");

        AuthResponse response = usuarioService.authenticateUser(loginRequest);

        assertNull(response.getToken());
        assertEquals("Credenciais inválidas", response.getMensagem());
    }
}

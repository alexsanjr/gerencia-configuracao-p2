package com.cefet.vocealuga.services;

import com.cefet.vocealuga.dtos.AuthResponse;
import com.cefet.vocealuga.dtos.LoginRequest;
import com.cefet.vocealuga.dtos.RegisterRequest;
import com.cefet.vocealuga.entities.*;
import com.cefet.vocealuga.repositories.FilialRepository;
import com.cefet.vocealuga.repositories.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    private final SmtpEmailService emailService;

    public UsuarioService(UsuarioRepository repository,
                          PasswordEncoder passwordEncoder, JwtTokenService tokenService, SmtpEmailService emailService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = tokenService;
        this.emailService = emailService;
    }

    public ResponseEntity<?> registerUser(RegisterRequest request) {

        if (request.getNome() == null || request.getNome().isBlank() ||
                request.getDocumento() == null || request.getDocumento().isBlank() ||
                request.getDataNascimento() == null ||
                request.getEmail() == null || request.getEmail().isBlank() ||
                request.getTelefone() == null || request.getTelefone().isBlank() ||
                request.getPassword() == null || request.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Campos obrigatórios em branco"));
        }

        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Email inválido"));
        }

        if (findByEmail(request.getEmail()) != null) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Usuário já existe"));
        }

        if (calcularIdade(request.getDataNascimento()) < 18) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Usuário é menor de idade"));
        }

        Usuario novoUsuario = criarUsuarioPeloTipo(request);

        if (novoUsuario == null) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Tipo de usuário inválido"));
        }

        if (request.getTelefone() == null || !request.getTelefone().matches("^\\d{10,13}$")) {
            return ResponseEntity.badRequest().body(Map.of("erro", "Telefone inválido"));
        }

        if (repository.findByDocumento(request.getDocumento()) != null) {
            return ResponseEntity.badRequest().body(Map.of("erro", "CPF já existe"));
        }


        saveUser(novoUsuario);

        emailService.sendWelcomeEmail(novoUsuario.getEmail(), novoUsuario.getNome());

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "mensagem", "Usuário " + request.getTipo() + " criado com sucesso!",
                "email", novoUsuario.getEmail()
        ));
    }

    private Usuario criarUsuarioPeloTipo(RegisterRequest request) {
        if (request.getTipo() == null) {
            return null;
        }

        return switch (request.getTipo()) {
            case CLIENTE -> new Cliente(null, request.getNome(), request.getDocumento(), request.getDataNascimento(),
                    request.getEmail(), request.getPassword(), request.getTelefone(), 0);
            case FUNCIONARIO -> new Funcionario(null, request.getNome(), request.getDocumento(), request.getDataNascimento(),
                    request.getEmail(), request.getPassword(), request.getTelefone(), "", null);
            case GERENTE -> new Gerente(null, request.getNome(), request.getDocumento(), request.getDataNascimento(),
                    request.getEmail(), request.getPassword(), request.getTelefone(), "", null);
            case ADMINISTRADOR -> new Administrador(null, request.getNome(), request.getDocumento(), request.getDataNascimento(),
                    request.getEmail(), request.getPassword(), request.getTelefone(), "", null);
        };
    }


    public Usuario saveUser(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return repository.save(usuario);
    }

    public Usuario findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public static int calcularIdade(LocalDate nascimento) {
        return Period.between(nascimento, LocalDate.now()).getYears();
    }

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Usuario user = findByEmail(loginRequest.getEmail());
        if (user != null && passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            String tipo = user instanceof Gerente ? "ROLE_GERENTE" :
                    user instanceof Administrador ? "ROLE_ADMIN" :
                            user instanceof Funcionario ? "ROLE_FUNCIONARIO" :
                                    "ROLE_CLIENTE";
            String token = jwtTokenService.generateToken(user.getEmail(), tipo, user.getId());
            return new AuthResponse(token, tipo, "Login realizado com sucesso");
        }
        return new AuthResponse("Credenciais inválidas");
    }
}

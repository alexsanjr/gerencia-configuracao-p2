package com.cefet.vocealuga.controllers;

import com.cefet.vocealuga.dtos.AuthResponse;
import com.cefet.vocealuga.dtos.LoginRequest;
import com.cefet.vocealuga.dtos.MeDTO;
import com.cefet.vocealuga.entities.Administrador;
import com.cefet.vocealuga.entities.Funcionario;
import com.cefet.vocealuga.entities.Gerente;
import com.cefet.vocealuga.entities.Usuario;
import com.cefet.vocealuga.services.AuthService;
import com.cefet.vocealuga.services.JwtTokenService;
import com.cefet.vocealuga.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtTokenService jwtTokenService;
    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;


    @Autowired
    public AuthController(JwtTokenService jwtTokenService,
                          UsuarioService usuarioService,
                          PasswordEncoder passwordEncoder) {
        this.jwtTokenService = jwtTokenService;
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse response = usuarioService.authenticateUser(loginRequest);
        if (response.getToken() != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<MeDTO> me(Authentication auth) {
          MeDTO dto = authService.findMe(auth);
        return ResponseEntity.ok(dto);
    }

}


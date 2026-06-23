package com.cefet.vocealuga.controllers;

import com.cefet.vocealuga.dtos.RegisterRequest;
import com.cefet.vocealuga.entities.*;
import com.cefet.vocealuga.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.Period;

@RestController
@RequestMapping("/register")
public class RegisterController {

    private final UsuarioService userService;

    public RegisterController(UsuarioService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest request) {
        return userService.registerUser(request);
    }
}

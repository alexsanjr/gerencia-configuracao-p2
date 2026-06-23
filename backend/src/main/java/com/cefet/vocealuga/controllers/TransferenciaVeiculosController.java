package com.cefet.vocealuga.controllers;


import com.cefet.vocealuga.dtos.TransferenciaVeiculosDTO;
import com.cefet.vocealuga.services.TransferenciaVeiculosService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/transferencia-veiculos")
public class TransferenciaVeiculosController {

    @Autowired
    private TransferenciaVeiculosService transferenciaVeiculosService;

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_FUNCIONARIO', 'ROLE_GERENTE', 'ROLE_ADMIN')")
    public ResponseEntity<TransferenciaVeiculosDTO> findById(@PathVariable Long id) {
        TransferenciaVeiculosDTO dto = transferenciaVeiculosService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_FUNCIONARIO', 'ROLE_GERENTE', 'ROLE_ADMIN')")
    public ResponseEntity<Page<TransferenciaVeiculosDTO>> findAll(Pageable pageable) {
        Page<TransferenciaVeiculosDTO> dto = transferenciaVeiculosService.findAll(pageable);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_FUNCIONARIO', 'ROLE_GERENTE', 'ROLE_ADMIN')")
    public ResponseEntity<TransferenciaVeiculosDTO> insert(@Valid @RequestBody TransferenciaVeiculosDTO dto) {
        dto = transferenciaVeiculosService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_FUNCIONARIO', 'ROLE_GERENTE', 'ROLE_ADMIN')")
    public ResponseEntity<TransferenciaVeiculosDTO> update(@PathVariable Long id, @Valid @RequestBody TransferenciaVeiculosDTO dto) {
        dto = transferenciaVeiculosService.update(id, dto);
        return ResponseEntity.ok(dto);
    }
}

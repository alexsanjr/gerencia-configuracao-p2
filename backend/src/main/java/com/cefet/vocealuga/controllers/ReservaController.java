package com.cefet.vocealuga.controllers;

import com.cefet.vocealuga.dtos.ReservaDTO;
import com.cefet.vocealuga.services.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/reservas")
public class ReservaController {

    @Autowired
    private ReservaService service;

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_FUNCIONARIO', 'ROLE_GERENTE', 'ROLE_ADMIN', 'ROLE_CLIENTE')")
    public ResponseEntity<ReservaDTO> findById(@PathVariable Long id) {
        ReservaDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_FUNCIONARIO', 'ROLE_GERENTE', 'ROLE_ADMIN')")
    public ResponseEntity<Page<ReservaDTO>> findAll(Pageable pageable) {
        Page<ReservaDTO> dto = service.findAll(pageable);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENTE', 'ROLE_FUNCIONARIO', 'ROLE_GERENTE')")
    public ResponseEntity<ReservaDTO> insert(@RequestBody ReservaDTO dto, Authentication authentication) {
        ReservaDTO result = service.insert(dto, authentication);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(uri).body(result);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_GERENTE', 'ROLE_ADMIN')")
    public ResponseEntity<ReservaDTO> update(@PathVariable Long id, @Valid @RequestBody ReservaDTO dto) {
        dto = service.update(id, dto);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_GERENTE', 'ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}

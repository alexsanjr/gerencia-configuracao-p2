package com.cefet.vocealuga.controllers;

import com.cefet.vocealuga.dtos.EstoqueDTO;
import com.cefet.vocealuga.services.EstoqueService;
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
@RequestMapping(value = "/estoques")
public class EstoqueController {

    @Autowired
    private EstoqueService estoqueService;

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_FUNCIONARIO', 'ROLE_GERENTE', 'ROLE_ADMIN')")
    public ResponseEntity<EstoqueDTO> findById(@PathVariable Long id) {
        EstoqueDTO dto = estoqueService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_FUNCIONARIO', 'ROLE_GERENTE', 'ROLE_ADMIN')")
    public ResponseEntity<Page<EstoqueDTO>> findAll(Pageable pageable) {
        Page<EstoqueDTO> dto = estoqueService.findAll(pageable);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_GERENTE', 'ROLE_ADMIN')")
    public ResponseEntity<EstoqueDTO> insert(@Valid @RequestBody EstoqueDTO dto) {
        dto = estoqueService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_GERENTE', 'ROLE_ADMIN')")
    public ResponseEntity<EstoqueDTO> update(@PathVariable Long id, @Valid @RequestBody EstoqueDTO dto) {
        dto = estoqueService.update(id, dto);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_GERENTE', 'ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        estoqueService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

package com.cefet.vocealuga.controllers;

import com.cefet.vocealuga.dtos.VeiculoDTO;
import com.cefet.vocealuga.services.VeiculoService;
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
@RequestMapping(value = "/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_FUNCIONARIO', 'ROLE_GERENTE', 'ROLE_ADMIN')")
    public ResponseEntity<VeiculoDTO> findById(@PathVariable Long id) {
        VeiculoDTO dto = veiculoService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<Page<VeiculoDTO>> findAll(
            @RequestParam(name = "marca", defaultValue = "") String marca,
            Pageable pageable) {
        Page<VeiculoDTO> dto = veiculoService.findAll(marca, pageable);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/placa/{placa}")
    public ResponseEntity<VeiculoDTO> findByPlaca(@PathVariable String placa) {
        VeiculoDTO dto = veiculoService.findByPlaca(placa);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_GERENTE', 'ROLE_ADMIN')")
    public ResponseEntity<VeiculoDTO> insert(@Valid @RequestBody VeiculoDTO dto) {
        dto = veiculoService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_GERENTE', 'ROLE_ADMIN')")
    public ResponseEntity<VeiculoDTO> update(@PathVariable Long id, @Valid @RequestBody VeiculoDTO dto) {
        dto = veiculoService.update(id, dto);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_GERENTE', 'ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        veiculoService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

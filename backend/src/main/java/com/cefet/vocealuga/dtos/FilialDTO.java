package com.cefet.vocealuga.dtos;

import jakarta.validation.constraints.NotBlank;


public class FilialDTO {
    private Long id;
    @NotBlank(message = "Campo requerido")
    private String nome;
    private String local;

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

package com.cefet.vocealuga.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class EstacaoDeServico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String local;
    @Column(columnDefinition = "TEXT")
    private String nome;

    public EstacaoDeServico(Long id, String local, String nome) {
        this.id = id;
        this.local = local;
        this.nome = nome;
    }

    public EstacaoDeServico() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EstacaoDeServico that = (EstacaoDeServico) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

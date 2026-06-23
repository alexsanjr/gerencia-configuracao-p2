package com.cefet.vocealuga.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Filial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String local;
    @OneToOne(mappedBy = "filial")
    private Estoque estoque;

    public Filial(Long id, String nome, String local, Estoque estoque) {
        this.id = id;
        this.nome = nome;
        this.local = local;
        this.estoque = estoque;
    }

    public Filial() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Estoque getEstoque() {
        return estoque;
    }

    public void setEstoque(Estoque estoque) {
        this.estoque = estoque;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Filial filial = (Filial) o;
        return Objects.equals(id, filial.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

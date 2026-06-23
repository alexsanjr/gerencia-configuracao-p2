package com.cefet.vocealuga.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "filial_id", referencedColumnName = "id")
    private Filial filial;

    public Estoque(Long id, String nome, Filial filial) {
        this.id = id;
        this.nome = nome;
        this.filial = filial;
    }
    public Estoque() {}

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

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Estoque estoque = (Estoque) o;
        return Objects.equals(id, estoque.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

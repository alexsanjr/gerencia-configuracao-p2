package com.cefet.vocealuga.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDate;

@Entity
public class Funcionario extends Usuario {
    private String cargo;
    @ManyToOne
    @JoinColumn(name = "filial_id")
    private Filial filial;


    public Funcionario() {
    }

    public Funcionario(Long id, String nome, String documento, LocalDate dataDeNascimento, String email, String password, String telefone, String cargo, Filial filial) {
        super(id, nome, documento, dataDeNascimento, email, password, telefone);
        this.cargo = cargo;
        this.filial = filial;
    }

    public Funcionario(Long id, String email, String password, String cargo, Filial filial) {
        super(id, email, password);
        this.cargo = cargo;
        this.filial = filial;
    }

    public Funcionario(Long id, String email, String password) {
        super(id, email, password);
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }
}

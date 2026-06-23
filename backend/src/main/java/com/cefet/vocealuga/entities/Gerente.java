package com.cefet.vocealuga.entities;

import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
public class Gerente extends Funcionario {

    public Gerente() {
    }

    public Gerente(Long id, String nome, String documento, LocalDate dataDeNascimento, String email, String password, String telefone, String cargo, Filial filial) {
        super(id, nome, documento, dataDeNascimento, email, password, telefone, cargo, filial);
    }

    public Gerente(Long id, String email, String password, String cargo, Filial filial) {
        super(id, email, password, cargo, filial);
    }

    public Gerente(Long id, String email, String password) {
        super(id, email, password);
    }
}

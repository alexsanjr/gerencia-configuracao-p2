package com.cefet.vocealuga.entities;

import jakarta.persistence.Entity;

import java.time.LocalDate;

@Entity
public class Cliente extends Usuario {
    private int pontosFidelidade;

    public Cliente() {
    }

    public Cliente(Long id, String nome, String documento, LocalDate dataDeNascimento, String email, String password, String telefone, int pontosFidelidade) {
        super(id, nome, documento, dataDeNascimento, email, password, telefone);
        this.pontosFidelidade = pontosFidelidade;
    }

    public Cliente(Long id, String email, String password, int pontosFidelidade) {
        super(id, email, password);
        this.pontosFidelidade = pontosFidelidade;
    }

    public Cliente(Long id, String email, String password) {
        super(id, email, password);
    }

    public int getPontosFidelidade() {
        return pontosFidelidade;
    }

    public void setPontosFidelidade(int pontosFidelidade) {
        this.pontosFidelidade = pontosFidelidade;
    }
}

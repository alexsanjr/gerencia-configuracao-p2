package com.cefet.vocealuga.dtos;

import com.cefet.vocealuga.dtos.enums.TipoRegister;
import jakarta.persistence.Column;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class RegisterRequest {
    @NotBlank
    private String nome;
    @Column(unique = true)
    @NotBlank
    private String documento;
    @NotNull
    private LocalDate dataNascimento;
    @Column(unique = true)
    @NotBlank
    private String email;
    @NotBlank
    private String telefone;
    @NotBlank
    private String password;
    private TipoRegister tipo;

    public RegisterRequest(String nome, String documento, LocalDate dataNascimento, String email, String telefone, String password, TipoRegister tipo) {
        this.nome = nome;
        this.documento = documento;
        this.dataNascimento = dataNascimento;
        this.email = email;
        this.telefone = telefone;
        this.password = password;
        this.tipo = tipo;
    }

    public RegisterRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public TipoRegister getTipo() {
        return tipo;
    }

    public void setTipo(TipoRegister tipo) {
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}

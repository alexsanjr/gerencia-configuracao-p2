package com.cefet.vocealuga.dtos;


import java.time.LocalDate;
import java.util.List;


public class MeDTO {

    private String nome;
    private String documento;
    private LocalDate dataDeNascimento;
    private String email;
    private String telefone;
    private String role;
    private List<Long> reservas;

    // somente Cliente
    private int pontosFidelidade;

    //Funcionario, Gerente e Admin
    private String cargo;
    private Long filial_id;


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

    public LocalDate getDataDeNascimento() {
        return dataDeNascimento;
    }

    public void setDataDeNascimento(LocalDate dataDeNascimento) {
        this.dataDeNascimento = dataDeNascimento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public int getPontosFidelidade() {
        return pontosFidelidade;
    }

    public void setPontosFidelidade(int pontosFidelidade) {
        this.pontosFidelidade = pontosFidelidade;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Long getFilial_id() {
        return filial_id;
    }

    public void setFilial_id(Long filial_id) {
        this.filial_id = filial_id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Long> getReservas() {
        return reservas;
    }

    public void setReservas(List<Long> reservas) {
        this.reservas = reservas;
    }
}

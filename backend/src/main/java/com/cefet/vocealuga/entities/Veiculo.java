package com.cefet.vocealuga.entities;

import com.cefet.vocealuga.entities.enums.Cor;
import com.cefet.vocealuga.entities.enums.Grupo;
import com.cefet.vocealuga.entities.enums.StatusVeiculo;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "estoque_id")
    private Estoque estoque;
    @ManyToOne
    @JoinColumn(name = "filial_id")
    private Filial filial;
    private String marca;
    private String modelo;
    private Grupo grupo;
    private int ano;
    private Cor cor;
    private double valorDiaria;
    private Long quilometragem;
    private StatusVeiculo statusVeiculo;
    private String placa;
    //created at
    //updated at


    public Veiculo() {
    }

    public Veiculo(Long id, Estoque estoque, String marca, String modelo, Grupo grupo, int ano, Cor cor, double valorDiaria, Long quilometragem, StatusVeiculo statusVeiculo, String placa) {
        this.id = id;
        this.estoque = estoque;
        this.filial = estoque.getFilial();
        this.marca = marca;
        this.modelo = modelo;
        this.grupo = grupo;
        this.ano = ano;
        this.cor = cor;
        this.valorDiaria = valorDiaria;
        this.quilometragem = quilometragem;
        this.statusVeiculo = statusVeiculo;
        this.placa = placa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public Cor getCor() {
        return cor;
    }

    public void setCor(Cor cor) {
        this.cor = cor;
    }

    public double getValorDiaria() {
        return valorDiaria;
    }

    public void setValorDiaria(double valorDiaria) {
        this.valorDiaria = valorDiaria;
    }

    public Long getQuilometragem() {
        return quilometragem;
    }

    public void setQuilometragem(Long quilometragem) {
        this.quilometragem = quilometragem;
    }

    public StatusVeiculo getStatusVeiculo() {
        return statusVeiculo;
    }

    public void setStatusVeiculo(StatusVeiculo statusVeiculo) {
        this.statusVeiculo = statusVeiculo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
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
        Veiculo veiculo = (Veiculo) o;
        return Objects.equals(id, veiculo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

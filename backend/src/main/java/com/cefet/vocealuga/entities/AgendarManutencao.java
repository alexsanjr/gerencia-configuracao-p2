package com.cefet.vocealuga.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
public class AgendarManutencao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;
    @ManyToOne
    @JoinColumn(name = "estacao_de_servico_id")
    private EstacaoDeServico estacaoDeServico;
    //Funcionario
    @Column(columnDefinition = "TEXT")
    private String motivoManutencao;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant dataManutencao;

    public AgendarManutencao(Long id, Veiculo veiculo, EstacaoDeServico estacaoDeServico, String motivoManutencao, Instant dataManutencao) {
        this.id = id;
        this.veiculo = veiculo;
        this.estacaoDeServico = estacaoDeServico;
        this.motivoManutencao = motivoManutencao;
        this.dataManutencao = dataManutencao;
    }

    public AgendarManutencao() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public EstacaoDeServico getEstacaoDeServico() {
        return estacaoDeServico;
    }

    public void setEstacaoDeServico(EstacaoDeServico estacaoDeServico) {
        this.estacaoDeServico = estacaoDeServico;
    }

    public String getMotivoManutencao() {
        return motivoManutencao;
    }

    public void setMotivoManutencao(String motivoManutencao) {
        this.motivoManutencao = motivoManutencao;
    }

    public Instant getDataManutencao() {
        return dataManutencao;
    }

    public void setDataManutencao(Instant dataManutencao) {
        this.dataManutencao = dataManutencao;
    }

    public void notificarEstacao() {

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AgendarManutencao that = (AgendarManutencao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

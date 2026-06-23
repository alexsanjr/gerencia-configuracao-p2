package com.cefet.vocealuga.dtos;

import java.time.Instant;

public class AgendarManutencaoDTO {
    private Long id;
    private Long veiculoId;
    private Long estacaoDeServicoId;
    private String motivoManutencao;
    private Instant dataManutencao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVeiculoId() {
        return veiculoId;
    }

    public void setVeiculoId(Long veiculoId) {
        this.veiculoId = veiculoId;
    }

    public Long getEstacaoDeServicoId() {
        return estacaoDeServicoId;
    }

    public void setEstacaoDeServicoId(Long estacaoDeServicoId) {
        this.estacaoDeServicoId = estacaoDeServicoId;
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
}

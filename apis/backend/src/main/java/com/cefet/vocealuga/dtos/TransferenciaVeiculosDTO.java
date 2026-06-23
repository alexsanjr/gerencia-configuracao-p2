package com.cefet.vocealuga.dtos;

import com.cefet.vocealuga.entities.enums.StatusTransferencia;

import java.time.Instant;
import java.util.Set;

public class TransferenciaVeiculosDTO {
    private Long id;
    private Long estoqueOrigem;
    private Long estoqueDestino;
    private Instant data;
    private Set<Long> idVeiculos;
    private StatusTransferencia status;

    public StatusTransferencia getStatus() {
        return status;
    }

    public void setStatus(StatusTransferencia status) {
        this.status = status;
    }

    public Set<Long> getIdVeiculos() {
        return idVeiculos;
    }

    public void setIdVeiculos(Set<Long> idVeiculos) {
        this.idVeiculos = idVeiculos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEstoqueOrigem() {
        return estoqueOrigem;
    }

    public void setEstoqueOrigem(Long estoqueOrigem) {
        this.estoqueOrigem = estoqueOrigem;
    }

    public Long getEstoqueDestino() {
        return estoqueDestino;
    }

    public void setEstoqueDestino(Long estoqueDestino) {
        this.estoqueDestino = estoqueDestino;
    }

    public Instant getData() {
        return data;
    }

    public void setData(Instant data) {
        this.data = data;
    }
}

package com.cefet.vocealuga.dtos;

import com.cefet.vocealuga.entities.enums.StatusReserva;
import com.cefet.vocealuga.entities.enums.TipoReserva;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReservaDTO {

    private Long id;
    private Long usuarioId;
    private Long motoristaId;
    private TipoReserva categoria;
    private StatusReserva status;
    private LocalDate dataReserva;
    private LocalDate dataVencimento;
    private Long localRetiradaId;
    private Long VeiculoId;
    private BigDecimal valorTotal;

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Long getMotoristaId() {
        return motoristaId;
    }

    public void setMotoristaId(Long motoristaId) {
        this.motoristaId = motoristaId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoReserva getCategoria() {
        return categoria;
    }

    public void setCategoria(TipoReserva categoria) {
        this.categoria = categoria;
    }

    public StatusReserva getStatus() {
        return status;
    }

    public void setStatus(StatusReserva status) {
        this.status = status;
    }

    public LocalDate getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(LocalDate dataReserva) {
        this.dataReserva = dataReserva;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Long getLocalRetiradaId() {
        return localRetiradaId;
    }

    public void setLocalRetiradaId(Long localRetiradaId) {
        this.localRetiradaId = localRetiradaId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getVeiculoId() {
        return VeiculoId;
    }

    public void setVeiculoId(Long veiculoId) {
        VeiculoId = veiculoId;
    }
}

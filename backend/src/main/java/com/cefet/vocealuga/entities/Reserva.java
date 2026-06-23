package com.cefet.vocealuga.entities;

import com.cefet.vocealuga.entities.enums.StatusReserva;
import com.cefet.vocealuga.entities.enums.TipoReserva;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "motorista_id")
    private Motorista motorista;
    private TipoReserva categoria;
    private StatusReserva status;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDate dataReserva;
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDate dataVencimento;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "filial_id")
    private Filial localRetirada;
    @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;
    private BigDecimal valorTotal;

    public Reserva(Long id, TipoReserva categoria, StatusReserva status, LocalDate dataReserva, LocalDate dataVencimento, Filial localRetirada, Usuario usuario, Veiculo  veiculo, Motorista motorista, BigDecimal valorTotal) {
        this.id = id;
        this.categoria = categoria;
        this.status = status;
        this.dataReserva = dataReserva;
        this.dataVencimento = dataVencimento;
        this.localRetirada = localRetirada;
        this.usuario = usuario;
        this.veiculo = veiculo;
        this.motorista = motorista;
        this.valorTotal = valorTotal;
    }

    public Reserva() {
    }

    public Motorista getMotorista() {
        return motorista;
    }

    public void setMotorista(Motorista motorista) {
        this.motorista = motorista;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
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

    public Filial getLocalRetirada() {
        return localRetirada;
    }

    public void setLocalRetirada(Filial localRetirada) {
        this.localRetirada = localRetirada;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Reserva reserva = (Reserva) o;
        return Objects.equals(id, reserva.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

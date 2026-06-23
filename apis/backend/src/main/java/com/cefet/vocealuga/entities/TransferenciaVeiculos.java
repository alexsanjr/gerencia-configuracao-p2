package com.cefet.vocealuga.entities;

import com.cefet.vocealuga.entities.enums.StatusTransferencia;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class TransferenciaVeiculos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "estoque_origem_id")
    private Estoque estoqueOrigem;
    @ManyToOne
    @JoinColumn(name = "estoque_destino_id")
    private Estoque estoqueDestino;
    //Gerente
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private Instant data;
    @ManyToMany
    @JoinTable(
            name = "transferenciaVeiculos_Veiculo",
            joinColumns = @JoinColumn(name = "transferencia_id"),
            inverseJoinColumns = @JoinColumn(name = "veiculo_id")
    )
    private Set<Veiculo> veiculos = new HashSet<>();
    private StatusTransferencia status;

    public TransferenciaVeiculos(Long id, Estoque estoqueOrigem, Estoque estoqueDestino, Instant data, StatusTransferencia status) {
        this.id = id;
        this.estoqueOrigem = estoqueOrigem;
        this.estoqueDestino = estoqueDestino;
        this.data = data;
        this.status = status;
    }

    public TransferenciaVeiculos() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Estoque getEstoqueOrigem() {
        return estoqueOrigem;
    }

    public void setEstoqueOrigem(Estoque estoqueOrigem) {
        this.estoqueOrigem = estoqueOrigem;
    }

    public Estoque getEstoqueDestino() {
        return estoqueDestino;
    }

    public void setEstoqueDestino(Estoque estoqueDestino) {
        this.estoqueDestino = estoqueDestino;
    }

    public Instant getData() {
        return data;
    }

    public void setData(Instant data) {
        this.data = data;
    }

    public Set<Veiculo> getVeiculos() {
        return veiculos;
    }

    public void setVeiculos(Set<Veiculo> veiculos) {
        this.veiculos = veiculos;
    }

    public StatusTransferencia getStatus() {
        return status;
    }

    public void setStatus(StatusTransferencia status) {
        this.status = status;
    }

    public void notificarTransferencia() {
        // lógica de notificação
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TransferenciaVeiculos that = (TransferenciaVeiculos) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

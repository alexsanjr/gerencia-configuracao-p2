package com.cefet.vocealuga.services;

import com.cefet.vocealuga.dtos.ReservaDTO;
import com.cefet.vocealuga.entities.*;
import com.cefet.vocealuga.entities.enums.StatusReserva;
import com.cefet.vocealuga.entities.enums.StatusVeiculo;
import com.cefet.vocealuga.repositories.*;
import com.cefet.vocealuga.services.exceptions.DatabaseException;
import com.cefet.vocealuga.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository repository;

    @Autowired
    private FilialRepository filialRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private MotoristaRepository motoristaRepository;


    @Transactional(readOnly = true)
    public ReservaDTO findById(Long id) {

        Reserva entity = repository.findById(id).orElseThrow(() -> new RuntimeException("Estação de serviço não encontrada"));
        return convertToDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<ReservaDTO> findAll(Pageable pageable) {
        Page<Reserva> result = repository.findAll(pageable);
        return result.map(this::convertToDTO);
    }

    @Transactional
    public ReservaDTO insert(ReservaDTO dto, Authentication authentication) {
        // Validações
        if (dto.getLocalRetiradaId() == null) {
            throw new IllegalArgumentException("Local de retirada é obrigatório");
        }
        if (dto.getDataReserva() == null || dto.getDataVencimento() == null) {
            throw new IllegalArgumentException("Datas não podem ser nulas");
        }
        if (!dto.getDataVencimento().isAfter(dto.getDataReserva())) {
            throw new IllegalArgumentException("Data de vencimento deve ser posterior à data de reserva");
        }
        if (dto.getVeiculoId() == null) {
            throw new IllegalArgumentException("Veículo é obrigatório");
        }
        if (dto.getMotoristaId() == null) {
            throw new IllegalArgumentException("Motorista é obrigatório");
        }

        // Usar o usuário logado como dono da reserva
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        dto.setUsuarioId(usuarioLogado.getId());

        // Verificar se o veículo existe e está disponível
        Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado"));

        // Atualizar status do veículo
        veiculo.setStatusVeiculo(StatusVeiculo.RESERVADO);
        veiculoRepository.save(veiculo);

        // Verificar se o motorista existe
        Motorista motorista = motoristaRepository.findById(dto.getMotoristaId())
                .orElseThrow(() -> new ResourceNotFoundException("Motorista não encontrado"));

        // Converter DTO para entidade
        Reserva entity = convertToEntity(dto);

        // Definir status inicial da reserva
        entity.setStatus(StatusReserva.AGUARDANDO_PAGAMENTO);


        // Salvar a reserva
        entity = repository.save(entity);

        return convertToDTO(entity);
    }

    @Transactional
    public void confirmarPagamentoReserva(Long reservaId) {
        Reserva reserva = repository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada"));

        if (reserva.getStatus() != StatusReserva.AGUARDANDO_PAGAMENTO) {
            throw new RuntimeException("Reserva não está aguardando pagamento");
        }


        reserva.setStatus(StatusReserva.PENDENTE);

        if (reserva.getVeiculo() != null) {
            Veiculo veiculo = reserva.getVeiculo();
            veiculo.setStatusVeiculo(StatusVeiculo.RESERVADO);
            veiculoRepository.save(veiculo);
        }

        repository.save(reserva);
    }

    @Transactional
    public ReservaDTO update(Long id, ReservaDTO dto) {
        try {
            Reserva entity = repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com ID: " + id));

            // Se a reserva está passando para CANCELADA, atualize o status do veículo para DISPONÍVEL
            if (dto.getStatus() == StatusReserva.CANCELADO && entity.getStatus() != StatusReserva.CANCELADO) {
                if (entity.getVeiculo() != null) {
                    Veiculo veiculo = entity.getVeiculo();
                    veiculo.setStatusVeiculo(StatusVeiculo.DISPONIVEL);
                    veiculoRepository.save(veiculo);
                }
            }

            if (dto.getStatus() == StatusReserva.ENCERRADO && entity.getStatus() != StatusReserva.ENCERRADO) {
                if (entity.getVeiculo() != null) {
                    Veiculo veiculo = entity.getVeiculo();
                    veiculo.setStatusVeiculo(StatusVeiculo.DISPONIVEL);
                    veiculoRepository.save(veiculo);
                }
            }

            if (dto.getStatus() == StatusReserva.EM_ANDAMENTO && entity.getStatus() != StatusReserva.EM_ANDAMENTO) {
                if (entity.getVeiculo() != null) {
                    Veiculo veiculo = entity.getVeiculo();
                    veiculo.setStatusVeiculo(StatusVeiculo.EM_USO);
                    veiculoRepository.save(veiculo);
                }
            }


            if (dto.getDataReserva() == null || dto.getDataVencimento() == null) {
                throw new IllegalArgumentException("Datas não podem ser nulas");
            }
            if (!dto.getDataVencimento().isAfter(dto.getDataReserva())) {
                throw new IllegalArgumentException("Data de vencimento deve ser posterior à data de reserva");
            }

            if (dto.getMotoristaId() != null &&
                    (entity.getMotorista() == null || !dto.getMotoristaId().equals(entity.getMotorista().getId()))) {

                Motorista motorista = motoristaRepository.findById(dto.getMotoristaId())
                        .orElseThrow(() -> new ResourceNotFoundException("Motorista não encontrado"));
                entity.setMotorista(motorista);
            }

            if (dto.getVeiculoId() != null &&
                    (entity.getVeiculo() == null || !dto.getVeiculoId().equals(entity.getVeiculo().getId()))) {

                if (entity.getVeiculo() != null) {
                    Veiculo veiculoAntigo = entity.getVeiculo();
                    veiculoAntigo.setStatusVeiculo(StatusVeiculo.DISPONIVEL);
                    veiculoRepository.save(veiculoAntigo);
                }

                Veiculo novoVeiculo = veiculoRepository.findById(dto.getVeiculoId())
                        .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado"));
                novoVeiculo.setStatusVeiculo(StatusVeiculo.RESERVADO);
                veiculoRepository.save(novoVeiculo);
                entity.setVeiculo(novoVeiculo);
            }

            if (dto.getLocalRetiradaId() != null) {
                Filial filial = filialRepository.findById(dto.getLocalRetiradaId())
                        .orElseThrow(() -> new ResourceNotFoundException("Local de retirada não encontrado"));
                entity.setLocalRetirada(filial);
            }

            entity.setDataReserva(dto.getDataReserva());
            entity.setDataVencimento(dto.getDataVencimento());
            if (dto.getCategoria() != null) {
                entity.setCategoria(dto.getCategoria());
            }
            if (dto.getStatus() != null) {
                entity.setStatus(dto.getStatus());
            }

            entity = repository.save(entity);

            return convertToDTO(entity);
        }
        catch (JpaObjectRetrievalFailureException e) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if(!repository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    public ReservaDTO convertToDTO(Reserva entity) {
        ReservaDTO dto = new ReservaDTO();
        dto.setId(entity.getId());
        dto.setDataReserva(entity.getDataReserva());
        dto.setDataVencimento(entity.getDataVencimento());
        dto.setCategoria(entity.getCategoria());
        dto.setStatus(entity.getStatus());
        dto.setLocalRetiradaId(entity.getLocalRetirada() != null ? entity.getLocalRetirada().getId() : null);
        dto.setUsuarioId(entity.getUsuario() != null ? entity.getUsuario().getId() : null);
        dto.setVeiculoId(entity.getVeiculo() != null ? entity.getVeiculo().getId() : null);
        dto.setMotoristaId(entity.getMotorista() != null ? entity.getMotorista().getId() : null);
        dto.setValorTotal(entity.getValorTotal());
        return dto;
    }

    public Reserva convertToEntity(ReservaDTO dto) {
        Reserva entity = new Reserva();
        entity.setId(dto.getId());
        entity.setDataReserva(dto.getDataReserva());
        entity.setDataVencimento(dto.getDataVencimento());
        entity.setCategoria(dto.getCategoria());
        entity.setStatus(dto.getStatus());

        // Buscar o usuário pelo ID
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        entity.setUsuario(usuario);

        // Buscar a filial pelo ID
        Filial filial = filialRepository.findById(dto.getLocalRetiradaId())
                .orElseThrow(() -> new ResourceNotFoundException("Local de retirada não encontrado"));
        entity.setLocalRetirada(filial);

        // Buscar o veículo pelo ID e calcular valor total
        if (dto.getVeiculoId() != null) {
            Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado"));
            entity.setVeiculo(veiculo);

            long dias = ChronoUnit.DAYS.between(dto.getDataReserva(), dto.getDataVencimento());

            if (dias <= 0) {
                dias = 1;
            }

            BigDecimal valorDiaria = BigDecimal.valueOf(veiculo.getValorDiaria());
            BigDecimal quantidadeDias = BigDecimal.valueOf(dias);
            BigDecimal valorTotal = valorDiaria.multiply(quantidadeDias);

            entity.setValorTotal(valorTotal);
        }

        // Buscar o motorista pelo ID
        if (dto.getMotoristaId() != null) {
            Motorista motorista = motoristaRepository.findById(dto.getMotoristaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Motorista não encontrado"));
            entity.setMotorista(motorista);
        }

        return entity;
    }

}


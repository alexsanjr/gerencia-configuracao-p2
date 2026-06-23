package com.cefet.vocealuga.service;

import com.cefet.vocealuga.dtos.VeiculoDTO;
import com.cefet.vocealuga.entities.Estoque;
import com.cefet.vocealuga.entities.Filial;
import com.cefet.vocealuga.entities.Veiculo;
import com.cefet.vocealuga.entities.enums.Cor;
import com.cefet.vocealuga.entities.enums.Grupo;
import com.cefet.vocealuga.entities.enums.StatusVeiculo;
import com.cefet.vocealuga.repositories.EstoqueRepository;
import com.cefet.vocealuga.repositories.VeiculoRepository;
import com.cefet.vocealuga.services.VeiculoService;
import com.cefet.vocealuga.services.exceptions.DatabaseException;
import com.cefet.vocealuga.services.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @InjectMocks
    private VeiculoService veiculoService;

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private EstoqueRepository estoqueRepository;

    private Veiculo veiculo;
    private VeiculoDTO veiculoDTO;
    private Estoque estoque;
    private Filial filial;

    @BeforeEach
    void setUp() {
        filial = new Filial();
        filial.setId(1L);
        filial.setNome("Filial Teste");

        estoque = new Estoque();
        estoque.setId(1L);
        estoque.setFilial(filial);

        veiculo = new Veiculo();
        veiculo.setId(1L);
        veiculo.setMarca("Toyota");
        veiculo.setModelo("Corolla");
        veiculo.setGrupo(Grupo.A);
        veiculo.setAno(2023);
        veiculo.setCor(Cor.BRANCO);
        veiculo.setValorDiaria(150.0);
        veiculo.setQuilometragem(1000L);
        veiculo.setPlaca("ABC1234");
        veiculo.setStatusVeiculo(StatusVeiculo.DISPONIVEL);
        veiculo.setEstoque(estoque);
        veiculo.setFilial(filial);

        veiculoDTO = new VeiculoDTO();
        veiculoDTO.setMarca("Toyota");
        veiculoDTO.setModelo("Corolla");
        veiculoDTO.setGrupo(Grupo.A);
        veiculoDTO.setAno(2023);
        veiculoDTO.setCor(Cor.BRANCO);
        veiculoDTO.setValorDiaria(150.0);
        veiculoDTO.setQuilometragem(1000L);
        veiculoDTO.setPlaca("ABC1234");
        veiculoDTO.setEstoqueId(1L);
        veiculoDTO.setFilialId(1L);
    }

    @Test
    void deveCadastrarVeiculoComDadosValidos() {
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        when(veiculoRepository.save(any(Veiculo.class))).thenAnswer(invocation -> {
            Veiculo savedVeiculo = invocation.getArgument(0);
            savedVeiculo.setId(1L);
            savedVeiculo.setStatusVeiculo(StatusVeiculo.DISPONIVEL);
            return savedVeiculo;
        });

        VeiculoDTO resultado = veiculoService.insert(veiculoDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Toyota", resultado.getMarca());
        assertEquals("Corolla", resultado.getModelo());
        assertEquals(Grupo.A, resultado.getGrupo());
        assertEquals(2023, resultado.getAno());
        assertEquals(Cor.BRANCO, resultado.getCor());
        assertEquals(150.0, resultado.getValorDiaria());
        assertEquals(1000L, resultado.getQuilometragem());
        assertEquals("ABC1234", resultado.getPlaca());
        assertEquals(StatusVeiculo.DISPONIVEL, resultado.getStatusVeiculo());
        assertEquals(1L, resultado.getEstoqueId());
        assertEquals(1L, resultado.getFilialId());

        verify(estoqueRepository).findById(1L);
        verify(veiculoRepository).save(any(Veiculo.class));
    }

    @Test
    void deveImpedirCadastroDeVeiculoComPlacaExistente() {
        veiculoDTO.setPlaca("ABC1234");
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        when(veiculoRepository.save(any(Veiculo.class)))
                .thenThrow(new DataIntegrityViolationException("Violação de chave única: placa"));

        DatabaseException exception = assertThrows(DatabaseException.class,
                () -> veiculoService.insert(veiculoDTO));

        assertEquals("Veículo já cadastrado", exception.getMessage());
        verify(estoqueRepository).findById(1L);
        verify(veiculoRepository).save(any(Veiculo.class));
    }

    @Test
    void deveImpedirCadastroComCamposObrigatoriosVazios() {
        VeiculoDTO veiculoDTOInvalido = new VeiculoDTO();
        veiculoDTOInvalido.setPlaca("XYZ9876");
        veiculoDTOInvalido.setEstoqueId(1L);
        veiculoDTOInvalido.setFilialId(1L);
        veiculoDTOInvalido.setAno(2023);
        veiculoDTOInvalido.setCor(Cor.BRANCO);
        veiculoDTOInvalido.setGrupo(Grupo.A);
        veiculoDTOInvalido.setQuilometragem(null);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<VeiculoDTO>> violations = validator.validate(veiculoDTOInvalido);

        assertFalse(violations.isEmpty(), "Deveria haver violações de validação");
        assertEquals(3, violations.size(), "Deveria haver 3 campos com violações");

        Set<String> mensagens = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(mensagens.contains("Campo requerido"),
                "A mensagem de erro deve conter 'Campo requerido'");

        Set<String> camposComErro = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
        assertTrue(camposComErro.contains("marca"), "Marca deveria estar na lista de campos com erro");
        assertTrue(camposComErro.contains("modelo"), "Modelo deveria estar na lista de campos com erro");
        assertTrue(camposComErro.contains("quilometragem"), "Quilometragem deveria estar na lista de campos com erro");
    }

    @Test
    void deveBuscarVeiculoPorPlacaExistente() {
        String placaProcurada = "ABC1234";
        when(veiculoRepository.findByPlaca(placaProcurada)).thenReturn(Optional.of(veiculo));

        VeiculoDTO resultado = veiculoService.findByPlaca(placaProcurada);

        assertNotNull(resultado, "O veículo deveria ser encontrado");
        assertEquals(placaProcurada, resultado.getPlaca(), "A placa do veículo encontrado deve corresponder à placa buscada");
        assertEquals("Toyota", resultado.getMarca());
        assertEquals("Corolla", resultado.getModelo());
        assertEquals(Grupo.A, resultado.getGrupo());
        assertEquals(2023, resultado.getAno());

        verify(veiculoRepository).findByPlaca(placaProcurada);
    }

    @Test
    void deveLancarExcecaoAoBuscarVeiculoComPlacaInexistente() {
        String placaInexistente = "XYZ9999";
        when(veiculoRepository.findByPlaca(placaInexistente)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> veiculoService.findByPlaca(placaInexistente),
                "Deveria lançar ResourceNotFoundException para placa inexistente"
        );

        assertTrue(
                exception.getMessage().contains("Veículo não encontrado"),
                "A mensagem deve informar que o veículo não foi encontrado"
        );
        assertTrue(
                exception.getMessage().contains(placaInexistente),
                "A mensagem deve conter a placa não encontrada"
        );
        verify(veiculoRepository).findByPlaca(placaInexistente);
    }

    @Test
    void deveAtualizarVeiculoComDadosValidos() {
        Long veiculoId = 1L;
        VeiculoDTO veiculoDTOAtualizado = new VeiculoDTO();
        veiculoDTOAtualizado.setId(veiculoId);
        veiculoDTOAtualizado.setMarca("Honda");
        veiculoDTOAtualizado.setModelo("Civic");
        veiculoDTOAtualizado.setGrupo(Grupo.B);
        veiculoDTOAtualizado.setAno(2022);
        veiculoDTOAtualizado.setCor(Cor.PRETO);
        veiculoDTOAtualizado.setValorDiaria(200.0);
        veiculoDTOAtualizado.setQuilometragem(5000L);
        veiculoDTOAtualizado.setPlaca("DEF5678");
        veiculoDTOAtualizado.setStatusVeiculo(StatusVeiculo.MANUTENCAO);
        veiculoDTOAtualizado.setEstoqueId(1L);
        veiculoDTOAtualizado.setFilialId(1L);

        when(veiculoRepository.getReferenceById(veiculoId)).thenReturn(veiculo);
        when(estoqueRepository.findById(1L)).thenReturn(Optional.of(estoque));
        when(veiculoRepository.save(any(Veiculo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VeiculoDTO resultado = veiculoService.update(veiculoId, veiculoDTOAtualizado);

        assertNotNull(resultado);
        assertEquals(veiculoId, resultado.getId());
        assertEquals("Honda", resultado.getMarca());
        assertEquals("Civic", resultado.getModelo());
        assertEquals(Grupo.B, resultado.getGrupo());
        assertEquals(2022, resultado.getAno());
        assertEquals(Cor.PRETO, resultado.getCor());
        assertEquals(200.0, resultado.getValorDiaria());
        assertEquals(5000L, resultado.getQuilometragem());
        assertEquals("DEF5678", resultado.getPlaca());
        assertEquals(StatusVeiculo.MANUTENCAO, resultado.getStatusVeiculo());

        verify(veiculoRepository).getReferenceById(veiculoId);
        verify(estoqueRepository).findById(1L);
        verify(veiculoRepository).save(any(Veiculo.class));
    }
}
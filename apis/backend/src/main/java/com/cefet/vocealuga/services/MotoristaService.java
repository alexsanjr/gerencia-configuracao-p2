package com.cefet.vocealuga.services;

import com.cefet.vocealuga.dtos.MotoristaDTO;
import com.cefet.vocealuga.dtos.MeDTO;
import com.cefet.vocealuga.entities.Motorista;
import com.cefet.vocealuga.entities.Usuario;
import com.cefet.vocealuga.repositories.MotoristaRepository;
import com.cefet.vocealuga.repositories.UsuarioRepository;
import com.cefet.vocealuga.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MotoristaService {

    @Autowired
    private MotoristaRepository repository;


    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public MotoristaDTO criarMotoristaPeloUsuarioLogado(String cnh, Authentication authentication) {
        MeDTO userInfo = authService.findMe(authentication);

        Usuario usuario = usuarioRepository.findByEmail(userInfo.getEmail());


        Motorista motorista = new Motorista();
        motorista.setCnh(cnh);
        motorista.setNome(userInfo.getNome());
        motorista.setCpf(userInfo.getDocumento());
        motorista.setDataNascimento(userInfo.getDataDeNascimento());

        Motorista motoristaSalvo = repository.save(motorista);

        return convertToDTO(motoristaSalvo);
    }

    @Transactional(readOnly = true)
    public MotoristaDTO findById(Long id) {
        Motorista motorista = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Motorista não encontrado com ID: " + id));
        return convertToDTO(motorista);
    }

    @Transactional(readOnly = true)
    public List<MotoristaDTO> findAll() {
        List<Motorista> list = repository.findAll();
        return list.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MotoristaDTO criarMotorista(MotoristaDTO dto) {

        Motorista motorista = new Motorista();
        motorista.setCnh(dto.getCnh());
        motorista.setNome(dto.getNome());
        motorista.setCpf(dto.getCpf());
        motorista.setDataNascimento(dto.getDataNascimento());

        Motorista motoristaSalvo = repository.save(motorista);

        MotoristaDTO resultDto = convertToDTO(motoristaSalvo);
        return resultDto;
    }
    private MotoristaDTO convertToDTO(Motorista motorista) {
        MotoristaDTO dto = new MotoristaDTO();
        dto.setId(motorista.getId());
        dto.setCnh(motorista.getCnh());
        dto.setNome(motorista.getNome());
        dto.setCpf(motorista.getCpf());
        dto.setDataNascimento(motorista.getDataNascimento());
        return dto;
    }
}
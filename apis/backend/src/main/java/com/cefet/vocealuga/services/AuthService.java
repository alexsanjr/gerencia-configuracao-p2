package com.cefet.vocealuga.services;

import com.cefet.vocealuga.dtos.MeDTO;
import com.cefet.vocealuga.entities.Cliente;
import com.cefet.vocealuga.entities.Funcionario;
import com.cefet.vocealuga.entities.Reserva;
import com.cefet.vocealuga.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private ReservaRepository reservaRepository;

    public MeDTO findMe(Authentication auth) {
        Object principal = auth.getPrincipal();
        MeDTO dto = new MeDTO();
        dto.setRole(getRole(auth)); // extrai a role via authorities

        if (principal instanceof Funcionario func) {
            dto.setNome(func.getNome());
            dto.setEmail(func.getEmail());
            dto.setTelefone(func.getTelefone());
            dto.setCargo(func.getCargo());
            if (func.getFilial() != null) {
                dto.setFilial_id(func.getFilial().getId());
            }
            dto.setDocumento(func.getDocumento());
            dto.setDataDeNascimento(func.getDataDeNascimento());

            List<Long> reservas = reservaRepository.findIdsByUsuarioId(func.getId());
            dto.setReservas(reservas);


        } else if (principal instanceof Cliente cliente) {
            dto.setNome(cliente.getNome());
            dto.setEmail(cliente.getEmail());
            dto.setTelefone(cliente.getTelefone());
            dto.setDataDeNascimento(cliente.getDataDeNascimento());
            dto.setPontosFidelidade(cliente.getPontosFidelidade());
            dto.setDocumento(cliente.getDocumento());

            List<Long> reservas = reservaRepository.findIdsByUsuarioId(cliente.getId());
            dto.setReservas(reservas);

        }

        return dto;
    }

    private String getRole(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst().orElse("ROLE_CLIENTE");
    }
}

package com.cefet.vocealuga.services;

import com.cefet.vocealuga.dtos.PagamentoDTO;
import org.springframework.stereotype.Service;

@Service
public class PagamentoService {

    public PagamentoDTO processarPagamento(PagamentoDTO dto) {
        if ("pix".equalsIgnoreCase(dto.getMetodo())) {
            System.out.println("Pagamento via PIX aprovado.");
        } else if ("credit-card".equalsIgnoreCase(dto.getMetodo()) || "debit-card".equalsIgnoreCase(dto.getMetodo())) {
            if (dto.getCardNumber() != null && dto.getCardNumber().length() >= 12) {
                System.out.println("Pagamento com cartão aprovado.");
            } else {
                throw new RuntimeException("Número de cartão inválido.");
            }
        } else {
            throw new RuntimeException("Método de pagamento inválido.");
        }

        return dto;
    }
}

package com.cefet.vocealuga.controllers;

import com.cefet.vocealuga.dtos.PagamentoDTO;
import com.cefet.vocealuga.dtos.ReservaDTO;
import com.cefet.vocealuga.entities.Pagamento;
import com.cefet.vocealuga.services.PagamentoService;
import com.cefet.vocealuga.services.PdfService;
import com.cefet.vocealuga.services.ReservaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pagamento")
public class PagamentoController {

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private ReservaService reservaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_FUNCIONARIO', 'ROLE_GERENTE', 'ROLE_ADMIN', 'ROLE_CLIENTE')")
    public ResponseEntity<Map<String, String>> insert(@Valid @RequestBody PagamentoDTO dto) {
        pagamentoService.processarPagamento(dto);
        return ResponseEntity.ok(Map.of(
                "mensagem", "Email de confirmação enviado. Verifique sua caixa de entrada.",
                "status", "pendente"
        ));
    }

    @GetMapping("/confirmar/{token}")
    public ResponseEntity<Map<String, String>> confirmarPagamento(@PathVariable String token) {
        try {
            boolean confirmado = pagamentoService.confirmarPagamento(token);
            if (confirmado) {
                return ResponseEntity.ok(Map.of(
                        "mensagem", "Pagamento confirmado com sucesso!",
                        "status", "confirmado"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "erro", "Erro ao confirmar pagamento",
                        "status", "erro"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "erro", e.getMessage(),
                    "status", "erro"
            ));
        }
    }

    @GetMapping("/comprovante/{token}")
    @PreAuthorize("hasAnyRole('ROLE_FUNCIONARIO', 'ROLE_GERENTE', 'ROLE_ADMIN', 'ROLE_CLIENTE')")
    public ResponseEntity<byte[]> downloadComprovante(@PathVariable String token) {
        try {
            Pagamento pagamento = pagamentoService.buscarPorToken(token);

            if (!pagamento.getConfirmado()) {
                return ResponseEntity.badRequest().build();
            }

            ReservaDTO reserva = null;
            if (pagamento.getReservaId() != null) {
                reserva = reservaService.findById(pagamento.getReservaId());
            }

            byte[] pdfBytes = pdfService.gerarComprovantePagamento(pagamento, reserva);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                    org.springframework.http.ContentDisposition.attachment()
                            .filename("comprovante_" + token + ".pdf")
                            .build()
            );

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
package com.cefet.vocealuga.services;

import com.cefet.vocealuga.dtos.PagamentoDTO;
import com.cefet.vocealuga.dtos.ReservaDTO;
import com.cefet.vocealuga.entities.Pagamento;
import com.cefet.vocealuga.entities.Usuario;
import com.cefet.vocealuga.repositories.PagamentoRepository;
import com.cefet.vocealuga.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private SmtpEmailService smtpEmailService;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private PdfService pdfService;

    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;

    private static final Logger logger = LoggerFactory.getLogger(PagamentoService.class);

    @Transactional
    public PagamentoDTO processarPagamento(PagamentoDTO dto) {
        validarDadosPagamento(dto);

        ReservaDTO reserva = null;
        BigDecimal valorPagamento = dto.getValor();

        if (dto.getReservaId() != null) {
            reserva = reservaService.findById(dto.getReservaId());
            valorPagamento = reserva.getValorTotal();
        }

        // Obter usuário logado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = (Usuario) authentication.getPrincipal();

        // Gerar token único para confirmação
        String token = UUID.randomUUID().toString();

        // Criar registro temporário do pagamento
        Pagamento pagamento = new Pagamento();
        pagamento.setToken(token);
        pagamento.setMetodo(dto.getMetodo());
        pagamento.setValor(valorPagamento);
        pagamento.setReservaId(dto.getReservaId());
        pagamento.setCardNumber(mascaraCartao(dto.getCardNumber()));
        pagamento.setCardExpiry(dto.getCardExpiry());
        pagamento.setCardCVV("***");
        pagamento.setCardName(dto.getCardName());
        pagamento.setEmailUsuario(usuario.getEmail());
        pagamento.setDataExpiracao(LocalDateTime.now().plusHours(24));
        pagamento.setConfirmado(false);

        pagamentoRepository.save(pagamento);

        enviarEmailConfirmacao(usuario.getEmail(), usuario.getNome(), token, dto.getMetodo());

        PagamentoDTO resposta = new PagamentoDTO();
        resposta.setMetodo(dto.getMetodo());
        resposta.setValor(dto.getValor());
        resposta.setReservaId(dto.getReservaId());

        return resposta;
    }

    @Transactional
    public boolean confirmarPagamento(String token) {
        Pagamento pagamento = pagamentoRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token de pagamento inválido"));

        if (pagamento.getDataExpiracao().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token de confirmação expirado");
        }

        if (pagamento.getConfirmado()) {
            throw new RuntimeException("Pagamento já confirmado anteriormente");
        }

        pagamento.setConfirmado(true);
        pagamentoRepository.save(pagamento);

        if (pagamento.getReservaId() != null) {
            reservaService.confirmarPagamentoReserva(pagamento.getReservaId());
        }

        // Processar pagamento definitivamente
        processarPagamentoDefinitivo(pagamento);

        // Gerar comprovante PDF e enviar por email
        try {
            ReservaDTO reserva = null;
            if (pagamento.getReservaId() != null) {
                reserva = reservaService.findById(pagamento.getReservaId());
            }

            byte[] pdfBytes = pdfService.gerarComprovantePagamento(pagamento, reserva);

            // Enviar email com comprovante em anexo
            smtpEmailService.sendPaymentConfirmation(
                    pagamento.getEmailUsuario(),
                    pagamento.getToken(),
                    "R$ " + pagamento.getValor(),
                    pdfBytes
            );

            logger.info("Comprovante PDF enviado por email para pagamento: {}", pagamento.getToken());

        } catch (Exception e) {
            logger.error("Erro ao gerar/enviar comprovante PDF: ", e);
        }

        return true;
    }

    private void validarDadosPagamento(PagamentoDTO dto) {
        if (dto.getReservaId() == null && (dto.getValor() == null || dto.getValor().compareTo(BigDecimal.ZERO) <= 0)) {
            throw new RuntimeException("Valor do pagamento deve ser maior que zero");
        }

        if ("pix".equalsIgnoreCase(dto.getMetodo())) {
            return;
        }

        if ("credit-card".equalsIgnoreCase(dto.getMetodo()) || "debit-card".equalsIgnoreCase(dto.getMetodo())) {
            if (dto.getCardNumber() == null || dto.getCardNumber().length() < 12) {
                throw new RuntimeException("Número de cartão inválido");
            }
            if (dto.getCardExpiry() == null || dto.getCardExpiry().isEmpty()) {
                throw new RuntimeException("Data de validade do cartão é obrigatória");
            }
            if (dto.getCardCVV() == null || dto.getCardCVV().length() < 3) {
                throw new RuntimeException("CVV inválido");
            }
            if (dto.getCardName() == null || dto.getCardName().trim().isEmpty()) {
                throw new RuntimeException("Nome no cartão é obrigatório");
            }
        } else {
            throw new RuntimeException("Método de pagamento inválido");
        }
    }

    private void enviarEmailConfirmacao(String email, String nomeUsuario, String token, String metodo) {
        String linkConfirmacao = baseUrl + "/pagamento/confirmar/" + token;

        String subject = "Confirmação de Pagamento - VocêAluga";
        String body = createHtmlPaymentConfirmation(nomeUsuario, metodo, linkConfirmacao);

        smtpEmailService.sendEmailAsync(email, subject, body);
    }

    public Pagamento buscarPorToken(String token) {
        return pagamentoRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado"));
    }

    private String createHtmlPaymentConfirmation(String nomeUsuario, String metodo, String linkConfirmacao) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background: #f9f9f9; }
                    .payment-info { background: white; padding: 15px; border-radius: 5px; margin: 15px 0; }
                    .footer { padding: 10px; text-align: center; color: #666; font-size: 12px; }
                    .highlight { color: #4CAF50; font-weight: bold; }
                    .btn { background: #4CAF50; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 15px 0; }
                    .warning { color: #ff9800; font-weight: bold; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🚗 VocêAluga</h1>
                        <p>Confirmação de Pagamento</p>
                    </div>
                    <div class="content">
                        <h2>Olá, <span class="highlight">%s</span>! 👋</h2>
                        <p>Recebemos sua solicitação de pagamento via <strong>%s</strong>.</p>

                        <div class="payment-info">
                            <h3>🔒 Para confirmar seu pagamento:</h3>
                            <p>Clique no botão abaixo para processar seu pagamento com segurança.</p>
                            
                            <div style="text-align: center;">
                                <a href="%s" class="btn">✅ Confirmar Pagamento</a>
                            </div>
                            
                            <p class="warning">⚠️ Este link expira em 24 horas.</p>
                            <p><strong>Data/Hora:</strong> %s</p>
                        </div>

                        <p>Se você não fez esta solicitação, ignore este email.</p>
                        <p>Seu pagamento só será processado após a confirmação.</p>
                    </div>
                    <div class="footer">
                        <p>Este é um email automático do sistema VocêAluga</p>
                        <p>Não responda este email</p>
                        <p>Em caso de dúvidas, entre em contato conosco</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(nomeUsuario, metodo.toUpperCase(), linkConfirmacao, LocalDateTime.now().toString());
    }

    private void processarPagamentoDefinitivo(Pagamento pagamento) {
        // Aqui você integraria com o gateway de pagamento real
        if ("pix".equalsIgnoreCase(pagamento.getMetodo())) {
            System.out.println("Processando pagamento PIX confirmado");
        } else {
            System.out.println("Processando pagamento com cartão confirmado");
        }

        // Lógica adicional: atualizar status da reserva, etc.
    }

    private String mascaraCartao(String numeroCartao) {
        if (numeroCartao == null || numeroCartao.length() < 8) {
            return "****";
        }
        return "**** **** **** " + numeroCartao.substring(numeroCartao.length() - 4);
    }
}
package com.cefet.vocealuga.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.util.concurrent.CompletableFuture;

@Service
public class SmtpEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${MAIL_FROM}")
    private String fromEmail;

    @Value("${mail.from.name}")
    private String fromName;

    @Async
    public CompletableFuture<Void> sendEmailAsync(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setReplyTo(fromEmail);

            message.setHeader("X-Mailer", "VocêAluga System");
            message.setHeader("X-Priority", "3");
            message.setHeader("List-Unsubscribe", "<mailto:" + fromEmail + ">");

            mailSender.send(message);
            System.out.println("Email SMTP enviado com sucesso para: " + to);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            System.err.println("Erro ao enviar email SMTP: " + e.getMessage());
            throw new RuntimeException("Falha ao enviar email SMTP", e);
        }
    }

    @Async
    public CompletableFuture<Void> sendEmailWithAttachmentAsync(String to, String subject, String body, byte[] attachmentData, String attachmentName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setReplyTo(fromEmail);

            if (attachmentData != null && attachmentData.length > 0) {
                helper.addAttachment(attachmentName, new ByteArrayResource(attachmentData), "application/pdf");
            }

            message.setHeader("X-Mailer", "VocêAluga System");
            message.setHeader("X-Priority", "3");
            message.setHeader("List-Unsubscribe", "<mailto:" + fromEmail + ">");

            mailSender.send(message);
            System.out.println("Email SMTP com anexo enviado com sucesso para: " + to);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            System.err.println("Erro ao enviar email SMTP com anexo: " + e.getMessage());
            throw new RuntimeException("Falha ao enviar email SMTP com anexo", e);
        }
    }

    public void sendTestEmail(String to) {
        String subject = "✅ Teste SMTP - VocêAluga";
        String body = createHtmlTestEmail();
        sendEmailAsync(to, subject, body);
    }

    public void sendPaymentConfirmation(String to, String pagamentoId, String valor, byte[] comprovantePdf) {
        String subject = "Confirmação de Pagamento - VocêAluga";
        String body = createHtmlPaymentConfirmation(pagamentoId, valor);
        //sendEmailAsync(to, subject, body);
        String attachmentName = "comprovante_pagamento_" + pagamentoId + ".pdf";
        sendEmailWithAttachmentAsync(to, subject, body, comprovantePdf, attachmentName);
    }

    public void sendWelcomeEmail(String to, String username) {
        String subject = "Bem-vindo ao VocêAluga! 🚗";
        String body = createHtmlWelcomeEmail(username);
        sendEmailAsync(to, subject, body);
    }

    private String createHtmlWelcomeEmail(String username) {
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
                .welcome-info { background: white; padding: 15px; border-radius: 5px; margin: 15px 0; }
                .footer { padding: 10px; text-align: center; color: #666; font-size: 12px; }
                .highlight { color: #4CAF50; font-weight: bold; }
                .btn { background: #4CAF50; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block; margin: 15px 0; }
                .features { list-style: none; padding: 0; }
                .features li { padding: 8px 0; }
                .features li:before { content: "✅ "; color: #4CAF50; font-weight: bold; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>🚗 VocêAluga</h1>
                    <p>Bem-vindo à nossa plataforma!</p>
                </div>
                <div class="content">
                    <h2>Olá, <span class="highlight">%s</span>! 👋</h2>
                    <p>Sua conta foi criada com sucesso no VocêAluga!</p>
                    
                    <div class="welcome-info">
                        <h3>O que você pode fazer agora:</h3>
                        <ul class="features">
                            <li>Navegar por nossa frota de veículos</li>
                            <li>Fazer reservas de forma fácil e rápida</li>
                            <li>Gerenciar suas reservas</li>
                            <li>Acompanhar histórico de aluguéis</li>
                            <li>Receber confirmações por email</li>
                        </ul>
                        
                        <p><strong>Data de cadastro:</strong> %s</p>
                    </div>
                    
                    <p>Agora você faz parte da família VocêAluga! 🎉</p>
                    <p>Estamos animados para ajudá-lo em suas próximas viagens.</p>
                    
                    <div style="text-align: center;">
                        <a href="#" class="btn">Explorar Veículos</a>
                    </div>
                </div>
                <div class="footer">
                    <p>Obrigado por escolher o VocêAluga!</p>
                    <p>Este é um email automático do sistema VocêAluga</p>
                    <p>Não responda este email</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(username, java.time.LocalDateTime.now().toString());
    }

    private String createHtmlPaymentConfirmation(String pagamentoId, String valor) {
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
                .success { color: #4CAF50; font-weight: bold; }
                .attachment-info { background: #e8f5e8; padding: 10px; border-radius: 5px; margin: 10px 0; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>🚗 VocêAluga</h1>
                    <p>Confirmação de Pagamento</p>
                </div>
                <div class="content">
                    <h2 class="success">✅ Pagamento Confirmado!</h2>
                    <p>Seu pagamento foi processado com sucesso.</p>

                    <div class="payment-info">
                        <p><strong>ID do Pagamento:</strong> %s</p>
                        <p><strong>Valor:</strong> %s</p>
                        <p><strong>Data/Hora:</strong> %s</p>
                        <p><strong>Status:</strong> <span class="success">Aprovado</span></p>
                    </div>

                    <div class="attachment-info">
                        <p><strong>📄 Comprovante em Anexo</strong></p>
                        <p>Seu comprovante de pagamento está anexado a este email em formato PDF.</p>
                        <p>Guarde este documento para seus registros.</p>
                    </div>

                    <p>Obrigado por utilizar o VocêAluga!</p>
                    <p>Em caso de dúvidas, entre em contato conosco.</p>
                </div>
                <div class="footer">
                    <p>Este é um email automático do sistema VocêAluga</p>
                    <p>Não responda este email</p>
                </div>
            </div>
        </body>
        </html>
        """.formatted(pagamentoId, valor, java.time.LocalDateTime.now().toString());
    }

    private String createHtmlTestEmail() {
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
                    .footer { padding: 10px; text-align: center; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🚗 VocêAluga</h1>
                        <p>Teste de Email SMTP</p>
                    </div>
                    <div class="content">
                        <h2>Email funcionando perfeitamente!</h2>
                        <p>Se você recebeu esta mensagem, o serviço SMTP está configurado corretamente.</p>
                        <p><strong>Data/Hora:</strong> %s</p>
                        <p><strong>Servidor:</strong> Gmail SMTP</p>
                        <p><strong>Status:</strong> ✅ Ativo</p>
                    </div>
                    <div class="footer">
                        <p>Este é um email automático do sistema VocêAluga</p>
                        <p>Não responda este email</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(java.time.LocalDateTime.now().toString());
    }
}
package com.cefet.vocealuga.controllers;

import com.cefet.vocealuga.services.SmtpEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class EmailController {

    @Autowired
    private SmtpEmailService smtpEmailService;

    @GetMapping("/test-email")
    public ResponseEntity<Map<String, String>> testSmtpEmail(@RequestParam String to) {
        try {
            smtpEmailService.sendTestEmail(to);
            return ResponseEntity.ok(Map.of(
                    "status", "sucesso",
                    "mensagem", "Email sendo enviado de forma assíncrona para: " + to,
                    "tipo", "SMTP",
                    "observacao", "O email chegará em alguns segundos"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "erro",
                    "mensagem", "Falha ao enviar email: " + e.getMessage(),
                    "tipo", "SMTP"
            ));
        }
    }
}
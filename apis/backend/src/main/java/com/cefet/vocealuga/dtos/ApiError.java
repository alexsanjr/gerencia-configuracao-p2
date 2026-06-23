package com.cefet.vocealuga.dtos;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public class ApiError {
    private int status;
    private String mensagem;
    private String path;
    private Instant timestamp;

    public ApiError(HttpStatus status, String mensagem, String path) {
        this.status = status.value();
        this.mensagem = mensagem;
        this.path = path;
        this.timestamp = Instant.now();
    }

    public int getStatus() {
        return status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getPath() {
        return path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}

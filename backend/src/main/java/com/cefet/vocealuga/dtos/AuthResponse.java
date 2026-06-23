package com.cefet.vocealuga.dtos;

public class AuthResponse {
    private String token;
    private String tipo;
    private String mensagem;

    public AuthResponse(String token, String tipo, String mensagem) {
        this.token = token;
        this.tipo = tipo;
        this.mensagem = mensagem;
    }

    public AuthResponse(String mensagem) {
        this.token = null;
        this.tipo = null;
        this.mensagem = mensagem;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}

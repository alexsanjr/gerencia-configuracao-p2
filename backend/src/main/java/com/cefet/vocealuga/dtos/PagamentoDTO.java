package com.cefet.vocealuga.dtos;

public class PagamentoDTO {
    private String metodo;
    private String cardNumber;
    private String cardExpiry;
    private String cardCVV;
    private String cardName;

    // Getters e setters
    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardExpiry() { return cardExpiry; }
    public void setCardExpiry(String cardExpiry) { this.cardExpiry = cardExpiry; }

    public String getCardCVV() { return cardCVV; }
    public void setCardCVV(String cardCVV) { this.cardCVV = cardCVV; }

    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }
}

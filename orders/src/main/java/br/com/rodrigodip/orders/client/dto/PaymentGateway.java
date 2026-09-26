package br.com.rodrigodip.orders.client.dto;

public record PaymentGateway(
        Long id,
        String paymentKey,
        boolean status,
        String notes) {

}

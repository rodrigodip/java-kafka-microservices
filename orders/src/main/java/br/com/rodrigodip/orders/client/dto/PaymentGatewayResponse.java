package br.com.rodrigodip.orders.client.dto;

public record PaymentGatewayResponse(
                Long id,
                String paymentKey,
                boolean status,
                String notes) {

}

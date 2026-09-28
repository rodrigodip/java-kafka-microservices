package br.com.rodrigodip.orders.dto;

import br.com.rodrigodip.orders.enums.PaymentMode;

public record PaymentResponse(
        PaymentMode paymentMode) {
}

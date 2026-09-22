package br.com.rodrigodip.orders.dto;

import br.com.rodrigodip.orders.enums.PaymentMode;

public record PaymentRequest(
        String data,
        PaymentMode paymentData) {

}

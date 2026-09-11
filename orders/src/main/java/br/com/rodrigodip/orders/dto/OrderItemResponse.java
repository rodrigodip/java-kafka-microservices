package br.com.rodrigodip.orders.dto;

import br.com.rodrigodip.orders.enums.PaymentMode;

public record OrderItemResponse(
        String data, PaymentMode payment) {

}

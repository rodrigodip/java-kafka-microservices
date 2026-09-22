package br.com.rodrigodip.orders.entity;

import br.com.rodrigodip.orders.enums.PaymentMode;
import lombok.Getter;

@Getter
public class PaymentData {
    private String data;
    private PaymentMode paymentMode;
}

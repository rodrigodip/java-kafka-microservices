package br.com.rodrigodip.orders.exceptions;

public class PaymentCallbackUnauthorizedException extends RuntimeException {

    public PaymentCallbackUnauthorizedException() {
        super("Invalid PSP callback credentials.");
    }
}

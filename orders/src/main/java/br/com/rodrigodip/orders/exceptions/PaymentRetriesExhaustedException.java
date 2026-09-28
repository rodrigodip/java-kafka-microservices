package br.com.rodrigodip.orders.exceptions;

public class PaymentRetriesExhaustedException extends RuntimeException {

    public PaymentRetriesExhaustedException(Long orderId, int maxAttempts) {
        super("Payment retry limit exhausted for order [" + orderId + "]: maximum of [" + maxAttempts
                + "] attempts reached. Order has been cancelled.");
    }
}

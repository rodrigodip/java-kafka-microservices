package br.com.rodrigodip.orders.exceptions;

import br.com.rodrigodip.orders.enums.OrderStatus;

public class InvalidOrderStatusException extends RuntimeException {

    public InvalidOrderStatusException(Long orderId, OrderStatus actual, OrderStatus expected) {
        super("Order [" + orderId + "] has status [" + actual + "] but [" + expected + "] was expected.");
    }
}

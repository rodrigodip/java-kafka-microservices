package br.com.rodrigodip.orders.exceptions;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super("No order found matching the provided ID [" + id + "]");
    }

}

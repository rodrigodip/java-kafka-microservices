package br.com.rodrigodip.orders.exceptions;

public class ProductNotFoundOnClientException extends RuntimeException {

    public ProductNotFoundOnClientException(Long id) {
        super("No product found on Product-Service matching the provided ID [" + id + "]");
    }
}

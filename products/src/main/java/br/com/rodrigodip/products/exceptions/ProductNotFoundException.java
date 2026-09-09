package br.com.rodrigodip.products.exceptions;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("No product found matching the provided ID [" + id + "]");
    }

}

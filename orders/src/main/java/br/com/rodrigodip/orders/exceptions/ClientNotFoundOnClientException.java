package br.com.rodrigodip.orders.exceptions;

public class ClientNotFoundOnClientException extends RuntimeException {

    public ClientNotFoundOnClientException(Long id) {
        super("No client found on Client-Service matching the provided ID [" + id + "]");
    }
}

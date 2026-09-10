package br.com.rodrigodip.clients.exceptions;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(Long id) {
        super("No client found matching the provided ID [" + id + "]");
    }

}

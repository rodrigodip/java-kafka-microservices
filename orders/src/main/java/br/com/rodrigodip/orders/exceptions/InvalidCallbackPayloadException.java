package br.com.rodrigodip.orders.exceptions;

public class InvalidCallbackPayloadException extends RuntimeException {

    public InvalidCallbackPayloadException(String message, Throwable cause) {
        super(message, cause);
    }
}

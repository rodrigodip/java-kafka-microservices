package br.com.rodrigodip.orders.exceptions;

public class DownstreamUnavailableException extends RuntimeException {

    public DownstreamUnavailableException(String service, Throwable cause) {
        super("Service [" + service + "] is currently unavailable. Try again later.", cause);
    }
}

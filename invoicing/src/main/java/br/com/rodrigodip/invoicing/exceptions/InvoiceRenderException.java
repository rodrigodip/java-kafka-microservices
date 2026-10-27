package br.com.rodrigodip.invoicing.exceptions;

public class InvoiceRenderException extends RuntimeException {
    public InvoiceRenderException(String message, Throwable cause) {
        super(message, cause);
    }
}
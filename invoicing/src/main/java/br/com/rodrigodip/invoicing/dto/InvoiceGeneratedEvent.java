package br.com.rodrigodip.invoicing.dto;

public record InvoiceGeneratedEvent(
        Long orderId,
        String invoiceKey,
        String invoiceUrl) {
}

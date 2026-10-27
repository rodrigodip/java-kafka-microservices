package br.com.rodrigodip.orders.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InvoiceGeneratedEvent(
        Long orderId,
        String invoiceKey,
        String invoiceUrl) {
}

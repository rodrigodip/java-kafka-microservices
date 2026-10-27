package br.com.rodrigodip.logistics.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderInvoicedEvent(
        Long orderId,
        Long clientId,
        String clientName,
        String clientCpf,
        String clientEmail,
        String clientPhone,
        String clientAddress,
        String clientAddressNumber,
        String clientZipcode,
        BigDecimal total,
        String invoiceUrl,
        List<OrderItemEvent> itensList) {
}

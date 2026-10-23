package br.com.rodrigodip.invoicing.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InvoiceRepresentation(
        Long orderId,
        Long clientId,
        String clientName,
        String clientCpf,
        String clientEmail,
        String clientPhone,
        String clientAddress,
        String clientAddressNumber,
        String clientZipcode,
        String status,
        BigDecimal total,
        List<Item> itensList) {
    public record Item(Long productId, String name, int quantity, BigDecimal unitPrice) {
        public BigDecimal lineTotal() {
            return unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
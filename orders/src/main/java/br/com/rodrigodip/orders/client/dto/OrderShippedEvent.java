package br.com.rodrigodip.orders.client.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import br.com.rodrigodip.orders.enums.OrderStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderShippedEvent(
        Long orderId,
        Long clientId,
        String clientName,
        String clientCpf,
        String clientEmail,
        String clientPhone,
        String clientAddress,
        String clientAddressNumber,
        String clientZipcode,
        OrderStatus status,
        BigDecimal total,
        String trackingCode,
        List<OrderItemEvent> itensList) {
}

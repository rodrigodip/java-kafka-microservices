package br.com.rodrigodip.orders.client.dto;

import java.math.BigDecimal;
import java.util.List;

import br.com.rodrigodip.orders.enums.OrderStatus;

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
        OrderStatus status,
        BigDecimal total,
        List<OrderItemEvent> itensList) {
}

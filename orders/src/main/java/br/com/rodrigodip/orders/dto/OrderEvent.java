package br.com.rodrigodip.orders.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.rodrigodip.orders.enums.OrderStatus;

public record OrderEvent(
        Long orderId,
        Long clientId,
        OrderStatus status,
        BigDecimal total,
        String paymentKey,
        LocalDateTime occurredAt) {
}

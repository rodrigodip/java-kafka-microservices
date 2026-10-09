package br.com.rodrigodip.orders.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.rodrigodip.orders.entity.Order;
import br.com.rodrigodip.orders.enums.OrderStatus;

public record OrderEvent(
        Long orderId,
        Long clientId,
        OrderStatus status,
        BigDecimal total,
        String paymentKey,
        LocalDateTime occurredAt) {

    public static OrderEvent from(Order order) {
        return new OrderEvent(
                order.getId(),
                order.getClientId(),
                order.getStatus(),
                order.getTotal(),
                order.getPaymentKey(),
                LocalDateTime.now());
    }
}

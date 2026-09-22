package br.com.rodrigodip.orders.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import br.com.rodrigodip.orders.enums.OrderStatus;

public record OrderResponse(
                Long id,
                Long clientId,
                OrderStatus status,
                BigDecimal total,
                LocalDateTime orderDate,
                PaymentResponse paymentData,
                List<OrderItemResponse> itensList) {
}

package br.com.rodrigodip.orders.client.dto;

import java.math.BigDecimal;

public record OrderItemEvent(
                Long productId,
                String name,
                Integer quantity,
                BigDecimal unitPrice) {
}

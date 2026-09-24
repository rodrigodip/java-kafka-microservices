package br.com.rodrigodip.orders.client.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price) {
}
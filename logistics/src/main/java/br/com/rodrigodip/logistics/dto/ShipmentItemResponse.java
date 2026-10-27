package br.com.rodrigodip.logistics.dto;

import java.math.BigDecimal;

public record ShipmentItemResponse(
        Long productId,
        String name,
        Integer quantity,
        BigDecimal unitPrice) {
}

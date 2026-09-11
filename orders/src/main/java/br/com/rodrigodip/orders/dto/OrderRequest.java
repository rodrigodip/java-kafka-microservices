package br.com.rodrigodip.orders.dto;

import java.math.BigDecimal;

public record OrderRequest(
		Long productId,
		Integer quantity,
		BigDecimal unitPrice) {
}

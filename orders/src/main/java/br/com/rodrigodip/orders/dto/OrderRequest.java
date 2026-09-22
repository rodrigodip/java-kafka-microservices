package br.com.rodrigodip.orders.dto;

import java.util.List;

import br.com.rodrigodip.orders.entity.PaymentData;

public record OrderRequest(
		Long clientId,
		PaymentData paymentData,
		List<OrderItemRequest> itensList) {
}

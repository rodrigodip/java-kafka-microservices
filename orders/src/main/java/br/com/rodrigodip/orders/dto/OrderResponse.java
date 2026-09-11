package br.com.rodrigodip.orders.dto;

import java.util.List;

public record OrderResponse(
                Long clientId,
                PaymentResponse paymentData,
                List<OrderItemResponse> itensList) {
}
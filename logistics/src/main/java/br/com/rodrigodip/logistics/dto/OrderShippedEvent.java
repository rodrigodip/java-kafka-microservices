package br.com.rodrigodip.logistics.dto;

import java.math.BigDecimal;
import java.util.List;

import br.com.rodrigodip.logistics.enums.ShipmentStatus;

public record OrderShippedEvent(
        Long orderId,
        Long clientId,
        String clientName,
        String clientCpf,
        String clientEmail,
        String clientPhone,
        String clientAddress,
        String clientAddressNumber,
        String clientZipcode,
        ShipmentStatus status,
        BigDecimal total,
        String trackingCode,
        List<OrderItemEvent> itensList) {
}

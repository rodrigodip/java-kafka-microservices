package br.com.rodrigodip.logistics.service;

import java.util.List;

import br.com.rodrigodip.logistics.dto.OrderInvoicedEvent;
import br.com.rodrigodip.logistics.entity.Shipment;
import br.com.rodrigodip.logistics.enums.ShipmentStatus;

public interface ShipmentService {

    List<Shipment> findPending();

    List<Shipment> findByStatus(ShipmentStatus status);

    Shipment findByOrderId(Long orderId);

    void registerInvoicedOrder(OrderInvoicedEvent event);

    Shipment ship(Long orderId, String trackingCode);
}

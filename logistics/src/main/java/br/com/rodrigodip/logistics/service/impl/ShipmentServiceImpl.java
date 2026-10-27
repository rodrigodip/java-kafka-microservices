package br.com.rodrigodip.logistics.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.rodrigodip.logistics.dto.OrderInvoicedEvent;
import br.com.rodrigodip.logistics.dto.mapper.ShipmentMapper;
import br.com.rodrigodip.logistics.entity.Shipment;
import br.com.rodrigodip.logistics.enums.ShipmentStatus;
import br.com.rodrigodip.logistics.exceptions.ShipmentNotFoundException;
import br.com.rodrigodip.logistics.repository.ShipmentRepository;
import br.com.rodrigodip.logistics.service.ShipmentEventPublisher;
import br.com.rodrigodip.logistics.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipmentServiceImpl implements ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentMapper shipmentMapper;
    private final ShipmentEventPublisher shipmentEventPublisher;

    @Override
    @Transactional(readOnly = true)
    public List<Shipment> findPending() {
        return shipmentRepository.findByStatusOrderByCreatedAtAsc(ShipmentStatus.PENDING);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Shipment> findByStatus(ShipmentStatus status) {
        return shipmentRepository.findByStatusOrderByCreatedAtAsc(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Shipment findByOrderId(Long orderId) {
        return shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ShipmentNotFoundException(orderId));
    }

    @Override
    @Transactional
    public void registerInvoicedOrder(OrderInvoicedEvent event) {
        if (shipmentRepository.existsByOrderId(event.orderId())) {
            log.info("Shipment for order {} already exists; ignoring duplicate order.invoiced", event.orderId());
            return;
        }

        Shipment shipment = shipmentMapper.toEntity(event);
        shipment.setStatus(ShipmentStatus.PENDING);
        shipmentRepository.save(shipment);

        log.info("Shipment created for order {} with {} item(s)", event.orderId(),
                shipment.getItensList() == null ? 0 : shipment.getItensList().size());
    }

    @Override
    @Transactional
    public Shipment ship(Long orderId, String trackingCode) {
        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ShipmentNotFoundException(orderId));

        shipment.markShipped(trackingCode.trim());
        Shipment saved = shipmentRepository.save(shipment);

        shipmentEventPublisher.publishShipped(saved);

        log.info("Shipment for order {} marked as SHIPPED with tracking code {}", orderId,
                saved.getTrackingCode());

        return saved;
    }
}

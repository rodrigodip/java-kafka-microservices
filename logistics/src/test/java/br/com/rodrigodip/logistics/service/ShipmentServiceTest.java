package br.com.rodrigodip.logistics.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.rodrigodip.logistics.dto.OrderInvoicedEvent;
import br.com.rodrigodip.logistics.dto.OrderItemEvent;
import br.com.rodrigodip.logistics.dto.mapper.ShipmentMapper;
import br.com.rodrigodip.logistics.entity.Shipment;
import br.com.rodrigodip.logistics.entity.ShipmentItem;
import br.com.rodrigodip.logistics.enums.ShipmentStatus;
import br.com.rodrigodip.logistics.exceptions.ShipmentNotFoundException;
import br.com.rodrigodip.logistics.repository.ShipmentRepository;
import br.com.rodrigodip.logistics.service.impl.ShipmentServiceImpl;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private ShipmentMapper shipmentMapper;

    @Mock
    private ShipmentEventPublisher shipmentEventPublisher;

    @InjectMocks
    private ShipmentServiceImpl shipmentService;

    @Test
    void registersInvoicedOrderAsPendingShipment() {
        OrderInvoicedEvent event = invoicedEvent();
        Shipment shipment = pendingShipment();
        when(shipmentRepository.existsByOrderId(42L)).thenReturn(false);
        when(shipmentMapper.toEntity(event)).thenReturn(shipment);
        when(shipmentRepository.save(shipment)).thenReturn(shipment);

        shipmentService.registerInvoicedOrder(event);

        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.PENDING);
        verify(shipmentRepository).save(shipment);
    }

    @Test
    void ignoresDuplicateInvoicedOrder() {
        OrderInvoicedEvent event = invoicedEvent();
        when(shipmentRepository.existsByOrderId(42L)).thenReturn(true);

        shipmentService.registerInvoicedOrder(event);

        verify(shipmentMapper, never()).toEntity(any());
        verify(shipmentRepository, never()).save(any());
    }

    @Test
    void listsPendingShipments() {
        Shipment shipment = pendingShipment();
        when(shipmentRepository.findByStatusOrderByCreatedAtAsc(ShipmentStatus.PENDING))
                .thenReturn(List.of(shipment));

        List<Shipment> result = shipmentService.findPending();

        assertThat(result).containsExactly(shipment);
    }

    @Test
    void shipsPendingShipmentAndPublishesEvent() {
        Shipment shipment = pendingShipment();
        when(shipmentRepository.findByOrderId(42L)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(shipment)).thenReturn(shipment);

        Shipment result = shipmentService.ship(42L, " BR123456789 ");

        assertThat(result.getStatus()).isEqualTo(ShipmentStatus.SHIPPED);
        assertThat(result.getTrackingCode()).isEqualTo("BR123456789");
        assertThat(result.getShippedAt()).isNotNull();
        verify(shipmentEventPublisher).publishShipped(shipment);
    }

    @Test
    void updatesTrackingCodeOfAlreadyShippedOrder() {
        Shipment shipment = pendingShipment();
        shipment.markShipped("OLD-CODE");
        when(shipmentRepository.findByOrderId(42L)).thenReturn(Optional.of(shipment));
        when(shipmentRepository.save(shipment)).thenReturn(shipment);

        Shipment result = shipmentService.ship(42L, "NEW-CODE");

        assertThat(result.getStatus()).isEqualTo(ShipmentStatus.SHIPPED);
        assertThat(result.getTrackingCode()).isEqualTo("NEW-CODE");
        verify(shipmentEventPublisher).publishShipped(shipment);
    }

    @Test
    void rejectsUnknownShipment() {
        when(shipmentRepository.findByOrderId(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shipmentService.ship(42L, "BR123"))
                .isInstanceOf(ShipmentNotFoundException.class);
    }

    private Shipment pendingShipment() {
        Shipment shipment = new Shipment();
        shipment.setOrderId(42L);
        shipment.setClientId(1L);
        shipment.setStatus(ShipmentStatus.PENDING);
        shipment.setTotal(new BigDecimal("100.00"));

        ShipmentItem item = new ShipmentItem();
        item.setProductId(1L);
        item.setName("Product");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("50.00"));
        shipment.addItem(item);

        return shipment;
    }

    private OrderInvoicedEvent invoicedEvent() {
        return new OrderInvoicedEvent(
                42L,
                1L,
                "Client",
                "12345678901",
                "client@example.com",
                "11999999999",
                "Street",
                "100",
                "01001000",
                new BigDecimal("100.00"),
                "http://invoicing/invoices/42/download",
                List.of(new OrderItemEvent(1L, "Product", 2, new BigDecimal("50.00"))));
    }
}

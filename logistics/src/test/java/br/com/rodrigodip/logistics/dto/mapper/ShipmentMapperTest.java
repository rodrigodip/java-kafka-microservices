package br.com.rodrigodip.logistics.dto.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import br.com.rodrigodip.logistics.dto.OrderInvoicedEvent;
import br.com.rodrigodip.logistics.dto.OrderItemEvent;
import br.com.rodrigodip.logistics.dto.OrderShippedEvent;
import br.com.rodrigodip.logistics.entity.Shipment;
import br.com.rodrigodip.logistics.enums.ShipmentStatus;

class ShipmentMapperTest {

    private final ShipmentMapper mapper = Mappers.getMapper(ShipmentMapper.class);

    @Test
    void mapsInvoicedEventToShipmentSnapshot() {
        Shipment shipment = mapper.toEntity(invoicedEvent());

        assertThat(shipment.getOrderId()).isEqualTo(42L);
        assertThat(shipment.getClientId()).isEqualTo(1L);
        assertThat(shipment.getClientName()).isEqualTo("Client");
        assertThat(shipment.getClientAddressNumber()).isEqualTo("100");
        assertThat(shipment.getTotal()).isEqualByComparingTo("100.00");
        assertThat(shipment.getInvoiceUrl()).isEqualTo("http://invoicing/invoices/42/download");
        assertThat(shipment.getItensList()).hasSize(1);
        assertThat(shipment.getItensList().get(0).getShipment()).isSameAs(shipment);
        assertThat(shipment.getItensList().get(0).getName()).isEqualTo("Product");
    }

    @Test
    void mapsShipmentToShippedEvent() {
        Shipment shipment = mapper.toEntity(invoicedEvent());
        shipment.setStatus(ShipmentStatus.SHIPPED);
        shipment.markShipped("BR123456789");

        OrderShippedEvent event = mapper.toShippedEvent(shipment);

        assertThat(event.orderId()).isEqualTo(42L);
        assertThat(event.status()).isEqualTo(ShipmentStatus.SHIPPED);
        assertThat(event.trackingCode()).isEqualTo("BR123456789");
        assertThat(event.itensList()).hasSize(1);
        assertThat(event.itensList().get(0).productId()).isEqualTo(1L);
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

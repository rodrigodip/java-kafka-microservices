package br.com.rodrigodip.orders.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.rodrigodip.orders.entity.Order;
import br.com.rodrigodip.orders.enums.OrderStatus;
import br.com.rodrigodip.orders.exceptions.InvalidOrderStatusException;
import br.com.rodrigodip.orders.exceptions.OrderNotFoundException;
import br.com.rodrigodip.orders.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderShippingServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderShippingService orderShippingService;

    @Test
    void marksInvoicedOrderAsShippedAndStoresTrackingCode() {
        Order order = order(OrderStatus.INVOICED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        Optional<Order> result = orderShippingService.markAsShipped(1L, "BR123456789");

        assertThat(result).contains(order);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(order.getTrackingCode()).isEqualTo("BR123456789");
        verify(orderRepository).save(order);
    }

    @Test
    void acceptsOrderPreparingShipment() {
        Order order = order(OrderStatus.PREPARING_SHIPMENT);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        orderShippingService.markAsShipped(1L, "BR123456789");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void ignoresShippedOrderWithSameTrackingCode() {
        Order order = order(OrderStatus.SHIPPED);
        order.setTrackingCode("BR123456789");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Optional<Order> result = orderShippingService.markAsShipped(1L, "BR123456789");

        assertThat(result).isEmpty();
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updatesTrackingCodeOfShippedOrder() {
        Order order = order(OrderStatus.SHIPPED);
        order.setTrackingCode("OLD-CODE");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        Optional<Order> result = orderShippingService.markAsShipped(1L, "NEW-CODE");

        assertThat(result).contains(order);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        assertThat(order.getTrackingCode()).isEqualTo("NEW-CODE");
        verify(orderRepository).save(order);
    }

    @Test
    void rejectsOrderThatWasNotInvoiced() {
        Order order = order(OrderStatus.PAID);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderShippingService.markAsShipped(1L, "BR123"))
                .isInstanceOf(InvalidOrderStatusException.class);
    }

    @Test
    void rejectsUnknownOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderShippingService.markAsShipped(1L, "BR123"))
                .isInstanceOf(OrderNotFoundException.class);
    }

    private Order order(OrderStatus status) {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(status);
        return order;
    }
}

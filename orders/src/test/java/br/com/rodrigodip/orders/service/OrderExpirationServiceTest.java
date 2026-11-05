package br.com.rodrigodip.orders.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.rodrigodip.orders.client.PaymentProperties;
import br.com.rodrigodip.orders.entity.Order;
import br.com.rodrigodip.orders.enums.OrderStatus;
import br.com.rodrigodip.orders.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderExpirationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentProperties paymentProperties;

    @InjectMocks
    private OrderExpirationService orderExpirationService;

    @Test
    void expiresPlacedOrdersOlderThanTtl() {
        when(paymentProperties.getPlacedTtl()).thenReturn(Duration.ofMinutes(5));

        Order oldOrder = placedOrder(1L, LocalDateTime.now().minusMinutes(6));
        when(orderRepository.findByStatusAndPlacedAtBefore(eq(OrderStatus.PLACED), any(LocalDateTime.class)))
                .thenReturn(List.of(oldOrder));

        orderExpirationService.expireStalePlacedOrders();

        assertThat(oldOrder.getStatus()).isEqualTo(OrderStatus.EXPIRED);
        verify(orderRepository).save(oldOrder);
    }

    @Test
    void doesNothingWhenNoStaleOrdersExist() {
        when(paymentProperties.getPlacedTtl()).thenReturn(Duration.ofMinutes(5));
        when(orderRepository.findByStatusAndPlacedAtBefore(eq(OrderStatus.PLACED), any(LocalDateTime.class)))
                .thenReturn(List.of());

        orderExpirationService.expireStalePlacedOrders();

        ArgumentCaptor<LocalDateTime> cutoffCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(orderRepository).findByStatusAndPlacedAtBefore(eq(OrderStatus.PLACED), cutoffCaptor.capture());
        LocalDateTime captured = cutoffCaptor.getValue();
        assertThat(captured).isAfter(LocalDateTime.now().minusMinutes(5).minusSeconds(5));
        assertThat(captured).isBefore(LocalDateTime.now().minusMinutes(5).plusSeconds(5));
    }

    @Test
    void usesConfiguredTtlToCalculateCutoff() {
        when(paymentProperties.getPlacedTtl()).thenReturn(Duration.ofHours(2));

        orderExpirationService.expireStalePlacedOrders();

        ArgumentCaptor<LocalDateTime> cutoffCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(orderRepository).findByStatusAndPlacedAtBefore(eq(OrderStatus.PLACED), cutoffCaptor.capture());

        LocalDateTime captured = cutoffCaptor.getValue();
        assertThat(captured).isAfter(LocalDateTime.now().minusHours(2).minusSeconds(5));
        assertThat(captured).isBefore(LocalDateTime.now().minusHours(2).plusSeconds(5));
    }

    private Order placedOrder(Long id, LocalDateTime placedAt) {
        Order order = new Order();
        order.setId(id);
        order.setStatus(OrderStatus.PLACED);
        order.setPlacedAt(placedAt);
        return order;
    }
}

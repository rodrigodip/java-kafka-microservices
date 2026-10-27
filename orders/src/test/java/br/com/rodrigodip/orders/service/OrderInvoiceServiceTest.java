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
class OrderInvoiceServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderInvoiceService orderInvoiceService;

    @Test
    void marksPaidOrderAsInvoicedAndStoresUrl() {
        Order order = order(OrderStatus.PAID);
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        Optional<Order> result = orderInvoiceService.markAsInvoiced(1L, "http://invoicing/invoices/1/download");

        assertThat(result).contains(order);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.INVOICED);
        assertThat(order.getInvoiceUrl()).isEqualTo("http://invoicing/invoices/1/download");
        verify(orderRepository).save(order);
    }

    @Test
    void returnsAlreadyInvoicedOrderForIdempotentRepublish() {
        Order order = order(OrderStatus.INVOICED);
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));

        Optional<Order> result = orderInvoiceService.markAsInvoiced(1L, "http://invoicing/invoices/1/download");

        assertThat(result).contains(order);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void ignoresOrderThatAlreadyMovedPastInvoicing() {
        Order order = order(OrderStatus.SHIPPED);
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));

        Optional<Order> result = orderInvoiceService.markAsInvoiced(1L, "http://invoicing/invoices/1/download");

        assertThat(result).isEmpty();
        verify(orderRepository, never()).save(any());
    }

    @Test
    void rejectsOrderThatWasNotPaid() {
        Order order = order(OrderStatus.PLACED);
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderInvoiceService.markAsInvoiced(1L, "url"))
                .isInstanceOf(InvalidOrderStatusException.class);
    }

    @Test
    void rejectsUnknownOrder() {
        when(orderRepository.findByIdWithItems(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderInvoiceService.markAsInvoiced(1L, "url"))
                .isInstanceOf(OrderNotFoundException.class);
    }

    private Order order(OrderStatus status) {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(status);
        return order;
    }
}

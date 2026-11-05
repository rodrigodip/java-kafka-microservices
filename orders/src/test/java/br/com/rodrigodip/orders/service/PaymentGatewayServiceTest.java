package br.com.rodrigodip.orders.service;

import static org.assertj.core.api.Assertions.assertThat;
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

import br.com.rodrigodip.orders.client.PaymentProperties;
import br.com.rodrigodip.orders.client.dto.PaymentGatewayResponse;
import br.com.rodrigodip.orders.entity.Order;
import br.com.rodrigodip.orders.enums.OrderStatus;
import br.com.rodrigodip.orders.exceptions.OrderNotFoundException;
import br.com.rodrigodip.orders.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentProperties paymentProperties;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @InjectMocks
    private PaymentGatewayService paymentGatewayService;

    @Test
    void marksOrderAsPaidOnSuccessfulCallback() {
        Order order = order(OrderStatus.PLACED, "key-1", 1);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        paymentGatewayService.updatePaymentStatus(callback(1L, "key-1", true, "approved"));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(order.getNotes()).isEqualTo("approved");
        verify(orderEventPublisher).publishPaid(order);
    }

    @Test
    void marksOrderAsPaymentFailedWhenBelowMaxAttempts() {
        Order order = order(OrderStatus.PLACED, "key-1", 1);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentProperties.getMaxAttempts()).thenReturn(3);
        when(orderRepository.save(order)).thenReturn(order);

        paymentGatewayService.updatePaymentStatus(callback(1L, "key-1", false, "denied"));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_FAILED);
        assertThat(order.getNotes()).isEqualTo("denied");
        verify(orderEventPublisher, never()).publishPaid(any());
    }

    @Test
    void marksOrderAsCancelledWhenMaxAttemptsReached() {
        Order order = order(OrderStatus.PAYMENT_FAILED, "key-1", 3);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentProperties.getMaxAttempts()).thenReturn(3);
        when(orderRepository.save(order)).thenReturn(order);

        paymentGatewayService.updatePaymentStatus(callback(1L, "key-1", false, "denied again"));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderEventPublisher, never()).publishPaid(any());
    }

    @Test
    void ignoresStalePaymentKeyAsNoOp() {
        Order order = order(OrderStatus.PLACED, "key-2", 1);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        paymentGatewayService.updatePaymentStatus(callback(1L, "key-1", true, "late callback"));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PLACED);
        verify(orderRepository, never()).save(any());
        verify(orderEventPublisher, never()).publishPaid(any());
    }

    @Test
    void ignoresCallbackForTerminalStatus() {
        Order order = order(OrderStatus.SHIPPED, "key-1", 1);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        paymentGatewayService.updatePaymentStatus(callback(1L, "key-1", false, "too late"));

        assertThat(order.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        verify(orderRepository, never()).save(any());
        verify(orderEventPublisher, never()).publishPaid(any());
    }

    @Test
    void throwsOrderNotFoundExceptionForUnknownOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        org.assertj.core.api.Assertions.assertThatThrownBy(
                () -> paymentGatewayService.updatePaymentStatus(callback(1L, "key-1", true, "")))
                .isInstanceOf(OrderNotFoundException.class);
    }

    private Order order(OrderStatus status, String paymentKey, int paymentAttempts) {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(status);
        order.setPaymentKey(paymentKey);
        order.setPaymentAttempts(paymentAttempts);
        return order;
    }

    private PaymentGatewayResponse callback(Long id, String paymentKey, boolean status, String notes) {
        return new PaymentGatewayResponse(id, paymentKey, status, notes);
    }
}

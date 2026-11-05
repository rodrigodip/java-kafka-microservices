package br.com.rodrigodip.orders.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.rodrigodip.orders.client.PaymentGatewayClient;
import br.com.rodrigodip.orders.client.PaymentProperties;
import br.com.rodrigodip.orders.entity.Order;
import br.com.rodrigodip.orders.entity.PaymentData;
import br.com.rodrigodip.orders.enums.OrderStatus;
import br.com.rodrigodip.orders.exceptions.InvalidOrderStatusException;
import br.com.rodrigodip.orders.exceptions.OrderNotFoundException;
import br.com.rodrigodip.orders.exceptions.PaymentRetriesExhaustedException;
import br.com.rodrigodip.orders.repository.OrderRepository;
import br.com.rodrigodip.orders.validator.OrderValidator;

@ExtendWith(MockitoExtension.class)
class OrderServicePaymentRetryTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentGatewayClient pspClient;

    @Mock
    private OrderValidator orderValidator;

    @Mock
    private PaymentProperties paymentProperties;

    @InjectMocks
    private OrderService orderService;

    private final PaymentData paymentData = new PaymentData();

    @ParameterizedTest
    @MethodSource("retryableStatuses")
    void allowsRetryFromRetryableStatus(OrderStatus initialStatus) {
        Order order = order(initialStatus, 1);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentProperties.getMaxAttempts()).thenReturn(3);
        when(pspClient.processPayment(order)).thenReturn("new-payment-key");
        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.PaymentRetry(1L, paymentData);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.PLACED);
        assertThat(result.getPaymentAttempts()).isEqualTo(2);
        assertThat(result.getPaymentKey()).isEqualTo("new-payment-key");
        assertThat(result.getNotes()).isNull();
        verify(pspClient).processPayment(order);
        verify(orderRepository).save(order);
    }

    static Stream<OrderStatus> retryableStatuses() {
        return Stream.of(OrderStatus.PLACED, OrderStatus.PAYMENT_FAILED, OrderStatus.EXPIRED);
    }

    @ParameterizedTest
    @MethodSource("nonRetryableStatuses")
    void rejectsRetryFromNonRetryableStatus(OrderStatus initialStatus) {
        Order order = order(initialStatus, 1);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.PaymentRetry(1L, paymentData))
                .isInstanceOf(InvalidOrderStatusException.class)
                .hasMessageContaining("must be PLACED, PAYMENT_FAILED or EXPIRED");

        verify(pspClient, never()).processPayment(any());
    }

    static Stream<OrderStatus> nonRetryableStatuses() {
        return Stream.of(OrderStatus.PAID, OrderStatus.INVOICED, OrderStatus.PREPARING_SHIPMENT,
                OrderStatus.SHIPPED, OrderStatus.CANCELLED);
    }

    @Test
    void cancelsOrderWhenMaxAttemptsReached() {
        Order order = order(OrderStatus.PAYMENT_FAILED, 3);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentProperties.getMaxAttempts()).thenReturn(3);

        assertThatThrownBy(() -> orderService.PaymentRetry(1L, paymentData))
                .isInstanceOf(PaymentRetriesExhaustedException.class);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository).save(order);
        verify(pspClient, never()).processPayment(any());
    }

    @Test
    void throwsOrderNotFoundExceptionForUnknownOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.PaymentRetry(1L, paymentData))
                .isInstanceOf(OrderNotFoundException.class);
    }

    private Order order(OrderStatus status, int paymentAttempts) {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(status);
        order.setPaymentAttempts(paymentAttempts);
        return order;
    }
}

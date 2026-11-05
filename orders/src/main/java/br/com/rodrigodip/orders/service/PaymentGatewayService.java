package br.com.rodrigodip.orders.service;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.rodrigodip.orders.client.PaymentProperties;
import br.com.rodrigodip.orders.client.dto.PaymentGatewayResponse;
import br.com.rodrigodip.orders.entity.Order;
import br.com.rodrigodip.orders.enums.OrderStatus;
import br.com.rodrigodip.orders.exceptions.OrderNotFoundException;
import br.com.rodrigodip.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentGatewayService {

    private final OrderRepository orderRepository;
    private final PaymentProperties paymentProperties;
    private final OrderEventPublisher orderEventPublisher;

    private static final Set<OrderStatus> TERMINAL_STATUSES = EnumSet.of(
            OrderStatus.PAID,
            OrderStatus.INVOICED,
            OrderStatus.PREPARING_SHIPMENT,
            OrderStatus.SHIPPED,
            OrderStatus.CANCELLED);

    @Transactional
    public Void updatePaymentStatus(PaymentGatewayResponse paymentGateway) {
        Order order = orderRepository.findById(paymentGateway.id())
                .orElseThrow(() -> new OrderNotFoundException(paymentGateway.id()));

        // Idempotency: a callback with a stale/unknown payment key (e.g. a late
        // callback for a previous attempt) must not 404 — it is simply ignored.
        if (!Objects.equals(order.getPaymentKey(), paymentGateway.paymentKey())) {
            log.info("Ignoring PSP callback for order {} with stale/unknown payment key", paymentGateway.id());
            return null;
        }

        if (TERMINAL_STATUSES.contains(order.getStatus())) {
            return null;
        }

        if (paymentGateway.status()) {
            order.setStatus(OrderStatus.PAID);
        } else if (order.getPaymentAttempts() >= paymentProperties.getMaxAttempts()) {
            order.setStatus(OrderStatus.CANCELLED);
        } else {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
        }
        order.setNotes(paymentGateway.notes());

        orderRepository.save(order);

        if (order.getStatus() == OrderStatus.PAID) {
            orderEventPublisher.publishPaid(order);
        }

        return null;
    }
}

package br.com.rodrigodip.orders.service;

import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Set;

import br.com.rodrigodip.orders.client.PaymentProperties;
import br.com.rodrigodip.orders.client.dto.PaymentGateway;
import br.com.rodrigodip.orders.enums.OrderStatus;
import br.com.rodrigodip.orders.exceptions.OrderNotFoundException;
import br.com.rodrigodip.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

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

    public Void updatePaymentStatus(PaymentGateway paymentGateway) {

        var foundOrder = orderRepository.findByIdAndPaymentKey(
                paymentGateway.id(),
                paymentGateway.paymentKey())
                .orElseThrow(() -> new OrderNotFoundException(paymentGateway.id()));

        if (TERMINAL_STATUSES.contains(foundOrder.getStatus())) {
            return null;
        }

        if (paymentGateway.status()) {
            foundOrder.setStatus(OrderStatus.PAID);
        } else if (foundOrder.getPaymentAttempts() >= paymentProperties.getMaxAttempts()) {
            foundOrder.setStatus(OrderStatus.CANCELLED);
        } else {
            foundOrder.setStatus(OrderStatus.PAYMENT_FAILED);
        }
        foundOrder.setNotes(paymentGateway.notes());

        orderRepository.save(foundOrder);

        if (foundOrder.getStatus() == OrderStatus.PAID) {
            orderEventPublisher.publishPaid(foundOrder);
        }

        return null;
    }
}

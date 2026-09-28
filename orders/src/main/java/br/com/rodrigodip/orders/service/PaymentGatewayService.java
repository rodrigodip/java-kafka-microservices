package br.com.rodrigodip.orders.service;

import org.springframework.stereotype.Service;

import br.com.rodrigodip.orders.client.dto.PaymentGateway;
import br.com.rodrigodip.orders.enums.OrderStatus;
import br.com.rodrigodip.orders.exceptions.OrderNotFoundException;
import br.com.rodrigodip.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentGatewayService {

    private final OrderRepository orderRepository;

    public Void updatePaymentStatus(PaymentGateway paymentGateway) {

        var foundOrder = orderRepository.findByIdAndPaymentKey(
                paymentGateway.id(),
                paymentGateway.paymentKey())
                .orElseThrow(() -> new OrderNotFoundException(paymentGateway.id()));

        if (paymentGateway.status()) {
            foundOrder.setStatus(OrderStatus.PAID);
        } else {
            foundOrder.setStatus(OrderStatus.PAYMENT_FAILED);
        }
        foundOrder.setNotes(paymentGateway.notes());

        orderRepository.save(foundOrder);
        return null;
    }
}

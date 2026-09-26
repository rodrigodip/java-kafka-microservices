package br.com.rodrigodip.orders.service;

import org.springframework.stereotype.Service;

import br.com.rodrigodip.orders.enums.OrderStatus;
import br.com.rodrigodip.orders.exceptions.OrderNotFoundException;
import br.com.rodrigodip.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentGatewayService {

    private final OrderRepository orderRepository;

    public Void updatePaymentStatus(long id, String paymentKey, boolean success, String notes) {

        var foundOrder = orderRepository.findByIdAndPaymentKey(id, paymentKey)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (success) {
            foundOrder.setStatus(OrderStatus.PAID);
            foundOrder.setNotes(notes);
        } else {
            foundOrder.setStatus(OrderStatus.PAYMENT_FAILED);
        }

        orderRepository.save(foundOrder);
        return null;
    }
}

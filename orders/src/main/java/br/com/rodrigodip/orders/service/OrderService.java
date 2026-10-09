package br.com.rodrigodip.orders.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentGatewayClient pspClient;
    private final OrderValidator orderValidator;
    private final PaymentProperties paymentProperties;
    private final OrderEventPublisher orderEventPublisher;

    @Transactional
    public Order saveOrder(Order order) {
        orderValidator.validate(order);

        order.place();
        order.setPaymentAttempts(1);
        var paymentKey = pspClient.processPayment(order);
        order.setPaymentKey(paymentKey);

        var savedOrder = orderRepository.save(order);
        orderEventPublisher.publishPlaced(savedOrder);

        return savedOrder;
    }

    public List<Order> findAll() {

        List<Order> ordersList = orderRepository.findAll();
        return ordersList;
    }

    public Order findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        return order;
    }

    @Transactional(noRollbackFor = PaymentRetriesExhaustedException.class)
    public Order PaymentRetry(Long id, PaymentData paymentData) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        if (order.getStatus() != OrderStatus.PAYMENT_FAILED) {
            throw new InvalidOrderStatusException(id, order.getStatus(), OrderStatus.PAYMENT_FAILED);
        }

        if (order.getPaymentAttempts() >= paymentProperties.getMaxAttempts()) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            throw new PaymentRetriesExhaustedException(id, paymentProperties.getMaxAttempts());
        }

        order.setPaymentData(paymentData);
        order.setStatus(OrderStatus.PLACED);
        order.setNotes(null);
        order.setPaymentAttempts(order.getPaymentAttempts() + 1);

        var paymentKey = pspClient.processPayment(order);
        order.setPaymentKey(paymentKey);

        var savedOrder = orderRepository.save(order);
        orderEventPublisher.publishPlaced(savedOrder);

        return savedOrder;
    }
}

package br.com.rodrigodip.orders.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.rodrigodip.orders.entity.Order;
import br.com.rodrigodip.orders.entity.PaymentGatewayClient;
import br.com.rodrigodip.orders.exceptions.OrderNotFoundException;
import br.com.rodrigodip.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentGatewayClient pspClient;

    @Transactional
    public Order saveOrder(Order order) {

        order.place();
        var paymentKey = pspClient.processPayment(order);
        order.setPaymentKey(paymentKey);

        return orderRepository.save(order);
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
}
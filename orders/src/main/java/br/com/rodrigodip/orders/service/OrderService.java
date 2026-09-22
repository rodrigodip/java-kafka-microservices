package br.com.rodrigodip.orders.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.rodrigodip.orders.entity.Order;
import br.com.rodrigodip.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Order saveOrder(Order order) {

        order.place();

        return orderRepository.save(order);
    }

}
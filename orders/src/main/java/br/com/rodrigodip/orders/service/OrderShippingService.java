package br.com.rodrigodip.orders.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.rodrigodip.orders.entity.Order;
import br.com.rodrigodip.orders.enums.OrderStatus;
import br.com.rodrigodip.orders.exceptions.InvalidOrderStatusException;
import br.com.rodrigodip.orders.exceptions.OrderNotFoundException;
import br.com.rodrigodip.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderShippingService {

    private final OrderRepository orderRepository;

    @Transactional
    public Optional<Order> markAsShipped(Long orderId, String trackingCode) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.SHIPPED) {
            if (Objects.equals(order.getTrackingCode(), trackingCode)) {
                log.info("Order {} is already shipped with tracking code {}", orderId, trackingCode);
                return Optional.empty();
            }

            order.setTrackingCode(trackingCode);
            log.info("Tracking code for shipped order {} updated to {}", orderId, trackingCode);

            return Optional.of(orderRepository.save(order));
        }

        if (order.getStatus() != OrderStatus.INVOICED
                && order.getStatus() != OrderStatus.PREPARING_SHIPMENT) {
            throw new InvalidOrderStatusException(orderId, order.getStatus(), OrderStatus.INVOICED);
        }

        order.setStatus(OrderStatus.SHIPPED);
        order.setTrackingCode(trackingCode);

        log.info("Order {} marked as SHIPPED with tracking code {}", orderId, trackingCode);

        return Optional.of(orderRepository.save(order));
    }
}

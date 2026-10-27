package br.com.rodrigodip.orders.service;

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
public class OrderInvoiceService {

    private final OrderRepository orderRepository;

    @Transactional
    public Optional<Order> markAsInvoiced(Long orderId, String invoiceUrl) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        if (order.getStatus() == OrderStatus.PREPARING_SHIPMENT || order.getStatus() == OrderStatus.SHIPPED) {
            log.info("Order {} already moved past invoicing (status={})", orderId, order.getStatus());
            return Optional.empty();
        }

        if (order.getStatus() == OrderStatus.INVOICED) {
            log.info("Order {} is already invoiced; republishing order.invoiced", orderId);
            return Optional.of(order);
        }

        if (order.getStatus() != OrderStatus.PAID) {
            throw new InvalidOrderStatusException(orderId, order.getStatus(), OrderStatus.PAID);
        }

        order.setStatus(OrderStatus.INVOICED);
        order.setInvoiceUrl(invoiceUrl);

        log.info("Order {} marked as INVOICED", orderId);

        return Optional.of(orderRepository.save(order));
    }
}

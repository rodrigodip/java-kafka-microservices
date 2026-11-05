package br.com.rodrigodip.orders.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.rodrigodip.orders.client.PaymentProperties;
import br.com.rodrigodip.orders.entity.Order;
import br.com.rodrigodip.orders.enums.OrderStatus;
import br.com.rodrigodip.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Marks PLACED orders that never received a PSP callback within the configured
 * TTL as EXPIRED, so they do not stay as "zombie" orders forever (limitations
 * #4 and #5).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderExpirationService {

    private final OrderRepository orderRepository;
    private final PaymentProperties paymentProperties;

    @Scheduled(fixedDelayString = "${app.payment.expiration-interval-ms:60000}")
    @Transactional
    public void expireStalePlacedOrders() {
        LocalDateTime cutoff = LocalDateTime.now().minus(paymentProperties.getPlacedTtl());
        List<Order> staleOrders = orderRepository.findByStatusAndPlacedAtBefore(OrderStatus.PLACED, cutoff);

        for (Order order : staleOrders) {
            order.setStatus(OrderStatus.EXPIRED);
            orderRepository.save(order);
            log.info("Order {} expired: no PSP callback since {}", order.getId(), order.getPlacedAt());
        }
    }
}

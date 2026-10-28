package br.com.rodrigodip.orders.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.rodrigodip.orders.client.dto.OrderShippedEvent;
import br.com.rodrigodip.orders.service.OrderShippingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderShippedSubscriber {

    private final OrderShippingService orderShippingService;

    @KafkaListener(
            topics = "${app.kafka.topics.shipped-orders}",
            containerFactory = "orderShippedKafkaListenerContainerFactory")
    public void onOrderShipped(OrderShippedEvent event) {
        log.info("Received 'order.shipped' event: orderId={}, trackingCode={}",
                event.orderId(), event.trackingCode());

        orderShippingService.markAsShipped(event.orderId(), event.trackingCode());
    }
}

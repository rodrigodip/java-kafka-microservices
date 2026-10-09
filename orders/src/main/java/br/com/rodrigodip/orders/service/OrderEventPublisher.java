package br.com.rodrigodip.orders.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import br.com.rodrigodip.orders.client.KafkaClientProperties;
import br.com.rodrigodip.orders.entity.Order;
import br.com.rodrigodip.orders.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderEventPublisher {

    private static final String PLACED_ORDERS = "placed-orders";
    private static final String PAID_ORDERS = "paid-orders";
    private static final String INVOICED_ORDERS = "invoiced-orders";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaClientProperties kafkaProperties;

    public void publishPlaced(Order order) {
        publish(PLACED_ORDERS, order);
    }

    public void publishPaid(Order order) {
        publish(PAID_ORDERS, order);
    }

    public void publishInvoiced(Order order) {
        publish(INVOICED_ORDERS, order);
    }

    private void publish(String topicKey, Order order) {
        String topic = kafkaProperties.getTopics().get(topicKey);
        if (topic == null) {
            throw new IllegalStateException("Kafka topic not configured for key: " + topicKey);
        }

        OrderEvent event = OrderEvent.from(order);
        kafkaTemplate.send(topic, String.valueOf(order.getId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish '{}' event for order {} to topic '{}'",
                                topicKey, order.getId(), topic, ex);
                    } else {
                        log.info("Published '{}' event for order {} to topic '{}'",
                                topicKey, order.getId(), topic);
                    }
                });
    }
}

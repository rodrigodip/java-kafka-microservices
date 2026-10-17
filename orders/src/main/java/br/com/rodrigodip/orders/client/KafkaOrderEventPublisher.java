package br.com.rodrigodip.orders.client;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import br.com.rodrigodip.orders.client.dto.OrderInvoicedEvent;
import br.com.rodrigodip.orders.client.dto.OrderPaidEvent;
import br.com.rodrigodip.orders.client.dto.OrderShippedEvent;
import br.com.rodrigodip.orders.entity.Order;
import br.com.rodrigodip.orders.service.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderEventPublisher implements OrderEventPublisher {

    private static final String PAID_ORDERS = "paid-orders";
    private static final String INVOICED_ORDERS = "invoiced-orders";
    private static final String SHIPPED_ORDERS = "shipped-orders";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaClientProperties kafkaProperties;
    private final OrderDataAssembler assembler;

    @Override
    public void publishPaid(Order order) {
        OrderPaidEvent event = assembler.assemblePaid(order);
        publish(PAID_ORDERS, String.valueOf(order.getId()), event);
    }

    @Override
    public void publishInvoiced(Order order) {
        OrderInvoicedEvent event = assembler.assembleInvoiced(order);
        publish(INVOICED_ORDERS, String.valueOf(order.getId()), event);
    }

    @Override
    public void publishShipped(Order order) {
        OrderShippedEvent event = assembler.assembleShipped(order);
        publish(SHIPPED_ORDERS, String.valueOf(order.getId()), event);
    }

    private void publish(String topicKey, String key, Object event) {
        String topic = kafkaProperties.getTopics().get(topicKey);
        if (topic == null) {
            throw new IllegalStateException("Kafka topic not configured for key: " + topicKey);
        }

        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish '{}' event to topic '{}'", topicKey, topic, ex);
                    } else {
                        log.info("Published '{}' event to topic '{}'", topicKey, topic);
                    }
                });
    }
}

package br.com.rodrigodip.orders.outbox;

import org.springframework.stereotype.Component;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import br.com.rodrigodip.orders.client.KafkaClientProperties;
import br.com.rodrigodip.orders.client.OrderDataAssembler;
import br.com.rodrigodip.orders.entity.Order;
import br.com.rodrigodip.orders.repository.OutboxRepository;
import br.com.rodrigodip.orders.service.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Writes order lifecycle events to the transactional outbox instead of
 * publishing straight to Kafka. The {@link OutboxRelay} drains the outbox in
 * the background, making the event durable atomically with the domain change.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderOutboxPublisher implements OrderEventPublisher {

    private static final String PAID_ORDERS = "paid-orders";
    private static final String INVOICED_ORDERS = "invoiced-orders";
    private static final String SHIPPED_ORDERS = "shipped-orders";

    private final OutboxRepository outboxRepository;
    private final KafkaClientProperties kafkaProperties;
    private final OrderDataAssembler assembler;
    private final ObjectMapper objectMapper;

    @Override
    public void publishPaid(Order order) {
        enqueue(PAID_ORDERS, order.getId(), assembler.assemblePaid(order));
    }

    @Override
    public void publishInvoiced(Order order) {
        enqueue(INVOICED_ORDERS, order.getId(), assembler.assembleInvoiced(order));
    }

    @Override
    public void publishShipped(Order order) {
        enqueue(SHIPPED_ORDERS, order.getId(), assembler.assembleShipped(order));
    }

    private void enqueue(String topicKey, Long orderId, Object event) {
        String topic = kafkaProperties.getTopics().get(topicKey);
        if (topic == null) {
            throw new IllegalStateException("Kafka topic not configured for key: " + topicKey);
        }

        try {
            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setTopic(topic);
            outboxEvent.setAggregateId(String.valueOf(orderId));
            outboxEvent.setPayload(objectMapper.writeValueAsString(event));

            outboxRepository.save(outboxEvent);
            log.info("Enqueued '{}' event to outbox for order {} (topic '{}')", topicKey, orderId, topic);
        } catch (JacksonException e) {
            throw new IllegalStateException("Failed to serialize '" + topicKey + "' event for order " + orderId, e);
        }
    }
}

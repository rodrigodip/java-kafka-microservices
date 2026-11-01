package br.com.rodrigodip.logistics.outbox;

import org.springframework.stereotype.Component;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import br.com.rodrigodip.logistics.dto.OrderShippedEvent;
import br.com.rodrigodip.logistics.dto.mapper.ShipmentMapper;
import br.com.rodrigodip.logistics.entity.Shipment;
import br.com.rodrigodip.logistics.messaging.KafkaProperties;
import br.com.rodrigodip.logistics.repository.OutboxRepository;
import br.com.rodrigodip.logistics.service.ShipmentEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Writes the order.shipped event to the transactional outbox instead of
 * publishing straight to Kafka. The {@link OutboxRelay} drains the outbox in
 * the background, making the event durable atomically with the shipment update.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShipmentOutboxPublisher implements ShipmentEventPublisher {

    private static final String TOPIC_KEY = "shipped-orders";

    private final OutboxRepository outboxRepository;
    private final KafkaProperties kafkaProperties;
    private final ShipmentMapper shipmentMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void publishShipped(Shipment shipment) {
        OrderShippedEvent event = shipmentMapper.toShippedEvent(shipment);

        String topic = kafkaProperties.getTopics().get(TOPIC_KEY);
        if (topic == null) {
            throw new IllegalStateException("Kafka topic not configured for key: " + TOPIC_KEY);
        }

        try {
            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setTopic(topic);
            outboxEvent.setAggregateId(String.valueOf(event.orderId()));
            outboxEvent.setPayload(objectMapper.writeValueAsString(event));

            outboxRepository.save(outboxEvent);
            log.info("Enqueued 'order.shipped' event to outbox for order {} (topic '{}')", event.orderId(), topic);
        } catch (JacksonException e) {
            throw new IllegalStateException(
                    "Failed to serialize order.shipped event for order " + event.orderId(), e);
        }
    }
}

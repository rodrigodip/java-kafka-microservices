package br.com.rodrigodip.logistics.messaging;

import java.util.concurrent.ExecutionException;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import br.com.rodrigodip.logistics.dto.OrderShippedEvent;
import br.com.rodrigodip.logistics.dto.mapper.ShipmentMapper;
import br.com.rodrigodip.logistics.entity.Shipment;
import br.com.rodrigodip.logistics.service.ShipmentEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaShipmentEventPublisher implements ShipmentEventPublisher {

    private static final String TOPIC_KEY = "shipped-orders";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties properties;
    private final ShipmentMapper shipmentMapper;

    @Override
    public void publishShipped(Shipment shipment) {
        OrderShippedEvent event = shipmentMapper.toShippedEvent(shipment);

        String topic = properties.getTopics().get(TOPIC_KEY);
        if (topic == null) {
            throw new IllegalStateException("Kafka topic not configured for key: " + TOPIC_KEY);
        }

        try {
            kafkaTemplate.send(topic, String.valueOf(event.orderId()), event).get();
            log.info("Published 'order.shipped' event to topic '{}' for order {}", topic, event.orderId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while publishing order.shipped event", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(
                    "Failed to publish order.shipped event for order " + event.orderId(), e.getCause());
        }
    }
}

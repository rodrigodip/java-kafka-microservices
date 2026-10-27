package br.com.rodrigodip.invoicing.messaging;

import java.util.concurrent.ExecutionException;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import br.com.rodrigodip.invoicing.dto.InvoiceGeneratedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceGeneratedPublisher {

    private static final String TOPIC_KEY = "invoice-generated";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties properties;

    public void publish(InvoiceGeneratedEvent event) {
        String topic = properties.getTopics().get(TOPIC_KEY);
        if (topic == null) {
            throw new IllegalStateException("Kafka topic not configured for key: " + TOPIC_KEY);
        }

        try {
            kafkaTemplate.send(topic, String.valueOf(event.orderId()), event).get();
            log.info("Published '{}' event to topic '{}' for order {}", TOPIC_KEY, topic, event.orderId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while publishing invoice.generated event", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException(
                    "Failed to publish invoice.generated event for order " + event.orderId(), e.getCause());
        }
    }
}

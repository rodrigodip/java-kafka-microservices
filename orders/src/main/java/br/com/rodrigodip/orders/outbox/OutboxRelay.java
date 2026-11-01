package br.com.rodrigodip.orders.outbox;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.rodrigodip.orders.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Polls the outbox for pending events and forwards them to Kafka. Publication
 * is at-least-once: a crash between the Kafka send and the outbox update simply
 * republishes the row on the next poll, which consumers handle idempotently.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxRelay {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelayString = "${app.outbox.poll-interval-ms:1000}")
    public void publishPending() {
        List<OutboxEvent> pending = outboxRepository.findTop100ByPublishedAtIsNullOrderByIdAsc();
        if (pending.isEmpty()) {
            return;
        }

        for (OutboxEvent event : pending) {
            try {
                kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload())
                        .get(10, TimeUnit.SECONDS);

                event.markPublished();
                outboxRepository.save(event);

                log.info("Published outbox event id={} to topic '{}'", event.getId(), event.getTopic());
            } catch (Exception ex) {
                event.recordFailure();
                outboxRepository.save(event);

                log.error("Failed to publish outbox event id={} to topic '{}' (attempts={})",
                        event.getId(), event.getTopic(), event.getAttempts(), ex);
            }
        }
    }
}

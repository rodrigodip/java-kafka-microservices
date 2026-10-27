package br.com.rodrigodip.orders.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.rodrigodip.orders.client.dto.InvoiceGeneratedEvent;
import br.com.rodrigodip.orders.service.OrderEventPublisher;
import br.com.rodrigodip.orders.service.OrderInvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InvoiceGeneratedSubscriber {

    private final OrderInvoiceService orderInvoiceService;
    private final OrderEventPublisher orderEventPublisher;

    @KafkaListener(topics = "${app.kafka.topics.invoice-generated}")
    public void onInvoiceGenerated(InvoiceGeneratedEvent event) {
        log.info("Received 'invoice.generated' event: orderId={}, invoiceKey={}",
                event.orderId(), event.invoiceKey());

        orderInvoiceService.markAsInvoiced(event.orderId(), event.invoiceUrl())
                .ifPresent(orderEventPublisher::publishInvoiced);
    }
}

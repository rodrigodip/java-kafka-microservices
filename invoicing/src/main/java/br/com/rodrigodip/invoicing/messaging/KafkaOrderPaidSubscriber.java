package br.com.rodrigodip.invoicing.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.rodrigodip.invoicing.dto.InvoiceRepresentation;
import br.com.rodrigodip.invoicing.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderPaidSubscriber {

    private final InvoiceService invoiceService;

    @KafkaListener(topics = "${app.kafka.topics.paid-orders}")
    public void onOrderPaid(InvoiceRepresentation event) {
        log.info("Received 'paid-orders' event: orderId={}, clientId={}, total={}",
                event.orderId(), event.clientId(), event.total());

        invoiceService.processPaidOrder(event);
    }
}

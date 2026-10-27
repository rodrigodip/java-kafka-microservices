package br.com.rodrigodip.logistics.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import br.com.rodrigodip.logistics.dto.OrderInvoicedEvent;
import br.com.rodrigodip.logistics.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderInvoicedSubscriber {

    private final ShipmentService shipmentService;

    @KafkaListener(topics = "${app.kafka.topics.invoiced-orders}")
    public void onOrderInvoiced(OrderInvoicedEvent event) {
        log.info("Received 'order.invoiced' event: orderId={}, clientId={}, total={}",
                event.orderId(), event.clientId(), event.total());

        shipmentService.registerInvoicedOrder(event);
    }
}

package br.com.rodrigodip.invoicing.service;

import org.springframework.stereotype.Service;

import br.com.rodrigodip.invoicing.dto.InvoiceRepresentation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    public void processPaidOrder(InvoiceRepresentation event) {
        log.info("Processing paid order {} for client {} (invoice generation pending)",
                event.orderId(), event.clientId());
    }
}

package br.com.rodrigodip.invoicing.service;

import org.springframework.stereotype.Service;

import br.com.rodrigodip.invoicing.PdfRender.OpenPdfInvoiceRenderer;
import br.com.rodrigodip.invoicing.bucket.BucketService;
import br.com.rodrigodip.invoicing.config.InvoicingProperties;
import br.com.rodrigodip.invoicing.dto.InvoiceGeneratedEvent;
import br.com.rodrigodip.invoicing.dto.InvoiceRepresentation;
import br.com.rodrigodip.invoicing.messaging.InvoiceGeneratedPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final BucketService bucketService;
    private final OpenPdfInvoiceRenderer renderer;
    private final InvoiceGeneratedPublisher invoiceGeneratedPublisher;
    private final InvoicingProperties properties;

    public void processPaidOrder(InvoiceRepresentation event) {
        String invoiceKey = InvoiceStorageKey.forOrder(event.orderId());
        byte[] pdf = renderer.render(event);

        bucketService.upload(invoiceKey, pdf);

        String invoiceUrl = buildInvoiceUrl(event.orderId());
        invoiceGeneratedPublisher.publish(new InvoiceGeneratedEvent(event.orderId(), invoiceKey, invoiceUrl));

        log.info("Invoice generated and uploaded for order {} (key={}, url={})",
                event.orderId(), invoiceKey, invoiceUrl);
    }

    private String buildInvoiceUrl(Long orderId) {
        String baseUrl = properties.getPublicBaseUrl();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        return baseUrl + InvoiceStorageKey.downloadPath(orderId);
    }
}

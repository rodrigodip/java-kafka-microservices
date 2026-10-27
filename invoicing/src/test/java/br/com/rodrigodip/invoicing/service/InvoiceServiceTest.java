package br.com.rodrigodip.invoicing.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.rodrigodip.invoicing.PdfRender.OpenPdfInvoiceRenderer;
import br.com.rodrigodip.invoicing.bucket.BucketService;
import br.com.rodrigodip.invoicing.config.InvoicingProperties;
import br.com.rodrigodip.invoicing.dto.InvoiceGeneratedEvent;
import br.com.rodrigodip.invoicing.dto.InvoiceRepresentation;
import br.com.rodrigodip.invoicing.messaging.InvoiceGeneratedPublisher;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private BucketService bucketService;

    @Mock
    private OpenPdfInvoiceRenderer renderer;

    @Mock
    private InvoiceGeneratedPublisher publisher;

    private InvoiceService invoiceService;

    @BeforeEach
    void setUp() {
        InvoicingProperties properties = new InvoicingProperties();
        properties.setPublicBaseUrl("http://invoicing:8084/");

        invoiceService = new InvoiceService(bucketService, renderer, publisher, properties);
    }

    @Test
    void uploadsWithDeterministicKeyAndPublishesStableDownloadUrl() {
        InvoiceRepresentation event = event();
        byte[] pdf = new byte[] { 1, 2, 3 };
        when(renderer.render(event)).thenReturn(pdf);

        invoiceService.processPaidOrder(event);

        verify(bucketService).upload("invoices/42.pdf", pdf);

        ArgumentCaptor<InvoiceGeneratedEvent> captor = ArgumentCaptor.forClass(InvoiceGeneratedEvent.class);
        verify(publisher).publish(captor.capture());

        InvoiceGeneratedEvent published = captor.getValue();
        assertThat(published.orderId()).isEqualTo(42L);
        assertThat(published.invoiceKey()).isEqualTo("invoices/42.pdf");
        assertThat(published.invoiceUrl()).isEqualTo("http://invoicing:8084/invoices/42/download");
    }

    private InvoiceRepresentation event() {
        return new InvoiceRepresentation(
                42L, 1L, "Rodrigo", null, null, null,
                null, null, null, "PAID", BigDecimal.TEN, List.of());
    }
}

package br.com.rodrigodip.invoicing.PdfRender;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.rodrigodip.invoicing.dto.InvoiceRepresentation;

class OpenPdfInvoiceRendererTest {

    private final OpenPdfInvoiceRenderer renderer = new OpenPdfInvoiceRenderer();

    @Test
    void rendersAValidPdfWithOrderData() {
        InvoiceRepresentation event = invoice();

        byte[] pdf = renderer.render(event);
        String content = new String(pdf, StandardCharsets.ISO_8859_1);

        assertThat(pdf).isNotEmpty();
        assertThat(content).startsWith("%PDF-");
        assertThat(content).contains("OpenPDF");
    }

    @Test
    void rendersWhenOptionalFieldsAreMissing() {
        InvoiceRepresentation event = new InvoiceRepresentation(
                3L, 1L, null, null, null, null,
                null, null, null, "PAID", null, null);

        byte[] pdf = renderer.render(event);

        assertThat(pdf).isNotEmpty();
        assertThat(new String(pdf, StandardCharsets.ISO_8859_1)).startsWith("%PDF-");
    }

    private InvoiceRepresentation invoice() {
        return new InvoiceRepresentation(
                3L,
                1L,
                "Rodrigo",
                "10056731795",
                "rod@gmail.com",
                "31992614515",
                "R. Jequitai",
                "165",
                "230455",
                "PAID",
                new BigDecimal("1600.00"),
                List.of(new InvoiceRepresentation.Item(1L, "Notebook", 1, new BigDecimal("1200.00")),
                        new InvoiceRepresentation.Item(2L, "Telefone", 2, new BigDecimal("200.00"))));
    }
}
